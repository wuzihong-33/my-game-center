package com.mygame.xinyue.logic.task;

import com.mygame.db.entity.manager.TaskManager;
import com.mygame.xinyue.dataconfig.TaskDataConfig;

/**
 * 通关到指定关卡的进度类
 */
public class SpecificBlockTaskProgress implements ITaskProgress{

    @Override
    public void updateProgress(TaskManager taskManager, TaskDataConfig taskDataConfig, Object data) {
        taskManager.setValue((String)data);
    }

    @Override
    public boolean isFinish(TaskManager taskManager, TaskDataConfig taskDataConfig) {
        String value = taskManager.getTaskStringValue();
        if(value == null) {
            return false;
        }
        return value.compareTo(taskDataConfig.param) >= 0;//如果当前关卡大于等于目标关卡，说明已通关
    }

    @Override
    public Object getProgessValue(TaskManager taskManager, TaskDataConfig taskDataConfig) {
        return taskManager.getTaskStringValue();
    }

}
