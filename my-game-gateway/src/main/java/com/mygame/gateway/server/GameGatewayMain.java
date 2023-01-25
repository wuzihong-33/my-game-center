package com.mygame.gateway.server;

import java.util.concurrent.TimeUnit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import com.mygame.game.messagedispatcher.DispatchGameMessageService;
import com.mygame.gateway.server.GatewayServerBoot;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.EventExecutorGroup;

@SpringBootApplication(scanBasePackages = { "com.mygame" })
public class GameGatewayMain {
	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(GameGatewayMain.class, args);
		DispatchGameMessageService.scanGameMessages(context, 1, "com.mygame");
		GatewayServerBoot serverBoot = context.getBean(GatewayServerBoot.class);
		serverBoot.startServer();
	}

	public void test() {
		EventExecutorGroup executorGroup = new DefaultEventExecutorGroup(3);// 创建一个事件线程池组
		EventExecutor executor = executorGroup.next();// 从线程池组中获取一个线程池，它是递增获取的，比如第一次是第一个，第二次是第二个，依赖类推，如果索引到数组长度，则从头开始返回
		executorGroup.shutdownGracefully();//使用默认的参数关闭线程池组中的所有线程池
		executorGroup.shutdownGracefully(10, 120, TimeUnit.SECONDS);//使用指定的参数关闭线程池组中的所有线程池
		executorGroup.isShuttingDown();//判断线程池是否在关闭中。在调用shutdownGracefully方法之后，就会返回true
		executorGroup.isTerminated();//调用shutdownGracefully方法之后，且所有任务都执行完毕返回true
	}
}
