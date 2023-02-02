package com.mygame.game.common;

public class GameMessageHeader implements Cloneable {
    private int messageSize;          // 消息总长度
    private int messageId;            // 消息号
    private int serviceId;            // 服务号
    private long clientSendTime;      // 客户端发送时间
    private long serverSendTime;      // 服务端发送时间
    private int seqId;                // 数据包序列号
    private int version;              // 协议版本
    private int errorCode;            // 错误码；如果非0，则包体为空
    private int fromServerId;         // 包的发送服务器id
    private int toServerId;           // 包的接受服务id
    private long playerId;            // pid
    private int clientSeqId;          // 主要用于实现Promise回调
    private EnumMessageType messageType; // 消息类型
    private HeaderAttribute attribute = new HeaderAttribute(); // 包头扩展，使用JSON序列化

    @Override
    public GameMessageHeader clone() throws CloneNotSupportedException {
        GameMessageHeader newHeader = (GameMessageHeader) super.clone();
        return newHeader;
    }
    
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

    public int getSeqId() {
        return seqId;
    }

    public void setSeqId(int seqId) {
        this.seqId = seqId;
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

    public int getClientSeqId() {
        return clientSeqId;
    }

    public void setClientSeqId(int clientSeqId) {
        this.clientSeqId = clientSeqId;
    }
}
