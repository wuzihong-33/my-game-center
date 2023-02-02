package com.mygame.xinyue.common;

import java.util.Optional;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import com.mygame.dao.AsyncPlayerDao;
import com.mygame.db.entity.Player;
import com.mygame.db.entity.manager.PlayerManager;
import com.mygame.game.common.IGameMessage;
import com.mygame.game.messagedispatcher.DispatchGameMessageService;
import com.mygame.gateway.message.channel.AbstractGameChannelHandlerContext;
import com.mygame.gateway.message.channel.GameChannelPromise;
import com.mygame.gateway.message.context.DispatchUserEventService;
import com.mygame.gateway.message.context.GatewayMessageContext;
import com.mygame.gateway.message.context.ServerConfig;
import com.mygame.gateway.message.context.UserEventContext;
import com.mygame.gateway.message.handler.AbstractGameMessageDispatchHandler;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.Promise;
import io.netty.util.concurrent.ScheduledFuture;

/**
 * 有状态
 */
public class GameBusinessMessageDispatchHandler extends AbstractGameMessageDispatchHandler<PlayerManager> {
    private DispatchGameMessageService dispatchGameMessageService;
//    private DispatchUserEventService dispatchUserEventService;
    private static Logger logger = LoggerFactory.getLogger(GameBusinessMessageDispatchHandler.class);
    private Player player;
    private PlayerManager PlayerManager;
    private AsyncPlayerDao playerDao;
    private ServerConfig serverConfig;
    
    private ScheduledFuture<?> flushToRedisScheduleFuture;
    private ScheduledFuture<?> flushToDBScheduleFuture;

    public GameBusinessMessageDispatchHandler(ApplicationContext applicationContext, ServerConfig serverConfig, DispatchGameMessageService dispatchGameMessageService, DispatchUserEventService dispatchUserEventService, AsyncPlayerDao playerDao) {
        super(applicationContext);
        this.dispatchGameMessageService = dispatchGameMessageService;
        this.playerDao = playerDao;
        this.serverConfig = serverConfig;
//        this.dispatchUserEventService = dispatchUserEventService;
    }

    @Override
    public void channelRegister(AbstractGameChannelHandlerContext ctx, long playerId, GameChannelPromise promise) {
        // 在用户GameChannel注册的时候，对用户的数据进行初始化
        playerDao.findPlayer(playerId, new DefaultPromise<>(ctx.executor())).addListener(new GenericFutureListener<Future<Optional<Player>>>() {
            @Override
            public void operationComplete(Future<Optional<Player>> future) throws Exception {
                Optional<Player> playerOp = future.get();
                if (playerOp.isPresent()) {
                    player = playerOp.get();
                    PlayerManager = new PlayerManager(player);
                    promise.setSuccess();
                    fixTimerFlushPlayer(ctx);// 启动定时持久化数据到数据库
                } else {
                    logger.error("player {} 不存在", playerId);
                    promise.setFailure(new IllegalArgumentException("找不到Player数据，playerId:" + playerId));
                }
            }
        });
    }
    
    @Override
    public void exceptionCaught(AbstractGameChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("服务器异常,playerId:{}", ctx.gameChannel().getPlayerId(), cause);
    }

    @Override
    public void channelInactive(AbstractGameChannelHandlerContext ctx) throws Exception {
        if (flushToDBScheduleFuture != null) {// 取消定时器
            flushToDBScheduleFuture.cancel(true);
        }
        if (flushToRedisScheduleFuture != null) {
            flushToRedisScheduleFuture.cancel(true);
        }
        this.playerDao.syncFlushPlayer(player);// GameChannel移除的时候，强制更新一次数据
        logger.debug("强制flush player {} 成功", player.getPlayerId());
        logger.debug("game channel 移除，playerId:{}", ctx.gameChannel().getPlayerId());
        ctx.fireChannelInactive();// 向下一个Handler发送channel失效事件
    }

