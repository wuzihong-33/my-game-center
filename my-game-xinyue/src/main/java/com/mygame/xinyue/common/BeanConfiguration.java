package com.mygame.xinyue.common;

import javax.annotation.PostConstruct;

import com.mygame.gateway.message.context.ServerConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.mygame.common.concurrent.GameEventExecutorGroup;
import com.mygame.dao.AsyncPlayerDao;
import com.mygame.dao.PlayerDao;

@Configuration
public class BeanConfiguration {
    @Autowired
    private ServerConfig serverConfig;

    private GameEventExecutorGroup dbExecutorGroup;

    @Autowired
    private PlayerDao playerDao;
    
    @PostConstruct
    public void init() {
        dbExecutorGroup = new GameEventExecutorGroup(serverConfig.getDbThreads());
    }
//
    //启动时创建主题
    @Bean
    public NewTopic messageCenter() {
        return new NewTopic("business-game-message-topic-10101", 1, (short) 1);
    }
    
    @Bean
    public AsyncPlayerDao asyncPlayerDao() {//配置AsyncPlayerDao的Bean
        return new AsyncPlayerDao(dbExecutorGroup, playerDao);
    }
}
