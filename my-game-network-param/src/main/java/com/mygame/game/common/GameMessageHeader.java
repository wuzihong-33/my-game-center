package com.mygame.game.common;

public class GameMessageHeader implements Cloneable {
    private int messageSize;          // 消息总长度，方便从tcp流中截取完整的数据包
    private int messageId;            // 消息请求id，每个消息都有唯一递增的id，用于消息的幂等处理
    private int serviceId;            // 消息请求id，每个消息都有唯一递增的id，用于消息的幂等处理
    private long clientSendTime;      // 消息请求id，每个消息都有唯一递增的id，用于消息的幂等处理
    private long serverSendTime;      // 消息请求id，每个消息都有唯一递增的id，用于消息的幂等处理
    private int clientSeqId;          // 消息请求id，每个消息都有唯一递增的id，用于消息的幂等处理
    private int version;              // 消息请求id，每个消息都有唯一递增的id，用于消息的幂等处理
    private int errorCode;            // 消息请求id，每个消息都有唯一递增的id，用于消息的幂等处理
    private int fromServerId;         // 消息请求id，每个消息都有唯一递增的id，用于消息的幂等处理
    private int toServerId;           // 消息请求id，每个消息都有唯一递增的id，用于消息的幂等处理
    private long playerId;            // 消息请求id，每个消息都有唯一递增的id，用于消息的幂等处理
    private EnumMessageType messageType;// 消息请求id，每个消息都有唯一递增的id，用于消息的幂等处理
    private HeaderAttribute attribute = new HeaderAttribute(); // 包头扩展

    public int getMessageSize() {
        return messageSize;
    }

    public void setMessageSize(int messageSize) {
        this.messageSize = messageSize;
    }

    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    public int getServiceId() {
        return serviceId;
    }

    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
    }

    public long getClientSendTime() {
        return clientSendTime;
    }

    public void setClientSendTime(long clientSendTime) {
        this.clientSendTime = clientSendTime;
    }

    public long getServerSendTime() {
        return serverSendTime;
    }

    public void setServerSendTime(long serverSendTime) {
        this.serverSendTime = serverSendTime;
    }

    public int getClientSeqId() {
        return clientSeqId;
    }

    public void setClientSeqId(int clientSeqId) {
        this.clientSeqId = clientSeqId;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public int getFromServerId() {
        return fromServerId;
    }

    public void setFromServerId(int fromServerId) {
        this.fromServerId = fromServerId;
    }

    public int getToServerId() {
        return toServerId;
    }

    public void setToServerId(int toServerId) {
        this.toServerId = toServerId;
    }

    public long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(long playerId) {
        this.playerId = playerId;
    }

    public EnumMessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(EnumMessageType messageType) {
        this.messageType = messageType;
    }

    public HeaderAttribute getAttribute() {
        return attribute;
    }

    public void setAttribute(HeaderAttribute attribute) {
        this.attribute = attribute;
    }
}
