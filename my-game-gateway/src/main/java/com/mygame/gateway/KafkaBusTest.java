package com.mygame.gateway;

import com.mygame.gateway.server.handler.HeartbeatHandler;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

//@Service //测试的时候打开注释
public class KafkaBusTest {
    private Logger logger = LoggerFactory.getLogger(KafkaBusTest.class);
    @Autowired
    private KafkaTemplate<String, byte[]> kafkaTemplate;

    @PostConstruct
    public void init() {
        String topic = "KafkaTestTopic";
        String str = "你好，kafka";
        ProducerRecord<String, byte[]> record = new ProducerRecord<String, byte[]>(topic, "hello", str.getBytes());
        kafkaTemplate.send(record);
    }
    
    @KafkaListener(topics = {"KafkaTestTopic"}, groupId = "my-game")
    public void receiver(ConsumerRecord<String, byte[]> record) {
        byte[] body = record.value();
        String value = new String(body);
        logger.info("收到kafka的消息: {}" + value);
        System.out.println("收到kafka的消息：" + value);
    }
}
