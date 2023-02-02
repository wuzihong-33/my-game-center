package com.mygame.db.entity.manager;
import com.mygame.db.entity.Hero;
import com.mygame.db.entity.Player;
import com.mygame.db.entity.Weapon;

/**
 * 
 */
public class PlayerManager {
    private Player player;
    private HeroManager heroManager; //英雄管理类
    private TaskManager taskManager;
    private InventoryManager inventoryManager;
    //声明其它的管理类....
    
    
    public PlayerManager(Player player) {//初始化所的管理类
        this.player = player;
        this.heroManager = new HeroManager(player);
        this.taskManager = new TaskManager(player.getTask());
        this.inventoryManager = new InventoryManager(player.getInventory());
        //其它的管理类.....
    }
    
    public Player getPlayer() {
        return player;
    }
    public int addPlayerExp(int exp) {
        //添加角色经验，判断是否升级，返回升级后当前最新的等级
        return player.getLevel();
    }
    
    public Weapon getWeapon(String weaponId) {
        return this.inventoryManager.getWeapon(weaponId);
    }
    public Hero getHero(String heroId) {
        return this.heroManager.getHero(heroId);
    }
    
    public HeroManager getHeroManager() {
        return heroManager;
    }
    public TaskManager getTaskManager() {
        return taskManager;
    }
    public InventoryManager getInventoryManager() {
        return inventoryManager;
    }
    
    
}
