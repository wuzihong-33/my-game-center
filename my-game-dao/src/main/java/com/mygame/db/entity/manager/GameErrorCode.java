package com.mygame.db.entity.manager;

import com.mygame.common.error.IServerError;

public enum GameErrorCode implements IServerError {
    HeroNotExist(101,"英雄不存在"),
    WeaponNotExist(102,"武器不存在"),
    HeroLevelNotEnough(103,"魂师等级不足"),
    EquipWeaponCostNotEnough(104,"装备武器消耗不足"),
    WeaponUnenable(105,"武器不可用"),
    HeroHadEquipedWeapon(106,"此英雄已装备武器"),
    ;
    private int errorCode;
    private String desc;
    
    private GameErrorCode(int errorCode ,String desc){
        this.errorCode = errorCode;
        this.desc = desc;
    }

    @Override
    public int getErrorCode() {
        return errorCode;
    }

    @Override
    public String getErrorDesc() {
        return desc;
    }

}
