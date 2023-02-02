package com.mygame.common.eventsystem;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

/**
 * 维护事件监听器（单例）
 */
public class GameEventDispatchAnnotationManager {
    private Logger logger = LoggerFactory.getLogger(GameEventDispatchAnnotationManager.class);
    private Map<String, List<GameEventListenerMapping>> gameEventMapping = new HashMap<>();

    /**
     * 1、通过applicationContext遍历标注了@GameEventService的类
     * 2、获取类中标注了@GameEventListener的方法，@GameEventListener的属性值表明了此方法所监听的事件
     * 3、建立事件名 -> GameEventListenerMapping(bean,method)的映射
     * @param context
     */
    public void init(ApplicationContext context) {
       context.getBeansWithAnnotation(GameEventService.class).values().forEach(bean->{
           Method[] methods = bean.getClass().getMethods();
           for(Method method : methods) {
               GameEventListener gameEventListener = method.getAnnotation(GameEventListener.class);
               if(gameEventListener != null) {
                   Class<? extends IGameEventMessage> eventClass = gameEventListener.value();
                   GameEventListenerMapping gameEventListenerMapping = new GameEventListenerMapping(bean, method);
                   this.addGameEventListenerMapping(eventClass.getName(), gameEventListenerMapping);
               }
           }
       });
    }

    public void sendGameEvent(Object origin, IGameEventMessage gameEventMessage) {
        String key = gameEventMessage.getClass().getName();
        List<GameEventListenerMapping> gameEventListenerMappings = this.gameEventMapping.get(key);
        if(gameEventListenerMappings != null) {
            gameEventListenerMappings.forEach(c->{
                try {
                    c.getMethod().invoke(c.getBean(), origin, gameEventMessage);//通过反射调用事件处理方法
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                    logger.error("事件发送失败",e);
                    throw new IllegalArgumentException("事件发送失败", e);
                }
            });
        }
    }
    
    private void addGameEventListenerMapping(String key,GameEventListenerMapping gameEventListenerMapping) {
        List<GameEventListenerMapping> gameEventListenerMappings = this.gameEventMapping.get(key);
        if(gameEventListenerMappings == null) {
            gameEventListenerMappings = new ArrayList<>();
            this.gameEventMapping.put(key, gameEventListenerMappings);
        }
        gameEventListenerMappings.add(gameEventListenerMapping);
    }
    
}
