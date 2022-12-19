package com.mygame.center.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.PostConstruct;

import com.mygame.common.error.GameErrorException;
import com.mygame.error.GameCenterError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.event.HeartbeatEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Service;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mygame.center.dataconfig.GameGatewayInfo;

/**
 * 负责维护游戏服务网关信息：检测网关、网关分配
 */
@Service
public class GameGatewayService implements ApplicationListener<HeartbeatEvent> {
    private Logger logger = LoggerFactory.getLogger(GameGatewayService.class);
    private List<GameGatewayInfo> gameGatewayInfoList; // 有效的网关列表
    @Autowired
    private DiscoveryClient discoveryClient; // 服务发现客户端实例
    private LoadingCache<Long, GameGatewayInfo> userGameGatewayCache;// 用户分配到的网关缓存

    /**
     * 游戏服务中心启动之后，向Consul获取注册的游戏网关信息
     */
    @PostConstruct
    public void init() {
        this.refreshGameGatewayInfo();
        // 初始化用户分配的游戏网关信息缓存。
        userGameGatewayCache = CacheBuilder.newBuilder().maximumSize(20000).expireAfterAccess(2, TimeUnit.HOURS).build(new CacheLoader<Long, GameGatewayInfo>() {
            @Override
            public GameGatewayInfo load(Long key) throws Exception {
                GameGatewayInfo gameGatewayInfo = selectGameGateway(key);
                return gameGatewayInfo;
            }
        });
    }

    /**
     * 向客户端提供可以使用的游戏网关信息
     * @param playerId
     * @return
     * @throws ExecutionException
     */
    public  GameGatewayInfo getGameGatewayInfo(Long playerId) throws ExecutionException { 
        GameGatewayInfo gameGatewayInfo = userGameGatewayCache.get(playerId);
        if (gameGatewayInfo != null) {
            List<GameGatewayInfo> gameGatewayInfos = this.gameGatewayInfoList;
            // 检测缓存的网关是否还有效，如果已被移除，从缓存中删除，并重新分配一个游戏网关信息。
            if (!gameGatewayInfos.contains(gameGatewayInfo)) {
                userGameGatewayCache.invalidate(playerId);
                gameGatewayInfo = userGameGatewayCache.get(playerId);//这时，缓存中已不存在playerId对应的值，会重新初始化。
            }
        }
        return gameGatewayInfo;
    }
    
    
    private void refreshGameGatewayInfo() {
        List<ServiceInstance> gameGatewayServiceInstances = discoveryClient.getInstances("game-gateway");
        logger.debug("抓取游戏网关配置成功,{}", gameGatewayServiceInstances);
        List<GameGatewayInfo> initGameGatewayInfoList = new ArrayList<>();
        AtomicInteger gameGatewayId = new AtomicInteger(1);// Id自增
        gameGatewayServiceInstances.forEach(instance -> {
            int weight = this.getGameGatewayWeight(instance);
            for (int i = 0; i < weight; i++) {// 根据权重初始化游戏网关数量。
                int id = gameGatewayId.getAndIncrement();
                GameGatewayInfo gameGatewayInfo = this.newGameGatewayInfo(id, instance);// 构造游戏网关信息类
                initGameGatewayInfoList.add(gameGatewayInfo); // 网关实例和gameGatewayInfo不是一对一的关系；取决于weight大小
            }
        });
        Collections.shuffle(initGameGatewayInfoList);// 打乱一下顺序，让游戏网关分布更加均匀一些。
        this.gameGatewayInfoList = initGameGatewayInfoList;
    }
    @Override
    public void onApplicationEvent(HeartbeatEvent event) {
        this.refreshGameGatewayInfo();// 根据心跳事件，刷新游戏网关列表信息。
    }

    private GameGatewayInfo newGameGatewayInfo(int id, ServiceInstance instance) {
        GameGatewayInfo gameGatewayInfo = new GameGatewayInfo();
        gameGatewayInfo.setId(id);
        String ip = instance.getHost(); //网关服务注册的地址
        int port = this.getGameGatewayPort(instance);//网关中手动配置的长连接端口
//        int httpPort = instance.getPort(); //获取网关服务注册的http端口
        gameGatewayInfo.setIp(ip);
        gameGatewayInfo.setPort(port);
//        gameGatewayInfo.setHttpPort(httpPort);
        return gameGatewayInfo;
    }

    private int getGameGatewayPort(ServiceInstance instance) {
        String value = instance.getMetadata().get("gamePort");
        if (value == null) {
            logger.warn("游戏网关{}未配置长连接端口，使用默认端口6000", instance.getServiceId());
            value = "6000";
        }
        return Integer.parseInt(value);
    }

    private int getGameGatewayWeight(ServiceInstance instance) {
        String value = instance.getMetadata().get("weight");
        if (value == null) {
            value = "1";
    }
        return Integer.parseInt(value);
    }

    /**
     * 从当前有效的游戏网关列表中选择一个返回
     * @param playerId
     * @return
     */
    private GameGatewayInfo selectGameGateway(Long playerId) {
        // 需要先引用一份出来，防止游戏网关列表发生变化，导致数据不一致
        // 可能出现，一个线程正准备从列表中取值，而此时列表刷新了，列表长度可能发生变化
        List<GameGatewayInfo> temGameGatewayInfoList = this.gameGatewayInfoList;
        if (temGameGatewayInfoList == null || temGameGatewayInfoList.size() == 0) {
            throw GameErrorException.newBuilder(GameCenterError.NO_GAME_GATEWAY_INFO).build();
        }
        int hashCode = Math.abs(playerId.hashCode());
        int gatewayCount = temGameGatewayInfoList.size();
        int index = hashCode % gatewayCount;
        return temGameGatewayInfoList.get(index);
    }


}
