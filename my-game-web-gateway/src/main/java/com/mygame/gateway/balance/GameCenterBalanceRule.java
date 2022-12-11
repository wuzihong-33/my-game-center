package com.mygame.gateway.balance;

import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.AbstractLoadBalancerRule;
import com.netflix.loadbalancer.Server;
import org.apache.commons.lang.math.RandomUtils;

import java.util.List;

public class GameCenterBalanceRule extends AbstractLoadBalancerRule {
    @Override
    public void initWithNiwsConfig(IClientConfig iClientConfig) {
    }

    private Server hashKeyChoose(List<Server> servers, Object key) {
        int hashCode = Math.abs(key.hashCode());
        int index = hashCode % servers.size();
        return servers.get(index);
    }

    private Server randomChoose(List<Server> servers) {
        int randomIndex = RandomUtils.nextInt(servers.size());
        return servers.get(randomIndex);
    }
    
    @Override
    public Server choose(Object key) {
        List<Server> servers = this.getLoadBalancer().getReachableServers();
        if (servers.isEmpty()) {
            return null;
        }
        if (servers.size() == 1) {
            return servers.get(0);
        }
        if (key == null) {
            return randomChoose(servers);
        }
        return hashKeyChoose(servers, key);
    }
}
