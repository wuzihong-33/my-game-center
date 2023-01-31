package com.mygame.game;

import com.mygame.game.common.AbstractGameMessage;
import com.mygame.game.common.EnumMessageType;
import com.mygame.game.common.GameMessageMetadata;
import com.mygame.game.common.IGameMessage;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 实现消息协议的自动序列化和反序列化
 */
@Service
public class GameMessageService {
    private Logger logger = LoggerFactory.getLogger(GameMessageService.class);
    private Map<String, Class<? extends IGameMessage>> gameMessageClassMap = new HashMap<>();

    /**
     * 建立 (MessageType:messageId) -> messageClass  映射关系
     */
    @PostConstruct
    public void init() {
        // 初始化的时候，将每个请求的响应的Message的class和messageId对应起来
        Reflections reflections = new Reflections("com.mygame");
        Set<Class<? extends AbstractGameMessage>> classSet = reflections.getSubTypesOf(AbstractGameMessage.class);
        classSet.forEach(clazz -> {
            GameMessageMetadata messageMetadata = clazz.getAnnotation(GameMessageMetadata.class);
            if (messageMetadata != null) {
                this.checkGameMessageMetadata(messageMetadata, clazz);
                int messageId = messageMetadata.messageId();
                EnumMessageType messageType = messageMetadata.messageType();
                String key = this.getMessageClassCacheKey(messageType, messageId);
                gameMessageClassMap.put(key, clazz);
            }
        });
        logger.error("建立 (MessageType:messageId) -> messageClass  映射关系{}", gameMessageClassMap.toString());        
    }
    private String getMessageClassCacheKey(EnumMessageType type, int messageId) {
        return messageId + ":" + type.name();
    }
    
    //获取响应数据包的实例
    public IGameMessage getResponseInstanceByMessageId(int messageId) {
        return this.getMessageInstance(EnumMessageType.RESPONSE, messageId);
    }
    //获取请求数据包的实例
    public IGameMessage getRequestInstanceByMessageId(int messageId) {
        return this.getMessageInstance(EnumMessageType.REQUEST, messageId);
    }
    
    
    //获取传数据反序列化的对象实例
    public  IGameMessage getMessageInstance(EnumMessageType messageType,int messageId) {
        String key = this.getMessageClassCacheKey(messageType, messageId);
        Class<? extends IGameMessage> clazz = this.gameMessageClassMap.get(key);
        if (clazz == null) {
            this.throwMetadataException("找不到messageId:" + key + "对应的响应数据对象Class");
        }
        IGameMessage gameMessage = null;
        try {
            gameMessage = clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            String msg = "实例化响应参数出现," + "messageId:" + key + ", class:" + clazz.getName();
            logger.error(msg, e);
            this.throwMetadataException(msg);
        }
        return gameMessage;
    }

    //检测数据对象的正确性
    private void checkGameMessageMetadata(GameMessageMetadata messageMetadata, Class<?> c) {
        int messageId = messageMetadata.messageId();
        if (messageId == 0) {
            this.throwMetadataException("messageId未设置:" + c.getName());
        }
        int serviceId = messageMetadata.serviceId();
        if (serviceId == 0) {
            this.throwMetadataException("serviceId未设置：" + c.getName());
        }
        EnumMessageType messageType = messageMetadata.messageType();
        if (messageType == null) {
            this.throwMetadataException("messageType未设置:" + c.getName());
        }
    }

    private void throwMetadataException(String msg) {
        throw new IllegalArgumentException(msg);
    }
}

