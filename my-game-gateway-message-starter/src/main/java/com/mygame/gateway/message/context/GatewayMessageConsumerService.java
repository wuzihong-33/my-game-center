package com.mygame.gateway.message.context;

import com.mygame.common.cloud.PlayerServiceInstance;
import com.mygame.common.concurrent.GameEventExecutorGroup;
import com.mygame.game.GameMessageService;
import com.mygame.game.bus.GameMessageInnerDecoder;
import com.mygame.game.common.EnumMessageType;
import com.mygame.game.common.GameMessageHeader;
import com.mygame.game.common.GameMessagePackage;
import com.mygame.game.common.IGameMessage;
import com.mygame.gateway.message.channel.GameChannelConfig;
import com.mygame.gateway.message.channel.GameChannelInitializer;
import com.mygame.gateway.message.channel.GameChannelService;
import com.mygame.gateway.message.channel.IMessageSendFactory;
import com.mygame.gateway.message.rpc.GameRpcService;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutorGroup;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * 监听kafka相应的topic，触发GameChannel通道读写
 */
@Service
public class GatewayMessageConsumerService {
    private IMessageSendFactory gameGatewayMessageSendFactory;// 默认实现的消息发送接口，GameChannel返回的消息通过此接口发送到kafka中
    @Autowired
    private GameChannelConfig serverConfig;// GameChannel的一些配置信息
    @Autowired
    private GameMessageService gameMessageService; // 消息管理类，负责管理根据消息id，获取对应的消息类实例
    @Autowired
    private KafkaTemplate<String, byte[]> kafkaTemplate;
    @Autowired
    private PlayerServiceInstance playerServiceInstance;
    @Autowired
    private ApplicationContext context;
    
    private GameChannelService gameChannelService;
    private GameEventExecutorGroup workerGroup;// 业务处理的线程池
    private EventExecutorGroup rpcWorkerGroup = new DefaultEventExecutorGroup(2);
    private GameRpcService gameRpcSendFactory;
    private Logger logger = LoggerFactory.getLogger(GatewayMessageConsumerService.class);
    
    public void setMessageSendFactory(IMessageSendFactory messageSendFactory) {
        this.gameGatewayMessageSendFactory = messageSendFactory;
    }
    public GameChannelService getGameMessageEventDispatchService() {
        return this.gameChannelService;
    }
    
    public void start(GameChannelInitializer gameChannelInitializer, int localServerId) {
        workerGroup = new GameEventExecutorGroup(serverConfig.getWorkerThreads());
        gameGatewayMessageSendFactory = new GameGatewayMessageSendFactory(kafkaTemplate, serverConfig.getGatewayGameMessageTopic());
        gameRpcSendFactory = new GameRpcService(serverConfig.getRpcRequestGameMessageTopic(),serverConfig.getRpcResponseGameMessageTopic(),localServerId, playerServiceInstance, rpcWorkerGroup, kafkaTemplate);
        gameChannelService = new GameChannelService(context,workerGroup, gameGatewayMessageSendFactory, gameChannelInitializer, gameRpcSendFactory);
    }

    
    
    @KafkaListener(topics = {"${game.channel.business-game-message-topic}" + "-" + "${game.server.config.server-id}"}, groupId = "${game.channel.topic-group-id}")
    public void consume(ConsumerRecord<String, byte[]> record) {
        IGameMessage gameMessage = this.getGameMessage(EnumMessageType.REQUEST, record.value());
        logger.debug("topic 收到消息：{}, 触发通道读", gameMessage);
        gameChannelService.fireReadMessage(gameMessage.getHeader().getPlayerId(), gameMessage);
    }
    

//    @KafkaListener(topics = {"${game.channel.rpc-request-game-message-topic}" + "-" + "${game.server.config.server-id}"}, groupId = "rpc-${game.channel.topic-group-id}")
//    public void consumeRPCRequestMessage(ConsumerRecord<String, byte[]> record) {
//        IGameMessage gameMessage = this.getGameMessage(EnumMessageType.RPC_REQUEST, record.value());
//        gameChannelService.fireReadRPCRequest(gameMessage);
//    }

//    @KafkaListener(topics = {"${game.channel.rpc-response-game-message-topic}" + "-" + "${game.server.config.server-id}"}, groupId = "rpc-request-${game.channel.topic-group-id}")
//    public void consumeRPCResponseMessage(ConsumerRecord<String, byte[]> record) {
//        IGameMessage gameMessage = this.getGameMessage(EnumMessageType.RPC_RESPONSE, record.value());
//        this.gameRpcSendFactory.recieveResponse(gameMessage);
//    }

    /**
     * 将通用GameMessagePackage转换成具体的IGameMessage
     * @param messageType
     * @param data
     * @return
     */
    private IGameMessage getGameMessage(EnumMessageType messageType, byte[] data) {
        GameMessagePackage gameMessagePackage = GameMessageInnerDecoder.readGameMessagePackage(data);
        GameMessageHeader header = gameMessagePackage.getHeader();
        IGameMessage gameMessage = gameMessageService.getMessageInstance(messageType, header.getMessageId());
        gameMessage.read(gameMessagePackage.getBody());
        gameMessage.setHeader(header);
        gameMessage.getHeader().setMessageType(messageType);
        return gameMessage;
    }
}
