package com.mygame.db.entity.manager;

import com.mygame.db.entity.Arena;

public class ArenaManager {

    private Arena arena;
    public ArenaManager(Arena arena) {
        this.arena = arena;
    }
    
    public Arena getArena() {
        return arena;
    }
    
    
    public void addChallengeTimes(int times) {
        int result = arena.getChallengeTimes() + times;
        arena.setChallengeTimes(result);
    }
}
