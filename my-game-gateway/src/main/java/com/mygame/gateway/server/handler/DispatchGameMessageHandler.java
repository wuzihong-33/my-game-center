package com.mygame.gateway.server.handler;


import com.mygame.common.cloud.PlayerServiceInstance;
import com.mygame.common.utils.JWTUtil;
import com.mygame.common.utils.NettyUtils;
import com.mygame.common.utils.TopicUtil;
import com.mygame.game.bus.GameMessageInnerDecoder;
import com.mygame.game.common.GameMessagePackage;
import com.mygame.gateway.server.GatewayServerConfig;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.concurrent.*;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;

/**
 * 负责将pipeline收到的业务数据包(填充上fromServerId和toServerId)，然后转发到对应的topic上
 * 这里的dispatch，区别于client的dispatch，client的dispatch是发送到对应的业务处理器中去处理业务，而这里的dispatch只负责转发
 */
public class DispatchGameMessageHandler extends ChannelInboundHandlerAdapter {
    private PlayerServiceInstance playerServiceInstance;// 服务管理类，从这里获取负载均衡的服务器信息
    private JWTUtil.TokenBody tokenBody;
    private KafkaTemplate<String, byte[]> kafkaTemplate;
    private GatewayServerConfig gatewayServerConfig; 
    private static Logger logger = LoggerFactory.getLogger(DispatchGameMessageHandler.class);

    public DispatchGameMessageHandler(KafkaTemplate<String, byte[]> kafkaTemplate,PlayerServiceInstance playerServiceInstance, GatewayServerConfig gatewayServerConfig) {
        this.playerServiceInstance = playerServiceInstance;
        this.gatewayServerConfig = gatewayServerConfig;
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        GameMessagePackage gameMessagePackage = (GameMessagePackage) msg;
        int serviceId = gameMessagePackage.getHeader().getServiceId();
        // ???? pipeline 里边的handler每个都是新对象？？？？使用handler来存储token会不会不太好。。。
        // TODO: 能否将此handler设计成通用的？
        if (tokenBody == null) {// 如果首次通信，获取验证信息？？为什么要验证token？仅仅是为了获取playerId？
            
            // 测试的时候将下边两行注释掉
//            ConfirmHandler confirmHandler = (ConfirmHandler) ctx.channel().pipeline().get("ConfirmHandler");
//            tokenBody = confirmHandler.getTokenBody();
        }
        String clientIp = NettyUtils.getRemoteIP(ctx.channel());
//        dispatchMessage(kafkaTemplate, ctx.executor(), playerServiceInstance, tokenBody.getPlayerId(), serviceId, clientIp, gameMessagePackage, gatewayServerConfig);
        dispatchMessage(kafkaTemplate, ctx.executor(), playerServiceInstance, 1, serviceId, clientIp, gameMessagePackage, gatewayServerConfig);
    }


    /**
     * 将客户端传来的GameMessage加上游戏网关的一些信息，转发到消息总线
     * @param kafkaTemplate
     * @param executor
     * @param playerServiceInstance
     * @param playerId
     * @param serviceId
     * @param clientIp
     * @param gameMessagePackage
     * @param gatewayServerConfig
     */
    public static void dispatchMessage(KafkaTemplate<String, byte[]> kafkaTemplate,
                                           EventExecutor executor,
                                           PlayerServiceInstance playerServiceInstance,
                                           long playerId,
                                           int serviceId,
                                           String clientIp,
                                           GameMessagePackage gameMessagePackage,
                                           GatewayServerConfig gatewayServerConfig) {

        Promise<Integer> promise = new DefaultPromise<>(executor);
        playerServiceInstance.selectServerId(playerId, serviceId, promise).addListener(new GenericFutureListener<Future<Integer>>() {
            @Override
            public void operationComplete(Future<Integer> future) throws Exception {
                if (future.isSuccess()) {
                    Integer toServerId = future.get();
                    gameMessagePackage.getHeader().setToServerId(toServerId);
                    gameMessagePackage.getHeader().setFromServerId(gatewayServerConfig.getServerId());
                    gameMessagePackage.getHeader().getAttribute().setClientIp(clientIp);
                    gameMessagePackage.getHeader().setPlayerId(playerId);
                    String topic = TopicUtil.generateTopic(gatewayServerConfig.getBusinessGameMessageTopic(), toServerId);
                    byte[] value = GameMessageInnerDecoder.sendMessage(gameMessagePackage);
                    ProducerRecord<String, byte[]> record = new ProducerRecord<String, byte[]>(topic, String.valueOf(playerId), value);
                    kafkaTemplate.send(record);
                    logger.debug("向topic: {} 写消息成功", topic);
                } else {
                    logger.error("获取serverId失败",future.cause());
                }
            }
        });
    }
}