    @Override
    public void channelRead(AbstractGameChannelHandlerContext ctx, Object msg) throws Exception {
        IGameMessage gameMessage = (IGameMessage) msg;
        GatewayMessageContext<PlayerManager> stx = new GatewayMessageContext<>(PlayerManager, gameMessage, ctx);
        dispatchGameMessageService.callMethod(gameMessage, stx);
    }

//    @Override
    public void userEventTriggered(AbstractGameChannelHandlerContext ctx, Object evt, Promise<Object> promise) throws Exception {
//        if (evt instanceof IdleStateEvent) {
//            logger.debug("收到空闲事件：{}", evt.getClass().getName());
//            ctx.close();
//        }
        // else if (evt instanceof GetPlayerInfoEvent) {
        // GetPlayerByIdMsgResponse response = new GetPlayerByIdMsgResponse();
        // response.getBodyObj().setPlayerId(this.player.getPlayerId());
        // response.getBodyObj().setNickName(this.player.getNickName());
        // Map<String, String> heros = new HashMap<>();
        // this.player.getHeros().forEach((k,v)->{//复制处理一下，防止对象安全溢出。
        // heros.put(k, v);
        // });
        // //response.getBodyObj().setHeros(this.player.getHeros());不要使用这种方式，它会把这个map传递到其它线程
        // response.getBodyObj().setHeros(heros);
        // promise.setSuccess(response);
        // }
//        UserEventContext<PlayerManager> utx = new UserEventContext<>(PlayerManager, ctx);
//        dispatchUserEventService.callMethod(utx, evt, promise);
    }

    @Override
    protected PlayerManager getDataManager() {
        return PlayerManager;
    }

    @Override
    protected Future<Boolean> updateToRedis(Promise<Boolean> promise) {
        return null;
    }

    @Override
    protected Future<Boolean> updateToDB(Promise<Boolean> promise) {
        return null;
    }

    @Override
    protected void initData(AbstractGameChannelHandlerContext ctx, long playerId, GameChannelPromise promise) {

    }

    /**
     * 以一定的频率持久化
     * @param ctx
     */
    private void fixTimerFlushPlayer(AbstractGameChannelHandlerContext ctx) {
        int flushRedisDelay = serverConfig.getFlushRedisDelaySecond();// 获取定时器执行的延迟时间，单位是秒
        int flushDBDelay = serverConfig.getFlushDBDelaySecond();
        flushToRedisScheduleFuture = ctx.executor().scheduleWithFixedDelay(() -> {// 创建持久化数据到redis的定时任务
            long start = System.currentTimeMillis();// 任务开始执行的时间
            Promise<Boolean> promise = new DefaultPromise<>(ctx.executor());
            playerDao.saveOrUpdatePlayerToRedis(player, promise).addListener(new GenericFutureListener<Future<Boolean>>() {
                @Override
                public void operationComplete(Future<Boolean> future) throws Exception {
                    if (future.isSuccess()) {
                        if (logger.isDebugEnabled()) {
                            long end = System.currentTimeMillis();
                            logger.debug("player {} 同步数据到redis成功,耗时:{} ms", player.getPlayerId(), (end - start));
                        }
                    } else {
                        logger.error("player {} 同步数据到Redis失败", player.getPlayerId());
                        // 这个时候应该报警
                    }
                }
            });
        }, flushRedisDelay, flushRedisDelay, TimeUnit.SECONDS);
        flushToDBScheduleFuture = ctx.executor().scheduleWithFixedDelay(() -> {
            long start = System.currentTimeMillis();// 任务开始执行时间
            Promise<Boolean> promise = new DefaultPromise<>(ctx.executor());
            playerDao.saveOrUpdatePlayerToDB(player, promise).addListener(new GenericFutureListener<Future<Boolean>>() {
                @Override
                public void operationComplete(Future<Boolean> future) throws Exception {
                    if (future.isSuccess()) {
                        if (logger.isDebugEnabled()) {
                            long end = System.currentTimeMillis();
                            logger.debug("player {} 同步数据到MongoDB成功,耗时:{} ms", player.getPlayerId(), (end - start));
                        }
                    } else {
                        logger.error("player {} 同步数据到MongoDB失败", player.getPlayerId());
                        // 这个时候应该报警,将数据同步到日志中，以待恢复
                    }
                }
            });
        }, flushDBDelay, flushDBDelay, TimeUnit.SECONDS);
    }
    

}
