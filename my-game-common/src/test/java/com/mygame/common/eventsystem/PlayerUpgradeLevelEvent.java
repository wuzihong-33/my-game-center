package com.mygame.common.eventsystem;

public class PlayerUpgradeLevelEvent implements IGameEventMessage{
    private long playerId;
    private int nowLevel;//当前等级
    private int preLevel;//升级前的升级
    private int costExp;//消耗的经验
    
    
    public long getPlayerId() {
        return playerId;
    }
    public void setPlayerId(long playerId) {
        this.playerId = playerId;
    }
    public int getNowLevel() {
        return nowLevel;
    }
    public void setNowLevel(int nowLevel) {
        this.nowLevel = nowLevel;
    }
    public int getPreLevel() {
        return preLevel;
    }
    public void setPreLevel(int preLevel) {
        this.preLevel = preLevel;
    }
    public int getCostExp() {
        return costExp;
    }
    public void setCostExp(int costExp) {
        this.costExp = costExp;
    }
    
}
