package com.mygame.dao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.mygame.common.concurrent.GameEventExecutorGroup;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Promise;

/**
 * 封装同步dao为异步
 */
public abstract class AbstractAsyncDao {
    protected Logger logger = null;
    private GameEventExecutorGroup executorGroup;
    public AbstractAsyncDao(GameEventExecutorGroup executorGroup) {
        this.executorGroup = executorGroup;
        logger = LoggerFactory.getLogger(this.getClass());
    }
    
    protected void execute(long playerId, Promise<?> promise, Runnable task) {
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
