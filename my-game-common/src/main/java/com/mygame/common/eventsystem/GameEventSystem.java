package com.mygame.common.eventsystem;

import org.springframework.context.ApplicationContext;

/**
 * 包装事件系统的操作，提供静态方法，方便调用
 */
public class GameEventSystem {
    private static GameEventDispatchAnnotationManager gameEventDispatchAnnotationManager = new GameEventDispatchAnnotationManager();
    
    //在服务启动的时候，调用此方法，初始化系统中的事件监听
    public static void start(ApplicationContext context) {
        gameEventDispatchAnnotationManager.init(context);
    }
    
    /**
     * 发送事件
     * @param origin 事件发生源。即，调用sendGameEvent的类实例
     * @param gameEventMessage 事件内容对象
     */
    public static void sendGameEvent(Object origin, IGameEventMessage gameEventMessage) {
        gameEventDispatchAnnotationManager.sendGameEvent(origin, gameEventMessage);
    }
}
