package com.mygame.gateway.message.context;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import com.mygame.common.utils.TopicUtil;
import com.mygame.game.bus.GameMessageInnerDecoder;
import com.mygame.game.common.GameMessagePackage;
import com.mygame.gateway.message.channel.GameChannelPromise;
import com.mygame.gateway.message.channel.IMessageSendFactory;

/**
 * 封装请求的发送
 */
public class GameGatewayMessageSendFactory implements IMessageSendFactory {
    private String topic;
    private KafkaTemplate<String, byte[]> kafkaTemplate;

    public GameGatewayMessageSendFactory(KafkaTemplate<String, byte[]> kafkaTemplate, String topic) {
        this.topic = topic;
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void sendMessage(GameMessagePackage gameMessagePackage, GameChannelPromise promise) {
        
        int toServerId = gameMessagePackage.getHeader().getToServerId();
        long playerId = gameMessagePackage.getHeader().getPlayerId();
        // 动态创建游戏网关监听消息的topic
        String sendTopic = TopicUtil.generateTopic(topic,toServerId);
        byte[] value = GameMessageInnerDecoder.sendMessage(gameMessagePackage);
        ProducerRecord<String, byte[]> record = new ProducerRecord<String, byte[]>(sendTopic, String.valueOf(playerId), value);
        kafkaTemplate.send(record);
        promise.setSuccess();
    }


}
