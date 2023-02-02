package com.mygame.xinyue.logic.functionevent;

import org.springframework.context.ApplicationEvent;
import com.mygame.db.entity.manager.PlayerManager;

public class PassBlockPointEvent extends ApplicationEvent{
    private static final long serialVersionUID = 1L;
    private String pointId;
    private PlayerManager PlayerManager;
    public PassBlockPointEvent(Object source,String pointId,PlayerManager PlayerManager) {
        super(source);
        this.pointId = pointId;
        this.PlayerManager = PlayerManager;
    }
    public String getPointId() {
        return pointId;
    }
    public PlayerManager getPlayerManager() {
        return PlayerManager;
    }
}
