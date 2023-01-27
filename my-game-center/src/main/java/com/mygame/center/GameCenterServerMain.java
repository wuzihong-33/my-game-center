package com.mygame.center;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * 游戏服务中心
 */
@EnableMongoRepositories(basePackages= {"com.mygame"})
@SpringBootApplication(scanBasePackages = {"com.mygame"})
@EnableDiscoveryClient
public class GameCenterServerMain {
    public static void main(String[] args) {
        SpringApplication.run(GameCenterServerMain.class, args);
    }
}
