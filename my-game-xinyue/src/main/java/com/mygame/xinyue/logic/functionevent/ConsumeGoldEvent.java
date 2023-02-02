package com.mygame.xinyue.logic.functionevent;

import org.springframework.context.ApplicationEvent;
import com.mygame.db.entity.manager.PlayerManager;

public class ConsumeGoldEvent extends ApplicationEvent{
    private static final long serialVersionUID = 1L;
    private int gold;
    private PlayerManager PlayerManager;
    public ConsumeGoldEvent(Object source,int gold,PlayerManager PlayerManager) {
        super(source);
        this.gold = gold;
        this.PlayerManager = PlayerManager;
    }
    public int getGold() {
        return gold;
    }
    public PlayerManager getPlayerManager() {
        return PlayerManager;
    }
    
    
    
    

}
