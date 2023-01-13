package com.mygame.gateway.server;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service //测试的时候打开注释
public class KafkaBusTest {
    @Autowired
    private KafkaTemplate<String, byte[]> kafkaTemplate;

    @PostConstruct
    public void init() {
        String str = "你好，kafka";
        ProducerRecord<String, byte[]> record = new ProducerRecord<String, byte[]>("KafkaTestTopic", "hello", str.getBytes());
        kafkaTemplate.send(record);
    }
    
    @KafkaListener(topics = {"KafkaTestTopic"}, groupId = "my-game")
    public void receiver(ConsumerRecord<String, byte[]> record) {
        byte[] body = record.value();
        String value = new String(body);
        System.out.println("收到kafka的消息：" + value);
    }
}
