package com.mygame.game.messagedispatcher;

import com.mygame.game.common.IGameMessage;

/**
 * IGameChannel上下文接口
 */
public interface IGameChannelContext {
    /**
     * 向channel发送消息
     * @param gameMessage
     */
    void sendMessage(IGameMessage gameMessage);

    /**
     * 获取请求
     * @param <T>
     * @return
     */
    <T> T getRequest();
    
    String getRemoteHost();
    
    long getPlayerId();
}
