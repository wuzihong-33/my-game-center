package com.mygame.common.cloud;

import io.netty.util.concurrent.DefaultEventExecutor;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Promise;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 缓存用户调用的服务对应的服务器id
 * 缓存playerId -> (serviceId->serverId)
 * 
 */
public class PlayerServiceInstance implements ApplicationListener<GameChannelCloseEvent> {
    // key: playerId；value: map(serviceId -> serverId)
    private Map<Long, Map<Integer, Integer>> serviceInstanceMap = new ConcurrentHashMap<>();
    @Autowired
    private BusinessServerService businessServerService;
    @Autowired
    private StringRedisTemplate redisTemplate;

    private EventExecutor eventExecutor = new DefaultEventExecutor();// 创建一个事件线程，操作redis的时候，使用异步

    public Set<Integer> getAllServiceId(){
        return businessServerService.getAllServiceId();
    }

    private void addLocalCache(long playerId, int serviceId, int serverId) {
        Map<Integer, Integer> instanceMap = this.serviceInstanceMap.get(playerId);
        instanceMap.put(serviceId, serverId);
    }
    private String getRedisKey(Long playerId) {
        return "service_instance_" + playerId;
    }

    
    /**
     * 首先从本地缓存中找
     * 如果没有，其次从redis中找（看看是否由别的服务计算好了）
     * 最后，如果Redis中没有缓存，或实例已失效，重新调用BusinessServerService获取一个新的服务实例Id
     * 
     * @param playerId
     * @param serviceId
     * @param promise
     * @return
     */
    public Promise<Integer> selectServerId(Long playerId, int serviceId, Promise<Integer> promise) {
        Map<Integer, Integer> serviceInstance = serviceInstanceMap.get(playerId);
        Integer serverId = null;
        if (serviceInstance != null) {
            serverId = serviceInstance.get(serviceId);
        } else {
            serviceInstance = new ConcurrentHashMap<>();
            serviceInstanceMap.put(playerId, serviceInstance);
        }
        
        if (serverId != null) {
            if (businessServerService.isEnableServer(serviceId, serverId)) {
                promise.setSuccess(serverId);
            } else {
                serverId = null; // 使其无效
            }
        }
        if (serverId == null) {
            eventExecutor.execute(()-> {
                String key = this.getRedisKey(playerId);// 从redis查找一下，是否已由别的服务计算好
                Object value = redisTemplate.opsForHash().get(key, String.valueOf(serviceId));
                boolean isEnableServer = true;
                if (value != null) {
                    int serverIdOfRedis = Integer.parseInt((String) value);
                    isEnableServer = businessServerService.isEnableServer(serviceId, serverIdOfRedis);
                    if (isEnableServer) {// 如果redis中已缓存且是有效的服务实例serverId，直接返回
                        promise.setSuccess(serverIdOfRedis);
                        this.addLocalCache(playerId, serviceId, serverIdOfRedis);
                    }
                }
                // 如果Redis中没有缓存，或实例已失效，重新获取一个新的服务实例Id
                if (value == null || !isEnableServer) {
                    Integer serverId2 = this.selectServerIdAndSaveRedis(playerId, serviceId);
                    this.addLocalCache(playerId, serviceId, serverId2);
                    promise.setSuccess(serverId2);
                }
            });
        }
        return promise;
    }
    
    private Integer selectServerIdAndSaveRedis(Long playerId, Integer serviceId) {
        Integer serverId = businessServerService.selectServerInfo(serviceId, playerId).getServerId();
        this.eventExecutor.execute(() -> {
            try {
                String key = this.getRedisKey(playerId);
                // K, HK, HV
                this.redisTemplate.opsForHash().put(key, String.valueOf(serviceId), String.valueOf(serverId));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        return serverId;
    }


    @Override
    public void onApplicationEvent(GameChannelCloseEvent event) {
        this.serviceInstanceMap.remove(event.getPlayerId());
    }
}
