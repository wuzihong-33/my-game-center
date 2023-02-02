package com.mygame.common.cloud;

import com.mygame.common.model.ServerInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.discovery.event.HeartbeatEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * 维护serviceId对应的服务器列表，提供根据 (serviceId+playerId) 获取业务服务器的接口
 * 只负责提供负载均衡接口，不负责缓存记忆
 */
@Service
public class BusinessServerService implements ApplicationListener<HeartbeatEvent> {
    private Logger logger = LoggerFactory.getLogger(BusinessServerService.class);
    @Autowired
    private DiscoveryClient discoveryClient;
    // serviceId -> List(serverId)
    private Map<Integer, List<ServerInfo>> serverInfos;

    @PostConstruct
    public void init() {
        this.refreshBusinessServerInfo();
    }

    public Set<Integer> getAllServiceId(){
        return serverInfos.keySet();
    }
    
    // TODO: 如果在refresh列表时发生get操作？
    // 刷新业务服务列表
    private void refreshBusinessServerInfo() {
        Map<Integer, List<ServerInfo>> tempServerInfoMap = new HashMap<>();
        List<ServiceInstance> businessServiceInstances = discoveryClient.getInstances("game-logic");
        logger.debug("抓取游戏服务配置成功,{}", businessServiceInstances);
        businessServiceInstances.forEach(instance -> {
            int weight = this.getServerInfoWeight(instance);
            for (int i = 0; i < weight; i++) {
                ServerInfo serverInfo = this.newServerInfo(instance);
                List<ServerInfo> serverList = tempServerInfoMap.get(serverInfo.getServiceId());
                if (serverList == null) {
                    serverList = new ArrayList<>();
                    tempServerInfoMap.put(serverInfo.getServiceId(), serverList);
                }
                serverList.add(serverInfo);
            }
        });
        this.serverInfos = tempServerInfoMap;
    }
    
    private int getServerInfoWeight(ServiceInstance instance) {
        String value = instance.getMetadata().get("weight");
        if (value == null) {
            value = "1";
        }
        return Integer.parseInt(value);
    }


    /**
     * 从游戏网关列表中选择一个游戏服务实例信息返回
     * @param serviceId
     * @param playerId
     * @return
     */
    public ServerInfo selectServerInfo(Integer serviceId,Long playerId) {
        Map<Integer, List<ServerInfo>> serverInfoMap = this.serverInfos;
        List<ServerInfo> serverList = serverInfoMap.get(serviceId);
        // 防止游戏网关列表发生变化，导致数据不一致。
        if (serverList == null || serverList.size() == 0) {
            return null;
        }
        int hashCode = Math.abs(playerId.hashCode());
        int gatewayCount = serverList.size();
        int index = hashCode % gatewayCount;
        if (index >= gatewayCount) {
            index = gatewayCount - 1;
        }
        return serverList.get(index);
    }


    /**
     * 判断某个服务中的serverId是否还有效
     * @param serviceId
     * @param serverId
     * @return
     */
    public boolean isEnableServer(Integer serviceId,Integer serverId) {
        Map<Integer, List<ServerInfo>> serverInfoMap = this.serverInfos;
        List<ServerInfo> serverInfoList = serverInfoMap.get(serviceId);
        if(serverInfoList != null) {
            return serverInfoList.stream().anyMatch(c->{
                return c.getServerId() == serverId;
            });
        }
        return false;
    }
    
    
    private ServerInfo newServerInfo(ServiceInstance instance) {
        String serviceId = instance.getMetadata().get("serviceId");
        String serverId =  instance.getMetadata().get("serverId");
        if (StringUtils.isEmpty(serviceId)) {
            throw new IllegalArgumentException(instance.getHost() + "的服务未配置serviceId");
        }

        if (StringUtils.isEmpty(serverId)) {
            throw new IllegalArgumentException(instance.getHost() + "的服务未配置serverId");
        }
        ServerInfo serverInfo = new ServerInfo();
        serverInfo.setServiceId(Integer.parseInt(serviceId));
        serverInfo.setServerId(Integer.parseInt(serverId));
        serverInfo.setHost(instance.getHost());
        serverInfo.setPort(instance.getPort());

        return serverInfo;
    }
    
    
    @Override
    public void onApplicationEvent(HeartbeatEvent event) {
        refreshBusinessServerInfo();
    }
}
