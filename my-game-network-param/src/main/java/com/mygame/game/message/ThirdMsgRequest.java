package com.mygame.game.message;

import com.google.protobuf.InvalidProtocolBufferException;
import com.mygame.game.common.AbstractGameMessage;
import com.mygame.game.common.EnumMessageType;
import com.mygame.game.common.GameMessageMetadata;
//import com.mygame.game.message.body.ThirdMsgBody;
//@GameMessageMetadata(messageId = 10003, messageType = EnumMessageType.REQUEST, serviceId = 1)
public class ThirdMsgRequest extends AbstractGameMessage{
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
    }//请求消息
//    private ThirdMsgBody.ThirdMsgRequestBody requestBody;//消息体使用Protocol Buffers生成的类
//    public ThirdMsgBody.ThirdMsgRequestBody getRequestBody() {//定义getter方法
//        return requestBody;
//    }
//    public void setRequestBody(ThirdMsgBody.ThirdMsgRequestBody requestBody) {//定义setter方法
//        this.requestBody = requestBody;
//    }
//
//    @Override
//    protected byte[] encode() {
//        return this.requestBody.toByteArray();//使用Protocol Buffers的方式将消息体序列化
//    }
//
//    @Override
//    protected void decode(byte[] body) {
//        try {
//            this.requestBody = ThirdMsgBody.ThirdMsgRequestBody.parseFrom(body);//使用Protocol Buffers的方式反序列化消息体
//        } catch (InvalidProtocolBufferException e) {
//            e.printStackTrace();
//        }
//        
//    }
//
//    @Override
//    protected boolean isBodyMsgNull() {
//        return this.requestBody == null;//判断消息体是否为空
//    }

}
