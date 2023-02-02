package com.mygame.common.eventsystem;


@GameEventService
public class PlayerUpgradeService {
    @GameEventListener(PlayerUpgradeLevelEvent.class)
    public void handlePlayerUpgradeLevel(Object origin, IGameEventMessage event) {
        System.out.println("收到角色升级事件：" + event.getClass().getSimpleName()+"   " + PlayerUpgradeService.class.getSimpleName()+"正在处理中");
    }
}
