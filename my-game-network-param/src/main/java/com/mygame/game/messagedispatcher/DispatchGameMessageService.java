package com.mygame.game.messagedispatcher;


import com.mygame.game.common.GameMessageMetadata;
import com.mygame.game.common.IGameMessage;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 实现消息自动分发
 */
@Service
public class DispatchGameMessageService {
    private Logger logger = LoggerFactory.getLogger(DispatchGameMessageService.class);
    @Autowired
    private ApplicationContext applicationContext;
    // 消息类型String名称 -> DispatcherMapping（消息处理类，具体的某个消息处理方法）的映射
    private Map<String, DispatcherMapping> dispatcherMappingMap = new HashMap<>();

    /**
     * 服务id，如果为0,则加载所有的消息类型，如果不为零，则只加载此类型的消息。
     * @param applicationContext
     * @param serviceId
     * @param packagePath 需要扫描的handler的path
     */
    public static void scanGameMessages(ApplicationContext applicationContext, int serviceId, String packagePath) {// 构造一个方便的调用方法
        DispatchGameMessageService dispatchGameMessageService = applicationContext.getBean(DispatchGameMessageService.class);
        dispatchGameMessageService.scanGameMessages(serviceId, packagePath);
    }

    /**
     * 目的：建立起 消息类型String名称 -> DispatcherMapping（消息处理类，具体的某个消息处理方法）的映射
     * 
     * 步骤：
     * 1、拿到用于处理GameMessage的Handler类的Class实例
     * 2、使用上下文获取handler相对应的bean
     * 3、获取handler的所有方法；遍历所有方法
     * 4、获取方法上的GameMessageMapping注释的value的类A（表明此方法可以用来处理哪个消息类型）
     * 5、获取类A的GameMessageMetadata注释，
     */
    private void scanGameMessages(int serviceId, String packagePath) {
        Reflections reflection = new Reflections(packagePath);
        Set<Class<?>> allGameMessageHandlerClass = reflection.getTypesAnnotatedWith(GameMessageHandler.class);
        if (allGameMessageHandlerClass != null) {
            allGameMessageHandlerClass.forEach(classItem -> {
                Object targetObject = applicationContext.getBean(classItem);
                Method[] methods = classItem.getMethods();
                for (Method method : methods) {
                    GameMessageMapping gameMessageMapping = method.getAnnotation(GameMessageMapping.class);
                    if (gameMessageMapping != null) {
                        Class<?> gameMessageClass = gameMessageMapping.value();
                        GameMessageMetadata gameMessageMetadata = gameMessageClass.getAnnotation(GameMessageMetadata.class);
                        // 如果入参serviceId == 0，则加载所有的消息类型
                        // 如果入参serviceId为指定类型，如1001，则加载某个具体的消息类型
                        if (serviceId == 0 || gameMessageMetadata.serviceId() == serviceId) {
                            DispatcherMapping dispatcherMapping = new DispatcherMapping(targetObject, method);
                            this.dispatcherMappingMap.put(gameMessageClass.getName(), dispatcherMapping);
                        }
                    }
                }
            });
        }
        logger.info("DispatchGameMessageService消息自动分发设计, 维护:{}", dispatcherMappingMap.toString());
    }

    /**
     * 通过反射调用消息处理函数
     * @param gameMessage
     * @param ctx
     */
    public void callMethod(IGameMessage gameMessage, IGameChannelContext ctx) {
        String key = gameMessage.getClass().getName();
        DispatcherMapping dispatcherMapping = this.dispatcherMappingMap.get(key);
        if (dispatcherMapping != null) {
            Object obj = dispatcherMapping.getTargetObject();
            try {
                dispatcherMapping.getTargetMethod().invoke(obj, gameMessage, ctx);// 调用处理消息的方法
            } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                logger.error("调用方法异常，class: {}, method: {}, e: {}", obj.getClass().getName(), dispatcherMapping.getTargetMethod().getName(), e);
            }
        } else {
            logger.warn("未能找到消息对应的处理方法，消息名：{}", key);
        }
    }
}
