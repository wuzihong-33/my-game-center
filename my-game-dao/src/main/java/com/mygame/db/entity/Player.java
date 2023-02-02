package com.mygame.db.entity;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "Player")
public class Player {
    @Id
    private long playerId;
    private String nickName;
    private int level;
    private long lastLoginTime;
    private long createTime;
    
    private ConcurrentHashMap<String, String> heros = new ConcurrentHashMap<>(); // 英雄角色
    private LinkedBlockingQueue<String> tasks = new LinkedBlockingQueue<>(); // 接受的任务
    private Inventory inventory = new Inventory(); // 背包
    private Task task = new Task();

    //    private ConcurrentHashMap<String, Hero> herosMap = new ConcurrentHashMap<>();
//    private ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<String, Integer>();
    
    
    public Inventory getInventory() {
        return inventory;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

//    public ConcurrentHashMap<String, Hero> getHerosMap() {
//        return herosMap;
//    }

//    public void setHerosMap(ConcurrentHashMap<String, Hero> herosMap) {
//        this.herosMap = herosMap;
//    }

    public LinkedBlockingQueue<String> getTasks() {
        return tasks;
    }

    public void setTasks(LinkedBlockingQueue<String> tasks) {
        this.tasks = tasks;
    }

    public Map<String, String> getHeros() {
       
        return heros;
    }

    public void setHeros(ConcurrentHashMap<String, String> heros) {
        this.heros = heros;
    }

//    public Map<String, Integer> getMap() {
//		return map;
//	}

//	public void setMap(ConcurrentHashMap<String, Integer> map) {
//		this.map = map;
//	}

	public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(long playerId) {
        this.playerId = playerId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public long getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(long lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    @Override
    public String toString() {
        return "Player [playerId=" + playerId + ", nickName=" + nickName + ", level=" + level + ", lastLoginTime=" + lastLoginTime + "]";
    }


}
