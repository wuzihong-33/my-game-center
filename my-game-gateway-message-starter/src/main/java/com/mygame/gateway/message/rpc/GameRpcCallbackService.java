package com.mygame.gateway.message.rpc;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import com.mygame.common.error.GameErrorException;
import com.mygame.game.common.IGameMessage;
import io.netty.util.concurrent.EventExecutorGroup;
import io.netty.util.concurrent.Promise;

public class GameRpcCallbackService {
    private Map<Integer, Promise<IGameMessage>> callbackMap = new ConcurrentHashMap<>();
    private EventExecutorGroup eventExecutorGroup;
    private int timeout = 30;// rpc超时时间

    public GameRpcCallbackService(EventExecutorGroup eventExecutorGroup) {
        this.eventExecutorGroup = eventExecutorGroup;
    }

    public void addCallback(Integer seqId, Promise<IGameMessage> promise) {
        if(promise == null) {
            return ;
        }
        callbackMap.put(seqId, promise);
        // 启动一个延时任务，rpc超时检测
        eventExecutorGroup.schedule(() -> {
            Promise<?> value = callbackMap.remove(seqId);
            if (value != null) {
                value.setFailure(GameErrorException.newBuilder(GameRPCError.TIME_OUT).build());
            }
        }, timeout, TimeUnit.SECONDS);
    }

    public void callback(IGameMessage gameMessage) {
        int seqId = gameMessage.getHeader().getClientSeqId();
        Promise<IGameMessage> promise = this.callbackMap.remove(seqId);
        if (promise != null) {
            promise.setSuccess(gameMessage);
        }
    }
}
