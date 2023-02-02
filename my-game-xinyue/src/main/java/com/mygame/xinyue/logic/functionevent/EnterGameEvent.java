package com.mygame.xinyue.logic.functionevent;

import org.springframework.context.ApplicationEvent;
import com.mygame.db.entity.manager.PlayerManager;

public class EnterGameEvent extends ApplicationEvent{

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private PlayerManager PlayerManager;
    public EnterGameEvent(Object source,PlayerManager PlayerManager) {
        super(source);
        this.PlayerManager= PlayerManager;
    }
    public PlayerManager getPlayerManager() {
        return PlayerManager;
    }
    
    
    
}
