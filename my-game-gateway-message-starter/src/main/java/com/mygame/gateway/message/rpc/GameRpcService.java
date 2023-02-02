package com.mygame.gateway.message.rpc;

import java.util.concurrent.atomic.AtomicInteger;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import com.mygame.common.cloud.PlayerServiceInstance;
import com.mygame.common.utils.TopicUtil;
import com.mygame.game.bus.GameMessageInnerDecoder;
import com.mygame.game.common.GameMessageHeader;
import com.mygame.game.common.GameMessagePackage;
import com.mygame.game.common.IGameMessage;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.EventExecutorGroup;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.Promise;


// 疑惑：为啥不把rpcService设置成单例？
public class GameRpcService {
    private AtomicInteger seqId = new AtomicInteger();// 自增的唯一序列Id
    private int localServerId;// 本地服务实例ID
    private PlayerServiceInstance playerServiceInstance;
    private EventExecutorGroup eventExecutorGroup;
    private KafkaTemplate<String, byte[]> kafkaTemplate;
    private static Logger logger = LoggerFactory.getLogger(GameRpcService.class);
    private GameRpcCallbackService gameRpcCallbackService;
    private String requestTopic;
    private String responseTopic;

    public GameRpcService(String requestTopic,String responseTopic,int localServerId, PlayerServiceInstance playerServiceInstance, EventExecutorGroup eventExecutorGroup, KafkaTemplate<String, byte[]> kafkaTemplate) {
        this.localServerId = localServerId;
        this.requestTopic = requestTopic;
        this.responseTopic = responseTopic;
        this.playerServiceInstance = playerServiceInstance;
        this.eventExecutorGroup = eventExecutorGroup;
        this.kafkaTemplate = kafkaTemplate;
        this.gameRpcCallbackService = new GameRpcCallbackService(eventExecutorGroup);
    }
    
    public void sendRPCResponse(IGameMessage gameMessage) {
        GameMessagePackage gameMessagePackage = new GameMessagePackage();
        gameMessagePackage.setHeader(gameMessage.getHeader());
        gameMessagePackage.setBody(gameMessage.body());
        String sendTopic = TopicUtil.generateTopic(responseTopic, gameMessage.getHeader().getToServerId());
        byte[] value = GameMessageInnerDecoder.sendMessage(gameMessagePackage);
        ProducerRecord<String, byte[]> record = new ProducerRecord<String, byte[]>(sendTopic, String.valueOf(gameMessage.getHeader().getPlayerId()), value);
        kafkaTemplate.send(record);
    }
    
    public void recieveResponse(IGameMessage gameMessage) {
        gameRpcCallbackService.callback(gameMessage);
    }

    public void sendRPCRequest(IGameMessage gameMessage, Promise<IGameMessage> promise) {
        GameMessagePackage gameMessagePackage = new GameMessagePackage();
        gameMessagePackage.setHeader(gameMessage.getHeader());
        gameMessagePackage.setBody(gameMessage.body());
        GameMessageHeader header = gameMessage.getHeader();
        header.setClientSeqId(seqId.incrementAndGet());
        header.setFromServerId(localServerId);
        header.setClientSendTime(System.currentTimeMillis());
        long playerId = header.getPlayerId();
        int serviceId = header.getServiceId();

        Promise<Integer> selectServerIdPromise = new DefaultPromise<>(this.eventExecutorGroup.next());
        playerServiceInstance.selectServerId(playerId, serviceId, selectServerIdPromise).addListener(new GenericFutureListener<Future<Integer>>() {
            @Override
            public void operationComplete(Future<Integer> future) throws Exception {
                if (future.isSuccess()) {
                    header.setToServerId(future.get());
                    // 动态创建游戏网关监听消息的topic
                    String sendTopic = TopicUtil.generateTopic(requestTopic, gameMessage.getHeader().getToServerId());
                    byte[] value = GameMessageInnerDecoder.sendMessage(gameMessagePackage);
                    ProducerRecord<String, byte[]> record = new ProducerRecord<String, byte[]>(sendTopic, String.valueOf(gameMessage.getHeader().getPlayerId()), value);
                    kafkaTemplate.send(record);
                    gameRpcCallbackService.addCallback(header.getClientSeqId(), promise);
                } else {
                    logger.error("获取目标服务实例ID出错",future.cause());
                }
            }
        });
    }

}
