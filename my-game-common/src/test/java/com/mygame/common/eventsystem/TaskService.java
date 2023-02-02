package com.mygame.common.eventsystem;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.EventListener;

@GameEventService
public class TaskService implements ApplicationListener<SpringBootEvent>{
    
    @Override
    public void onApplicationEvent(SpringBootEvent event) {
        System.out.println("收到springboot事件:" + event.getClass().getName());
    }
    
    @GameEventListener(PlayerUpgradeLevelEvent.class)
    public void playerUpgradeEvent(Object origin,PlayerUpgradeLevelEvent event) {
        System.out.println("EnumMessageType：" + event.getClass().getName());
        //在这里处理相应的业务逻辑。
    }
    
    
    @EventListener
    public void springBootEvent(SpringBootEvent event) {
        System.out.println("注解1收到事件：" + event.getClass().getName());
    }


}
