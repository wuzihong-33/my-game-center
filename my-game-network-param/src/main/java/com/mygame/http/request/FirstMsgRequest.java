package com.mygame.http.request;

import com.mygame.game.common.AbstractGameMessage;
import com.mygame.game.common.EnumMessageType;
import com.mygame.game.common.GameMessageMetadata;

@GameMessageMetadata(messageId = 10001, serviceId = 1,messageType= EnumMessageType.REQUEST) 
public class FirstMsgRequest extends AbstractGameMessage {
    private String value;

    @Override
    protected void decode(byte[] body) {
        value = new String(body);
    }

    @Override
    protected byte[] encode() {
        return value.getBytes();
    }

    @Override
    protected boolean isBodyMsgNull() {// 返回要序列化的消息体是否为null
        return this.value == null;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
