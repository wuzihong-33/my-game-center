package com.mygame.xinyue.logic.functionevent;

import org.springframework.context.ApplicationEvent;
import com.mygame.db.entity.manager.PlayerManager;

public class ConsumeDiamondEvent extends ApplicationEvent{
    private static final long serialVersionUID = 1L;
    private int diamond;
    private PlayerManager PlayerManager;
    public ConsumeDiamondEvent(Object source, int diamond, PlayerManager PlayerManager) {
        super(source);
        this.diamond = diamond;
        this.PlayerManager = PlayerManager;
    }
    public int getDiamond() {
        return diamond;
    }
    public PlayerManager getPlayerManager() {
        return PlayerManager;
    }
    
    

}
