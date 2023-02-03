package com.mygame.gateway.message.channel;

import com.mygame.common.cloud.GameChannelCloseEvent;
import com.mygame.common.concurrent.GameEventExecutorGroup;
import com.mygame.game.common.IGameMessage;
import com.mygame.gateway.message.rpc.GameRpcService;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Promise;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.util.HashMap;
import java.util.Map;


/**
 * GameChannel服务类，（最外一层）提供统一的channel操作，并传递到实际的channel里边去处理
 * 相当于一个代理类
 */
public class GameChannelService {
    private static Logger logger = LoggerFactory.getLogger(GameChannelService.class);
    private Map<Long, GameChannel> gameChannelGroup = new HashMap<>();// 管理PlayerId与GameChannel的集合

    private GameEventExecutorGroup workerGroup;// 业务处理线程池组
    private EventExecutor executor;// 当前管理gameChannelGroup集合的事件线程池
    private IMessageSendFactory messageSendFactory; // 向客户端发送消息的接口类，可以根据需求，有不同的实现，这里默认是发送到kafka的消息总线服务中。
    private GameChannelInitializer channelInitializer;
    private GameRpcService gameRpcSendFactory;
    private ApplicationContext context;

    public GameChannelService(ApplicationContext context, GameEventExecutorGroup workerGroup, IMessageSendFactory messageSendFactory, GameChannelInitializer channelInitializer, GameRpcService gameRpcSendFactory) {
        this.context = context;
        this.workerGroup = workerGroup;
        this.executor = workerGroup.next();
        this.messageSendFactory = messageSendFactory;
        this.channelInitializer = channelInitializer;
        this.gameRpcSendFactory = gameRpcSendFactory;
    }

    public ApplicationContext getApplicationContext() {
        return context;
    }

    /**
     * 触发通道读业务请求
     * @param playerId
     * @param message
     */
    public void fireReadMessage(Long playerId, IGameMessage message) {
        this.safeExecute(() -> {
            GameChannel gameChannel = this.getGameChannel(playerId);
            gameChannel.fireReadGameMessage(message);
        });
    }

    /**
     * 触发通道读rpc请求
     * @param gameMessage
     */
    public void fireReadRPCRequest(IGameMessage gameMessage) {
        this.safeExecute(() -> {
            GameChannel gameChannel = this.getGameChannel(gameMessage.getHeader().getPlayerId());
            gameChannel.fireChannelReadRPCRequest(gameMessage);
        });
    }

    /**
     * 触发通道读用户定义的事件
     * @param playerId
     * @param msg
     * @param promise
     */
    public void fireUserEvent(Long playerId, Object msg, Promise<Object> promise) {
        this.safeExecute(() -> {
            GameChannel gameChannel = this.getGameChannel(playerId);
            gameChannel.fireUserEvent(msg, promise);
        });
    }

    
    // 发送GameChannel失效的事件，在这个事件中可以处理一些数据落地的操作
    public void fireInactiveChannel(Long playerId) {
        this.safeExecute(() -> {
            GameChannel gameChannel = this.gameChannelGroup.remove(playerId);
            if (gameChannel != null) {
                gameChannel.fireChannelInactive();
                // 发布GameChannel失效事件
                GameChannelCloseEvent event = new GameChannelCloseEvent(this, playerId);
                context.publishEvent(event);
            }
        });
    }

    public void broadcastMessage(IGameMessage gameMessage, long... playerIds) {// 发送消息广播事件，客多个客户端发送消息。
        if (playerIds == null || playerIds.length == 0) {
            logger.debug("广播的对象集合为空，直接返回");
            return;
        }
        this.safeExecute(() -> {
            for (long playerId : playerIds) {
                if (this.gameChannelGroup.containsKey(playerId)) {
                    GameChannel gameChannel = this.getGameChannel(playerId);
                    gameChannel.pushMessage(gameMessage);
                }
            }
        });
    }

    
    public void broadcastMessage(IGameMessage gameMessage) {
        this.safeExecute(() -> {
            this.gameChannelGroup.values().forEach(channel -> {
                channel.pushMessage(gameMessage);
            });
        });
    }

    private GameChannel getGameChannel(Long playerId) {
        GameChannel gameChannel = this.gameChannelGroup.get(playerId);
        if (gameChannel == null) {// 第一次发送消息请求，创建并注册该GameChannel
            gameChannel = new GameChannel(playerId, this, messageSendFactory, gameRpcSendFactory);
            this.gameChannelGroup.put(playerId, gameChannel);
            this.channelInitializer.initChannel(gameChannel);// 初始化Channel
            gameChannel.register(workerGroup.select(playerId), playerId);// 发注册GameChannel的事件。
        }
        return gameChannel;
    }

    private void safeExecute(Runnable task) {
        if (this.executor.inEventLoop()) {// 如果当前调用这个方法的线程和此类所属的线程是同一个线程，则可以立刻执行执行。
            try {
                task.run();
            } catch (Throwable e) {
                logger.error("服务器内部错误", e);
            }
        } else {
            this.executor.execute(() -> {// 如果当前调用这个方法的线程和此类所属的线程不是同一个线程，将此任务提交到线程池中等待执行。
                try {
                    task.run();
                } catch (Throwable e) {
                    logger.error("服务器内部错误", e);
                }
            });
        }
    }

}
