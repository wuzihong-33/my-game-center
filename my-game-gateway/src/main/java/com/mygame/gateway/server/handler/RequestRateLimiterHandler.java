package com.mygame.gateway.server.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.util.concurrent.RateLimiter;
import com.mygame.game.common.GameMessagePackage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * 限流器
 */
public class RequestRateLimiterHandler extends ChannelInboundHandlerAdapter {
    private RateLimiter globalRateLimiter; // 全局限制器
    private static RateLimiter userRateLimiter;// 用户限流器，用于限制单个用户的请求。
    private static Logger logger = LoggerFactory.getLogger(RequestRateLimiterHandler.class);
    private int lastClientSeqId = 0;
    public RequestRateLimiterHandler(RateLimiter globalRateLimiter, double requestPerSecond) {
        this.globalRateLimiter = globalRateLimiter;
        userRateLimiter = RateLimiter.create(requestPerSecond);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (!userRateLimiter.tryAcquire()) {// 获取令牌失败，触发限流
            logger.debug("channel {} 请求过多，连接断开", ctx.channel().id().asShortText());
            ctx.close();
            return;
        }
        if (!globalRateLimiter.tryAcquire()) {// 获取全局令牌失败，触发限流
            logger.debug("全局请求超载，channel {} 断开", ctx.channel().id().asShortText());
            ctx.close();
            return;
        }
        
        GameMessagePackage gameMessagePackage = (GameMessagePackage)msg;
        int clientSeqId = gameMessagePackage.getHeader().getSeqId();
        if(lastClientSeqId > 0) {
            if(clientSeqId <= lastClientSeqId) {
                return ;
            }
        }
        this.lastClientSeqId = clientSeqId;
        ctx.fireChannelRead(msg);
    }
}
