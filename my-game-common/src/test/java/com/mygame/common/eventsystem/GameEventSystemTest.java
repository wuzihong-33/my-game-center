package com.mygame.common.eventsystem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.Test;


// 由于在my-game-common项目中没有springboot启动的main方法，因此，需要在单元测试的时候，在测试类上指定Bean的配置类
@ContextConfiguration(classes = {BeanConfig.class})
public class GameEventSystemTest extends AbstractTestNGSpringContextTests{
	@Autowired
	private ApplicationContext context;

    @Test
    public void sendGameEvent() {
        //先初始化事件系统,在直接项目中是在项目启动的时候调用
    	GameEventSystem.start(context);
    	PlayerUpgradeLevelEvent event = new PlayerUpgradeLevelEvent();
        event.setPlayerId(1);
        GameEventSystem.sendGameEvent(this, event);
        
//        PlayerUpgradeListener playerUpgradeListener = new PlayerUpgradeListener();
//        GameEventSystem.registerListener(PlayerUpgradeLevelEvent.class, playerUpgradeListener);
//        PlayerUpgradeLevelEvent event = new PlayerUpgradeLevelEvent();
//        event.setPlayerId(1);
//        GameEventSystem.sendGameEvent(this, event);
    }
    
//    @Test
//    public void annotionGameEvent() {
//    	//先初始化事件系统,在直接项目中是在项目启动的时候调用
//    	GameEventSystem.start(context);
//    	PlayerUpgradeLevelEvent event = new PlayerUpgradeLevelEvent();
//        event.setPlayerId(1);
//        GameEventSystem.sendGameEvent(this, event);
//    }
    
    
//    @Test
//    public void springBootPublish() {
//        SpringBootEvent event = new SpringBootEvent(this);//产生一个事件
//        event.setPlayerId(1);//设置事件信息
//        context.publishEvent(event);//发布事件
//        SpringBootEvent2 event2 = new SpringBootEvent2(this);
//        context.publishEvent(event2);
//    }
}
