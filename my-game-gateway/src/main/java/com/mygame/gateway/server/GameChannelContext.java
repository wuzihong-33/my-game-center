package com.mygame.gateway.server;

import io.netty.channel.Channel;
import io.netty.channel.ChannelPromise;
import io.netty.util.concurrent.Future;

/**
 * channel上下文，封装playerId和channel
 */
public class GameChannelContext {
    private Channel channel;
    private long playerId;

    public GameChannelContext(long playerId, Channel channel) {
        this.channel = channel;
        this.playerId = playerId;
    }

    public Channel getChannel() {
        return channel;
    }

    public long getPlayerId() {
        return playerId;
    }

    public Future<?> writeAndFlush(Object msg) {
        return channel.writeAndFlush(msg);
    }

    public Future<?> writeAndFlush(Object msg, ChannelPromise promise) {
        return channel.writeAndFlush(msg, promise);
    }

    public void sendEvent(Object msg) {
        this.channel.pipeline().fireUserEventTriggered(msg);
    }
}
