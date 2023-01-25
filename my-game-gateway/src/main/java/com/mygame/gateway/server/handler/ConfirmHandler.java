package com.mygame.gateway.server.handler;

import java.util.Set;
import java.util.concurrent.TimeUnit;
import com.mygame.common.utils.AESUtils;
import com.mygame.common.utils.NettyUtils;
import com.mygame.common.utils.RSAUtils;
import com.mygame.error.GameGatewayError;
import com.mygame.game.message.GatewayMessageCode;
import com.mygame.gateway.server.handler.codec.DecodeHandler;
import com.mygame.message.ConfirmMsgRequest;
import com.mygame.message.ConfirmMsgResponse;
import com.mygame.message.ConnectionStatusMsgRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.util.Base64Utils;
import org.springframework.util.StringUtils;
import com.mygame.common.cloud.PlayerServiceInstance;
import com.mygame.common.utils.JWTUtil;
import com.mygame.common.utils.JWTUtil.TokenBody;
import com.mygame.game.common.GameMessagePackage;
import com.mygame.gateway.server.ChannelService;
import com.mygame.gateway.server.GatewayServerConfig;
import com.mygame.gateway.server.handler.codec.EncodeHandler;
import io.jsonwebtoken.ExpiredJwtException;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.ScheduledFuture;

public class ConfirmHandler extends ChannelInboundHandlerAdapter {
    @Autowired
    private PlayerServiceInstance businessServerService;// 用于获取负载均衡的服务器信息
    @Autowired
    private GatewayServerConfig serverConfig;
    @Autowired
    private ChannelService channelService;
    @Autowired
    private KafkaTemplate<String, byte[]> kafkaTemplate;// TODO: 将来可以将消息的发送封装到自己的类里面，这样就可以屏蔽底层消息队列的使用了
    
    private boolean confirmSuccess = false;// 标记连接是否认证成功
    private ScheduledFuture<?> future;// 定时器的返回值
    private TokenBody tokenBody; // 客户端携带过来的
    private static Logger logger = LoggerFactory.getLogger(ConfirmHandler.class);

