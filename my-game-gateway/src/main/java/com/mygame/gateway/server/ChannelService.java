package com.mygame.gateway.server;

import io.netty.channel.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.BiConsumer;

/**
 * 维护playerId -> channel的映射（此处的channel是netty的channel，非IGameChannel）
 */
@Service
public class ChannelService {
    private Logger logger = LoggerFactory.getLogger(ChannelService.class);
    // 疑惑？？为什么不直接使用ConcurrentHashMap 
    // 因为最终是由游戏服务器网关来和客户端交互！
    private Map<Long, Channel> playerChannelMap = new HashMap<>();
    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    public void addChannel(Long playerId, Channel channel) {
        this.writeLock(() -> {// 数据写入，添加写锁
            playerChannelMap.put(playerId, channel);
        });
    }
    public Channel getChannel(Long playerId) {
        lock.readLock().lock();
        try {
            Channel channel = this.playerChannelMap.get(playerId);
            return channel;
        } finally {
            lock.readLock().unlock();
        }
    }
    public void broadcast(BiConsumer<Long, Channel> consumer) {// 向Channel广播消息
        this.readLock(() -> {
            this.playerChannelMap.forEach(consumer);
        });
    }
    public void removeChannel(Long playerId, Channel removedChannel) {
        this.writeLock(() -> {
            Channel existChannel = playerChannelMap.get(playerId);
            if (existChannel != null && existChannel == removedChannel) {// 必须是同一个对象才可以移除
                playerChannelMap.remove(playerId);
                existChannel.close();
            }
        });
    }

    public int getChannelCount() {
        // 使用写锁而非读锁，防止其它线程在获取数量时使用写锁更新
        lock.writeLock().lock();
        try {
            int size = this.playerChannelMap.size();// 获取连锁的数量
            return size;
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    // 封装添加读锁，统一添加，防止写错
    private void readLock(Runnable task) {
        lock.readLock().lock();
        try {
            task.run();
        }catch (Exception e) {  //统一异常捕获
            logger.error("ChannelService读锁处理异常",e);
        }finally {
            lock.readLock().unlock();
        }
    }
    // 封装添加写锁，统一添加，防止写错
    private void writeLock(Runnable task) {
        // 阻塞点？？
        lock.writeLock().lock();
        try {
            task.run();
        }catch (Exception e) {  //统一异常捕获
            logger.error("ChannelService写锁处理异常",e);
        } finally {
            lock.writeLock().unlock();
        }
    }
}
    
   