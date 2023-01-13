package com.mygame.gateway.server;


import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "game.gateway.server.config")
public class GatewayServerConfig {
    private int serverId;
    private int port;
    private int bossThreadCount;
    private int workThreadCount;
    private long recBufSize;
    private long sendBufSize;
    // 达到压缩的消息最小大小
    private int compressMessageSize = 1024 * 2;
    //等待认证的超时时间
    private int waiteConfirmTimeoutSecond = 30;
    /**
     * 单个用户的限流请允许的每秒请求数量
     */
    private double requestPerSecond = 10;
    /**
     * 全局流量限制请允许每秒请求数量
     */
    private double globalRequestPerSecond=2000;
    /**
     * channel读取空闲时间
     */
    private int readerIdleTimeSeconds = 300;
    /**
     * channel写出空闲时间
     */
    private int writerIdleTimeSeconds = 12;
    /**
     * 读写空闲时间
     */
    private int allIdleTimeSeconds = 15;
    private String businessGameMessageTopic = "business-game-message-topic";
    private String gatewayGameMessageTopic = "gateway-game-message-topic";

    public int getServerId() {
        return serverId;
    }

    public void setServerId(int serverId) {
        this.serverId = serverId;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getBossThreadCount() {
        return bossThreadCount;
    }

    public void setBossThreadCount(int bossThreadCount) {
        this.bossThreadCount = bossThreadCount;
    }

    public int getWorkThreadCount() {
        return workThreadCount;
    }

    public void setWorkThreadCount(int workThreadCount) {
        this.workThreadCount = workThreadCount;
    }

    public long getRecBufSize() {
        return recBufSize;
    }

    public void setRecBufSize(long recBufSize) {
        this.recBufSize = recBufSize;
    }

    public long getSendBufSize() {
        return sendBufSize;
    }

    public void setSendBufSize(long sendBufSize) {
        this.sendBufSize = sendBufSize;
    }

    public int getCompressMessageSize() {
        return compressMessageSize;
    }

    public void setCompressMessageSize(int compressMessageSize) {
        this.compressMessageSize = compressMessageSize;
    }

    public int getWaiteConfirmTimeoutSecond() {
        return waiteConfirmTimeoutSecond;
    }

    public void setWaiteConfirmTimeoutSecond(int waiteConfirmTimeoutSecond) {
        this.waiteConfirmTimeoutSecond = waiteConfirmTimeoutSecond;
    }

    public double getRequestPerSecond() {
        return requestPerSecond;
    }

    public void setRequestPerSecond(double requestPerSecond) {
        this.requestPerSecond = requestPerSecond;
    }

    public double getGlobalRequestPerSecond() {
        return globalRequestPerSecond;
    }

    public void setGlobalRequestPerSecond(double globalRequestPerSecond) {
        this.globalRequestPerSecond = globalRequestPerSecond;
    }

    public int getReaderIdleTimeSeconds() {
        return readerIdleTimeSeconds;
    }

    public void setReaderIdleTimeSeconds(int readerIdleTimeSeconds) {
        this.readerIdleTimeSeconds = readerIdleTimeSeconds;
    }

    public int getWriterIdleTimeSeconds() {
        return writerIdleTimeSeconds;
    }

    public void setWriterIdleTimeSeconds(int writerIdleTimeSeconds) {
        this.writerIdleTimeSeconds = writerIdleTimeSeconds;
    }

    public int getAllIdleTimeSeconds() {
        return allIdleTimeSeconds;
    }

    public void setAllIdleTimeSeconds(int allIdleTimeSeconds) {
        this.allIdleTimeSeconds = allIdleTimeSeconds;
    }

    public String getBusinessGameMessageTopic() {
        return businessGameMessageTopic;
    }

    public void setBusinessGameMessageTopic(String businessGameMessageTopic) {
        this.businessGameMessageTopic = businessGameMessageTopic;
    }

    public String getGatewayGameMessageTopic() {
        return gatewayGameMessageTopic;
    }

    public void setGatewayGameMessageTopic(String gatewayGameMessageTopic) {
        this.gatewayGameMessageTopic = gatewayGameMessageTopic;
    }
}