    public TokenBody getTokenBody() {
        return tokenBody;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {// 此方法会在连接建立成功channel注册之后调用
        logger.debug("客户端 {} 连接成功，channelId:{}", NettyUtils.getRemoteIP(ctx.channel()), ctx.channel().id().asShortText());
        int delay = serverConfig.getWaiteConfirmTimeoutSecond();// 从配置中获取延迟时间
        future = ctx.channel().eventLoop().schedule(() -> {
            if (!confirmSuccess) {// 如果没有认证成功，则关闭连接。
                logger.debug("连接认证超时，断开连接，channelId:{}", ctx.channel().id().asShortText());
                ctx.close();
            }
        }, delay, TimeUnit.SECONDS);
        ctx.fireChannelActive();
    }


    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if (future != null) {
            future.cancel(true);// 取消定时任务
        }
        if (tokenBody != null) { // 连接断开之后，移除连接
            long playerId = tokenBody.getPlayerId();
            this.channelService.removeChannel(playerId, ctx.channel());// 调用移除，否则出现内存泄漏的问题。
        }
        ctx.fireChannelInactive();// 接着告诉下面的Handler
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        GameMessagePackage gameMessagePackage = (GameMessagePackage) msg;
        int messageId = gameMessagePackage.getHeader().getMessageId();
        if (messageId == GatewayMessageCode.ConnectConfirm.getMessageId()) {
            ConfirmMsgRequest request = new ConfirmMsgRequest();
            request.read(gameMessagePackage.getBody());// 反序列化消息内容
            String token = request.getBodyObj().getToken();
            ConfirmMsgResponse response = new ConfirmMsgResponse();
            if (StringUtils.isEmpty(token)) {// 检测token
                logger.error("token为空，关闭连接");
                ctx.close();
            } else {
                try {
                    tokenBody = JWTUtil.getTokenBody(token);// 解析token里面的内容，如果解析失败，会抛出异常
                    this.confirmSuccess = true;// 标记认证成功
                    this.repeatedConnect();// 检测重复连接
                    channelService.addChannel(tokenBody.getPlayerId(), ctx.channel());// 加入连接管理
                    String aesSecretKey = AESUtils.createSecret(tokenBody.getUserId(), tokenBody.getServerId());// 生成此连接的AES密钥
                    // 将对称加密密钥分别设置到编码和解码的handler中。
                    DecodeHandler decodeHandler = ctx.channel().pipeline().get(DecodeHandler.class);
                    decodeHandler.setAesSecret(aesSecretKey);
                    EncodeHandler encodeHandler = ctx.channel().pipeline().get(EncodeHandler.class);
                    encodeHandler.setAesSecret(aesSecretKey);
                    byte[] clientPublicKey = this.getClientRsaPublicKey();
                    byte[] encryptAesKey = RSAUtils.encryptByPublicKey(aesSecretKey.getBytes(), clientPublicKey);// 使用客户端的公钥加密对称加密密钥
                    response.getBodyObj().setSecretKey(Base64Utils.encodeToString(encryptAesKey));// 返回给客户端
                    GameMessagePackage returnPackage = new GameMessagePackage();
                    returnPackage.setHeader(response.getHeader());
                    returnPackage.setBody(response.body());
                    ctx.writeAndFlush(returnPackage);

                    // 通知各个服务，某个用户连接成功
                    String ip = NettyUtils.getRemoteIP(ctx.channel());
                    this.sendConnectStatusMsg(true, ctx.executor(), ip);
                } catch (Exception e) {
                    if (e instanceof ExpiredJwtException) {// 告诉客户端token过期，它客户端重新获取并重新连接
                        response.getHeader().setErrorCode(GameGatewayError.TOKEN_EXPIRE.getErrorCode());
                        ctx.writeAndFlush(response);
                        ctx.close();
                        logger.warn("token过期，关闭连接");
                    } else {
                        logger.error("token解析异常，直接关闭连接", e);
                        ctx.close();
                    }
                }
            }
        } else {
            if (!confirmSuccess) {
                logger.trace("连接未认证，不处理任务消息，关闭连接，channelId:{}", ctx.channel().id().asShortText());
                ctx.close();
                return;
            }
            ctx.fireChannelRead(msg);// 如果不是认证消息，则向下发送消息，让后面的Handler去处理，如果不下发，后面的Handler将接收不到消息。
        }
    }

    private void sendConnectStatusMsg(boolean connect, EventExecutor executor, String clientIp) {
        ConnectionStatusMsgRequest request = new ConnectionStatusMsgRequest();
        request.getBodyObj().setConnect(connect);
        long playerId = tokenBody.getPlayerId();
        Set<Integer> allServiceId = businessServerService.getAllServiceId();
        for (Integer serviceId : allServiceId) {
            //通知所有的服务，用户的连接状态
            GameMessagePackage gameMessagePackage = new GameMessagePackage();
            gameMessagePackage.setBody(request.body());
            gameMessagePackage.setHeader(request.getHeader());
            DispatchGameMessageHandler.dispatchMessage(kafkaTemplate, executor, businessServerService, playerId, serviceId, clientIp, gameMessagePackage, serverConfig);
        }
    }

    // 从token中获取客户端的公钥
    private byte[] getClientRsaPublicKey() {
        String publicKey = tokenBody.getParams()[1];// 获取客户端的公钥字符串。
        return Base64Utils.decodeFromString(publicKey);
    }

    /**
     * 重复连接检测
     */
    private void repeatedConnect() {
        if (tokenBody != null) {
            Channel existChannel = this.channelService.getChannel(tokenBody.getPlayerId());
            if (existChannel != null) {
                ConfirmMsgResponse response = new ConfirmMsgResponse();
                response.getHeader().setErrorCode(GameGatewayError.REPEATED_CONNECT.getErrorCode());
                GameMessagePackage returnPackage = new GameMessagePackage();
                returnPackage.setHeader(response.getHeader());
                returnPackage.setBody(response.body());
                existChannel.writeAndFlush(returnPackage);// 返回一条异地登录信息给客户端
                // 关闭旧连接，保留新连接
                existChannel.close();
            }
        }
    }
}
