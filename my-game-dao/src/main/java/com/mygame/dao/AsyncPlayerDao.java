package com.mygame.dao;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.mygame.common.concurrent.GameEventExecutorGroup;
import com.mygame.db.entity.Player;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.Promise;

/**
 * 封装同步dao为异步
 */
public class AsyncPlayerDao {
    private GameEventExecutorGroup executorGroup;
    private PlayerDao playerDao;
    private static Logger logger = LoggerFactory.getLogger(AsyncPlayerDao.class);

    // 由外面注入线程池组，可以使线程池组的共用
    public AsyncPlayerDao(GameEventExecutorGroup executorGroup, PlayerDao playerDao) {// 初始化的时候，从构造方法注入线程数量，和需要的PlayerDao实例
        this.executorGroup = executorGroup;
        this.playerDao = playerDao;
    }
    
    public Future<Optional<Player>> findPlayer(long playerId, Promise<Optional<Player>> promise) {
        this.execute(playerId, promise, () -> {
            Optional<Player> playerOp = playerDao.findById(playerId);
            promise.setSuccess(playerOp);
        });
        return promise;
    }

    public Promise<Boolean> saveOrUpdatePlayerToDB(Player player,Promise<Boolean> promise) {
        this.execute(player.getPlayerId(), promise, ()->{
            playerDao.saveOrUpdateToDB(player);
            promise.setSuccess(true);
        });
        return promise;
    }

    public Promise<Boolean> saveOrUpdatePlayerToRedis(Player player,Promise<Boolean> promise) {
        this.execute(player.getPlayerId(),promise,()->{
           playerDao.saveOrUpdateToRedis(player, player.getPlayerId());
           promise.setSuccess(true);
        });
        return promise;
    }
    
    public void syncFlushPlayer(Player player) {
        this.playerDao.saveOrUpdate(player, player.getPlayerId());
    }


    private void execute(long playerId, Promise<?> promise, Runnable task) {
        EventExecutor executor = this.executorGroup.select(playerId);
        executor.execute(() -> {
            try {
                task.run();
            } catch (Throwable e) {// 统一进行异常捕获，防止由于数据库查询的异常导到线程卡死
                logger.error("数据库操作失败,playerId:{}", playerId, e);
                if (promise != null) {
                    promise.setFailure(e);
                }
            }
        });
    }
}
