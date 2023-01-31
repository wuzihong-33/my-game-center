package com.mygame.game.message;

import com.google.protobuf.InvalidProtocolBufferException;
import com.mygame.game.common.AbstractGameMessage;
import com.mygame.game.common.EnumMessageType;
import com.mygame.game.common.GameMessageMetadata;
//import com.mygame.game.message.body.ThirdMsgBody;
@GameMessageMetadata(messageId = 10003, messageType = EnumMessageType.RESPONSE, serviceId = 1)
public class ThirdMsgResponse extends AbstractGameMessage{
    @Override
    protected byte[] encode() {
        return new byte[0];
    }

    @Override
    protected void decode(byte[] body) {

    }

    @Override
    protected boolean isBodyMsgNull() {
        return false;
    }
//    private ThirdMsgBody.ThirdMsgResponseBody responseBody;//声明消息体
//    
//    
//    public ThirdMsgBody.ThirdMsgResponseBody getResponseBody() {
//        return responseBody;
//    }
//
//    public void setResponseBody(ThirdMsgBody.ThirdMsgResponseBody responseBody) {
//        this.responseBody = responseBody;
//    }
//
//    @Override
//    protected byte[] encode() {
//        return this.responseBody.toByteArray();//序列化消息体
//    }
//
//    @Override
//    protected void decode(byte[] body) {
//        try {
//            this.responseBody = ThirdMsgBody.ThirdMsgResponseBody.parseFrom(body);//反序列化消息体
//        } catch (InvalidProtocolBufferException e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    protected boolean isBodyMsgNull() {
//        return this.responseBody == null;//判断消息体是否为空
//    }

}
