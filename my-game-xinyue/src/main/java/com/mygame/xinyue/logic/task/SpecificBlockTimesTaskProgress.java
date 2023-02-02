package com.mygame.xinyue.logic.task;

import com.mygame.db.entity.manager.TaskManager;
import com.mygame.xinyue.dataconfig.TaskDataConfig;

/**
 * 指定某个关卡通关多少钱的任务
 */
public class SpecificBlockTimesTaskProgress implements ITaskProgress{

    @Override
    public void updateProgress(TaskManager taskManager, TaskDataConfig taskDataConfig, Object data) {
        String pointId = (String)data;
        String[] params = taskDataConfig.param.split(",");
        if(pointId.equals(params[0])) {
            taskManager.addManyIntValue(pointId, 1);//如果和目标关卡id匹配，测通关次数加1
        }
    }

    @Override
    public boolean isFinish(TaskManager taskManager, TaskDataConfig taskDataConfig) {
        String[] params = taskDataConfig.param.split(",");
        int value = taskManager.getManayIntValue(params[0]);
        return value >= Integer.parseInt(params[1]);//如果当前值大于等于目标要求的次数，说明完成任务
    }

    @Override
    public Object getProgessValue(TaskManager taskManager, TaskDataConfig taskDataConfig) {
        String[] params = taskDataConfig.param.split(",");
         int value = taskManager.getManayIntValue(params[0]);
        return value;
    }

    
}
