package com.mygame.gateway.message.rpc;

import com.mygame.game.common.EnumMessageType;
import com.mygame.game.common.GameMessageHeader;
import com.mygame.game.common.IGameMessage;
import com.mygame.gateway.message.channel.AbstractGameChannelHandlerContext;

/**
 * 方便发送rpc返回消息
 * @param <T>
 */
public class RPCEventContext<T> {
    private IGameMessage request;
    private T data;//这个用于存储缓存的数据，因为不同的服务的数据结构是不同的，所以这里使用泛型
    private AbstractGameChannelHandlerContext ctx;
    public RPCEventContext(T data,IGameMessage request, AbstractGameChannelHandlerContext ctx) {
        super();
        this.request = request;
        this.ctx = ctx;
        this.data = data;
    }

    public T getData() {
        return data;
    }
    
    public void sendResponse(IGameMessage response) {
        GameMessageHeader responseHeader = response.getHeader();
        EnumMessageType mesasageType = responseHeader.getMessageType();
        if(mesasageType != EnumMessageType.RPC_RESPONSE) {
            throw new IllegalArgumentException(response.getClass().getName() + " 参数类型不对，不是RPC的响应数据对象");
        }
        GameMessageHeader requestHeander = request.getHeader();
        responseHeader.setToServerId(requestHeander.getFromServerId());
        responseHeader.setFromServerId(requestHeander.getToServerId());
        responseHeader.setClientSeqId(requestHeander.getClientSeqId());
        responseHeader.setClientSendTime(requestHeander.getClientSendTime());
        responseHeader.setPlayerId(requestHeander.getPlayerId());
        responseHeader.setServerSendTime(System.currentTimeMillis());
        ctx.writeRPCMessage(response, null);
    }
}
