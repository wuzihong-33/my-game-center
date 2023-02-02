package com.mygame.xinyue;

import com.mygame.dao.PlayerDao;
import com.mygame.gateway.message.handler.GameChannelIdleStateHandler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ApplicationContext;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import com.mygame.dao.AsyncPlayerDao;
import com.mygame.game.messagedispatcher.DispatchGameMessageService;
//import com.mygame.gateway.message.context.DispatchUserEventService;
import com.mygame.gateway.message.context.GatewayMessageConsumerService;
import com.mygame.gateway.message.context.ServerConfig;
//import com.mygame.gateway.message.handler.GameChannelIdleStateHandler;
import com.mygame.xinyue.common.GameBusinessMessageDispatchHandler;

@EnableDiscoveryClient
@SpringBootApplication(scanBasePackages = {"com.mygame"})
@EnableMongoRepositories(basePackages = {"com.mygame"}) // 负责连接数据库
public class XinyueGameServerMain {
    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(XinyueGameServerMain.class, args);
        ServerConfig serverConfig = context.getBean(ServerConfig.class);
        DispatchGameMessageService.scanGameMessages(context, serverConfig.getServiceId(), "com.mygame");
        GatewayMessageConsumerService gatewayMessageConsumerService = context.getBean(GatewayMessageConsumerService.class);
        AsyncPlayerDao playerDao = context.getBean(AsyncPlayerDao.class);
        DispatchGameMessageService dispatchGameMessageService= context.getBean(DispatchGameMessageService.class);
//        DispatchUserEventService dispatchUserEventService = context.getBean(DispatchUserEventService.class);

        // 启动网关消息监听，并初始化GameChannelHandler
        gatewayMessageConsumerService.start((gameChannel) -> {
            gameChannel.getChannelPipeline().addLast(new GameChannelIdleStateHandler(300, 300, 300));
            gameChannel.getChannelPipeline().addLast(new GameBusinessMessageDispatchHandler(context,serverConfig,dispatchGameMessageService,null, playerDao));
        }, serverConfig.getServerId());
        
    }

}
