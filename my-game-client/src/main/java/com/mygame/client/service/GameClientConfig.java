package com.mygame.client.service;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "game.client.config")
public class GameClientConfig {
    /**
     * 客户端处理数据的线程数。
     */
    private int workThreads = 16;
    /**
     * 连接超时时间，单位秒
     */
    private int connectTimeout = 10;
    /**
     * 默认提供的游戏网关地址:localhost
     */
    private String defaultGameGatewayHost = "localhost";
    /**
     * 网关认证需要的token
     */
    private String gatewayToken = "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiIxNjc1MTgxMTIwMzIyIiwiaWF0IjoxNjc1MTgxMTIwLCJzdWIiOiJ7XCJvcGVuSWRcIjpcIjAxMjM0NVwiLFwicGFyYW1zXCI6W1wiXCIsXCJNSUdmTUEwR0NTcUdTSWIzRFFFQkFRVUFBNEdOQURDQmlRS0JnUUNXVkJWcHlvL2NBRitOTEkyZVJ3NU1sWkZyam5lczNGek9rNVY3dTg1RWs3QTdZbk5JR0Roc3BYVFhOMUVWYkxYbVBNa1RkbUptTlY4TkQ1cmMwNCtvUFF5bElRUFlsbnRlTXpjeStmRCt6bjBpbW1mb2pFcVkvVHFnd2NkdlBUU1diWEtjWW9NdVhYWjJpWnVtQkozbHNwQ09MRnhxSmM3YnI2ZURLTm5DRlFJREFRQUJcIl0sXCJwbGF5ZXJJZFwiOjAsXCJzZXJ2ZXJJZFwiOlwiLTFcIixcInVzZXJJZFwiOjJ9In0.WDexIf0M5Z9c0iCncOs39PyYWqpKE1HvyEpRyvvkxNw";
    
    /**
     * 默认提供的游戏网关的端口:6003
     */
    private int defaultGameGatewayPort = 6003;
    /**
     * 是否使用服务中心
     * true表示从服务中心获取游戏网关信息；false使用默认游戏网关
     */
    private boolean useGameCenter;
    /**
     * 游戏服务中心地址
     */
    private String gameCenterUrl = "http://localhost:5003";
    /**
     * 启动消息压缩的阈值，单位byte
     */
    private int messageCompressThreshold = 1024 * 2;
    /**
     * 客户端加密rsa私钥
     */
    private String rsaPrivateKey;
    /**
     * 客户端版本
     */
    private int version;

    public int getWorkThreads() {
        return workThreads;
    }

    public void setWorkThreads(int workThreads) {
        this.workThreads = workThreads;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public String getDefaultGameGatewayHost() {
        return defaultGameGatewayHost;
    }

    public void setDefaultGameGatewayHost(String defaultGameGatewayHost) {
        this.defaultGameGatewayHost = defaultGameGatewayHost;
    }

    public String getGatewayToken() {
        return gatewayToken;
    }

    public void setGatewayToken(String gatewayToken) {
        this.gatewayToken = gatewayToken;
    }

    public int getDefaultGameGatewayPort() {
        return defaultGameGatewayPort;
    }

    public void setDefaultGameGatewayPort(int defaultGameGatewayPort) {
        this.defaultGameGatewayPort = defaultGameGatewayPort;
    }

    public boolean isUseGameCenter() {
        return useGameCenter;
    }

    public void setUseGameCenter(boolean useGameCenter) {
        this.useGameCenter = useGameCenter;
    }

    public String getGameCenterUrl() {
        return gameCenterUrl;
    }

    public void setGameCenterUrl(String gameCenterUrl) {
        this.gameCenterUrl = gameCenterUrl;
    }

    public int getMessageCompressThreshold() {
        return messageCompressThreshold;
    }

    public void setMessageCompressThreshold(int messageCompressThreshold) {
        this.messageCompressThreshold = messageCompressThreshold;
    }

    public String getRsaPrivateKey() {
        return rsaPrivateKey;
    }

    public void setRsaPrivateKey(String rsaPrivateKey) {
        this.rsaPrivateKey = rsaPrivateKey;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }
}
