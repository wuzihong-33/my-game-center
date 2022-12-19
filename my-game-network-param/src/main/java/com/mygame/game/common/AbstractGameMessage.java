package com.mygame.game.common;

public abstract class AbstractGameMessage implements IGameMessage {
    private GameMessageHeader header;
    private byte[] body;


    public AbstractGameMessage() {
        GameMessageMetaData gameMessageMetaData = this.getClass().getAnnotation(GameMessageMetaData.class);
        if (gameMessageMetaData == null) {
            throw new IllegalArgumentException("消息没有添加元数据注解：" + this.getClass().getName());
        }
        header = new GameMessageHeader();
        header.setMessageId(gameMessageMetaData.messageId());
        header.setServiceId(gameMessageMetaData.serviceId());
        header.setMessageType(gameMessageMetaData.messageType());
    }
    
    
    @Override
    public GameMessageHeader getHeader() {
        return header;
    }

    @Override
    public void setHeader(GameMessageHeader header) {
        this.header = header;
    }

    @Override
    public void read(byte[] bytes) {
        body = bytes;
        if (body != null) {
            this.decode(body);
        }
    }

    @Override
    public byte[] body() {
        if (body == null) {
            if (!this.isBodyMsgNull()) {
                body = this.encode();
                if (body == null) {// 检测是否返回的空，防止开发者默认返回null
                    throw new IllegalArgumentException("消息序列化之后的值为null:" + this.getClass().getName());
                }
            }
        }
        return body;
    }

    protected abstract byte[] encode();
    protected abstract void decode(byte[] body);
    protected abstract boolean isBodyMsgNull();
    
}
