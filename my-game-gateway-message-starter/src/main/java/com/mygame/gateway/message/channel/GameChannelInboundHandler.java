package com.mygame.gateway.message.channel;

import com.mygame.game.common.IGameMessage;
import io.netty.util.concurrent.Promise;

public interface GameChannelInboundHandler extends GameChannelHandler {
    void channelRegister(AbstractGameChannelHandlerContext ctx,long playerId,GameChannelPromise promise);
    
    void channelInactive(AbstractGameChannelHandlerContext ctx) throws Exception;
    
    void channelRead(AbstractGameChannelHandlerContext ctx, Object msg) throws Exception;
    
    void channelReadRPCRequest(AbstractGameChannelHandlerContext ctx, IGameMessage msg) throws Exception;
    
    //触发一些内部事件
//    void userEventTriggered(AbstractGameChannelHandlerContext ctx, Object evt, Promise<Object> promise) throws Exception;
}
