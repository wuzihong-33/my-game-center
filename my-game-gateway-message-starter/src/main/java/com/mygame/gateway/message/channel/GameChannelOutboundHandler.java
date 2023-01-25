package com.mygame.gateway.message.channel;

import com.mygame.game.common.IGameMessage;
import io.netty.util.concurrent.Promise;

public interface GameChannelOutboundHandler extends GameChannelHandler {
    void writeAndFlush(AbstractGameChannelHandlerContext ctx, IGameMessage msg, GameChannelPromise promise) throws Exception;
    void writeRPCMessage(AbstractGameChannelHandlerContext ctx, IGameMessage gameMessage, Promise<IGameMessage> callback);
    void close(AbstractGameChannelHandlerContext ctx, GameChannelPromise promise);
}
