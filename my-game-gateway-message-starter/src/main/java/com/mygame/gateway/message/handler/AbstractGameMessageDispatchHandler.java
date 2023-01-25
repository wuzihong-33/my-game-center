package com.mygame.gateway.message.handler;


import com.mygame.game.common.IGameMessage;
import com.mygame.gateway.message.channel.AbstractGameChannelHandlerContext;
import com.mygame.gateway.message.channel.GameChannelHandler;
import com.mygame.gateway.message.channel.GameChannelInboundHandler;
import com.mygame.gateway.message.channel.GameChannelPromise;
import com.mygame.gateway.message.context.UserEventContext;
import io.netty.channel.ChannelInboundHandler;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.Promise;
import io.netty.util.concurrent.ScheduledFuture;
import org.slf4j.Logger;
import org.springframework.context.ApplicationContext;

public abstract class AbstractGameMessageDispatchHandler<T> implements GameChannelInboundHandler {
////    private DispatchRPCEventService dispatchRPCEventService;
////    private DispatchGameMessageService dispatchGameMessageService;
////    private DispatchUserEventService dispatchUserEventService;
//    private ScheduledFuture<?> flushToRedisScheduleFuture;
//    private ScheduledFuture<?> flushToDBScheduleFuture;
////    private ServerConfig serverConfig;
//
//    protected abstract T getDataManager();
//    protected long playerId;
//    protected Logger logger;
//    protected int gatewayServerId;
//
//    public AbstractGameMessageDispatchHandler(ApplicationContext applicationContext) {
////        this.dispatchRPCEventService = applicationContext.getBean(DispatchRPCEventService.class);
////        this.dispatchGameMessageService = applicationContext.getBean(DispatchGameMessageService.class);
////        this.dispatchUserEventService = applicationContext.getBean(DispatchUserEventService.class);
////        this.serverConfig = applicationContext.getBean(ServerConfig.class);
////        logger = LoggerFactory.getLogger(this.getClass());
//    }
//
//    @Override
//    public void exceptionCaught(AbstractGameChannelHandlerContext ctx, Throwable cause) throws Exception {
//        ctx.fireExceptionCaught(cause);
//    }
//
//    @Override
//    public void channelRegister(AbstractGameChannelHandlerContext ctx, long playerId, GameChannelPromise promise) {
//        this.playerId = playerId;
//        GameChannelPromise initPromise = ctx.newPromise();
//        initPromise.addListener(new GenericFutureListener<Future<? super Void>>() {
//            @Override
//            public void operationComplete(Future<? super Void> future) throws Exception {
//                // 初始化成功之后，启动定时器，定时持久化数据
//                fixTimerFlushPlayer(ctx);
//                promise.setSuccess();
//            }
//        });
//        this.initData(ctx, playerId, initPromise);
//    }
//
//    @Override
//    public void channelInactive(AbstractGameChannelHandlerContext ctx) throws Exception {
//        if (flushToDBScheduleFuture != null) {// 取消定时器
//            flushToDBScheduleFuture.cancel(true);
//        }
//        if (flushToRedisScheduleFuture != null) {
//            flushToRedisScheduleFuture.cancel(true);
//        }
//        this.updateToRedis0(ctx);
//        this.updateToDB0(ctx);
//        logger.debug("game channel 移除，playerId:{}", ctx.gameChannel().getPlayerId());
//        ctx.fireChannelInactive();// 向下一个Handler发送channel失效事件
//    }
//
//    @Override
//    public void channelRead(AbstractGameChannelHandlerContext ctx, Object msg) throws Exception {
//        IGameMessage gameMessage = (IGameMessage) msg;
//        T dataManager = this.getDataManager();
////        GatewayMessageContext<T> stx = new GatewayMessageContext<>(dataManager, null, null, gameMessage, ctx);
////        dispatchGameMessageService.callMethod(gameMessage, stx);
//    }
//    
//    
//    @Override
//    public void userEventTriggered(AbstractGameChannelHandlerContext ctx, Object evt, Promise<Object> promise) throws Exception {
//        T data = this.getDataManager();
//        UserEventContext<T> utx = new UserEventContext<>(data, ctx);
////        dispatchUserEventService.callMethod(utx, evt, promise);
//    }
//
//    @Override
//    public void channelReadRPCRequest(AbstractGameChannelHandlerContext ctx, IGameMessage msg) throws Exception {
//        T data = this.getDataManager();
////        RPCEventContext<T> rpcEventContext = new RPCEventContext<>(data, msg, ctx);
////        this.dispatchRPCEventService.callMethod(rpcEventContext, msg);
//    }
//
//    protected abstract Future<Boolean> updateToRedis(Promise<Boolean> promise);
//
//    protected abstract Future<Boolean> updateToDB(Promise<Boolean> promise);
//
//    protected  long getPlayerId() {
//        return playerId;
//    }
//
//    protected abstract void initData(AbstractGameChannelHandlerContext ctx, long playerId, GameChannelPromise promise);
//
//    private void fixTimerFlushPlayer(AbstractGameChannelHandlerContext ctx) {
//        int flushRedisDelay = serverConfig.getFlushRedisDelaySecond();// 获取定时器执行的延迟时间，单位是秒
//        int flushDBDelay = serverConfig.getFlushDBDelaySeond();
//        flushToRedisScheduleFuture = ctx.executor().scheduleWithFixedDelay(() -> {// 创建持久化数据到redis的定时任务
//            this.updateToRedis0(ctx);
//        }, flushRedisDelay, flushRedisDelay, TimeUnit.SECONDS);
//        flushToDBScheduleFuture = ctx.executor().scheduleWithFixedDelay(() -> {
//            this.updateToDB0(ctx);
//        }, flushDBDelay, flushDBDelay, TimeUnit.SECONDS);
//    }
//
//    private void updateToRedis0(AbstractGameChannelHandlerContext ctx) {
//        long start = System.currentTimeMillis();// 任务开始执行的时间
//        Promise<Boolean> promise = new DefaultPromise<>(ctx.executor());
//        this.updateToRedis(promise).addListener(new GenericFutureListener<Future<Boolean>>() {
//            @Override
//            public void operationComplete(Future<Boolean> future) throws Exception {
//                if (future.isSuccess()) {
//                    if (logger.isDebugEnabled()) {
//                        long end = System.currentTimeMillis();
//                        logger.debug("player {} 同步数据到redis成功,耗时:{} ms", getPlayerId(), (end - start));
//                    }
//                } else {
//                    logger.error("player {} 同步数据到Redis失败", getPlayerId());
//                    // 这个时候应该报警
//                }
//            }
//        });
//    }
//
//    private void updateToDB0(AbstractGameChannelHandlerContext ctx) {
//        long start = System.currentTimeMillis();// 任务开始执行时间
//        Promise<Boolean> promise = new DefaultPromise<>(ctx.executor());
//        updateToDB(promise).addListener(new GenericFutureListener<Future<Boolean>>() {
//            @Override
//            public void operationComplete(Future<Boolean> future) throws Exception {
//                if (future.isSuccess()) {
//                    if (logger.isDebugEnabled()) {
//                        long end = System.currentTimeMillis();
//                        logger.debug("player {} 同步数据到MongoDB成功,耗时:{} ms", getPlayerId(), (end - start));
//                    }
//                } else {
//                    logger.error("player {} 同步数据到MongoDB失败", getPlayerId());
//                    // 这个时候应该报警,将数据同步到日志中，以待恢复
//                }
//            }
//        });
//    }

}
