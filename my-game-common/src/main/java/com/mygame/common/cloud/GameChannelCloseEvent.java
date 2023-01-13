package com.mygame.common.cloud;

import org.springframework.context.ApplicationEvent;

public class GameChannelCloseEvent extends ApplicationEvent {
    
    private static final long serialVersionUID = 1L;
    private long playerId;
    
    public GameChannelCloseEvent(Object source,long playerId) {
        super(source);
        this.playerId = playerId;
    }
    public long getPlayerId() {
        return playerId;
    }
}
