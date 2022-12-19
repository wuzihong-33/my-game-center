package com.mygame.center;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

/**
 * 游戏服务中心
 */
//@SpringBootApplication(scanBasePackages= {"com.mygame"})
@EnableMongoRepositories(basePackages= {"com.mygame"})
@SpringBootApplication(scanBasePackages = {"com.mygame"})
@EnableDiscoveryClient
public class WebGameCenterServerMain {
    public static void main(String[] args) {
        try {
            SpringApplication.run(WebGameCenterServerMain.class, args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
