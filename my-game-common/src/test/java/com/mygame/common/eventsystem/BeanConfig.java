package com.mygame.common.eventsystem;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class BeanConfig {
	@Bean
	public TaskService getTaskService() {
		return new TaskService();
	}

	@Bean
	public PlayerUpgradeService getPlayerUpgradeService() {
		return new PlayerUpgradeService();
	}
	
	
}
