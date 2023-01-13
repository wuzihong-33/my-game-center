package com.mygame.gateway.server.hander;


import com.mygame.common.cloud.PlayerServiceInstance;
import com.mygame.common.utils.JWTUtil;
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
import org.springframework.kafka.core.KafkaTemplate;

/**
 * 负责将pipeline收到的业务数据包(填充上fromServerId和toServerId)，然后转发到对应的topic上
 */
public class DispatchGameMessageHandler extends ChannelInboundHandlerAdapter {
    private PlayerServiceInstance playerServiceInstance;// 注入业务服务管理类，从这里获取负载均衡的服务器信息
    private GatewayServerConfig gatewayServerConfig; // 注入游戏网关服务配置信息。
    private JWTUtil.TokenBody tokenBody;
    private KafkaTemplate<String, byte[]> kafkaTemplate;
    private static Logger logger = LoggerFactory.getLogger(DispatchGameMessageHandler.class);

    public DispatchGameMessageHandler(KafkaTemplate<String, byte[]> kafkaTemplate,PlayerServiceInstance playerServiceInstance, GatewayServerConfig gatewayServerConfig) {
        this.playerServiceInstance = playerServiceInstance;
        this.gatewayServerConfig = gatewayServerConfig;
        this.kafkaTemplate = kafkaTemplate;
    }
    
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        GameMessagePackage gameMessagePackage = (GameMessagePackage) msg;
        int serviceId = gameMessagePackage.getHeader().getServiceId();
        // ???? pipeline 里边的handler每个都是新对象？？？？
        if (tokenBody == null) {// 如果首次通信，获取验证信息
            ConfirmHandler confirmHandler = (ConfirmHandler) ctx.channel().pipeline().get("ConfirmHandler");
            tokenBody = confirmHandler.getTokenBody();
        }
        
    }
    public static void dispatchMessage(KafkaTemplate<String, byte[]> kafkaTemplate,
                                           EventExecutor executor,
                                           PlayerServiceInstance playerServiceInstance,
                                           long playerId,
                                           int serviceId,
                                           String clientIp,GameMessagePackage gameMessagePackage,
                                           GatewayServerConfig gatewayServerConfig) {

        Promise<Integer> promise = new DefaultPromise<>(executor);
        playerServiceInstance.selectServerId(playerId, serviceId, promise).addListener(new GenericFutureListener<Future<Integer>>() {
            @Override
            public void operationComplete(Future<Integer> future) throws Exception {
                if (future.isSuccess()) {
                    Integer toServerId = future.get();
                    gameMessagePackage.getHeader().setToServerId(toServerId);
                    // 网关只能有1个？？？？？
                    gameMessagePackage.getHeader().setFromServerId(gatewayServerConfig.getServerId());
                    gameMessagePackage.getHeader().getAttribute().setClientIp(clientIp);
                    gameMessagePackage.getHeader().setPlayerId(playerId);
                    String topic = TopicUtil.generateTopic(gatewayServerConfig.getBusinessGameMessageTopic(), toServerId);
                    byte[] value = GameMessageInnerDecoder.sendMessage(gameMessagePackage);
                    ProducerRecord<String, byte[]> record = new ProducerRecord<String, byte[]>(topic, String.valueOf(playerId), value);
                    kafkaTemplate.send(record);
                    logger.debug("发送到{}消息成功->{}",gameMessagePackage.getHeader());
                } else {
                    logger.error("消息发送失败",future.cause());
                }
            }
        });
    }
}