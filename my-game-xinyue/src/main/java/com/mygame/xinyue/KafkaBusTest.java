package com.mygame.xinyue;

import javax.annotation.PostConstruct;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service //测试的时候打开注释
public class KafkaBusTest {
    private Logger logger = LoggerFactory.getLogger(KafkaBusTest.class);
    @Autowired
    private KafkaTemplate<String, byte[]> kafkaTemplate;

    @PostConstruct
    public void init() {
        String str = "你好，kafka";
        // 如何创建主题？
        ProducerRecord<String, byte[]> record = new ProducerRecord<String, byte[]>("KafkaTestTopic", "hello", str.getBytes());
        kafkaTemplate.send(record);

//        game.channel.business-game-message-topic-101001
    }
    @KafkaListener(topics = {"KafkaTestTopic"}, groupId = "my-game")
    public void receiver(ConsumerRecord<String, byte[]> record) {
        byte[] body = record.value();
        String value = new String(body);
        logger.debug("收到kafka的消息：" + value);
    }
}
