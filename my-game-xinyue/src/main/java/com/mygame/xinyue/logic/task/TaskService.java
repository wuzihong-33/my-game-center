package com.mygame.xinyue.logic.task;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import com.mygame.db.entity.manager.TaskManager;
import com.mygame.xinyue.dataconfig.TaskDataConfig;
//import com.mygame.xinyue.logic.functionevent.ConsumeDiamond;
import com.mygame.xinyue.logic.functionevent.ConsumeGoldEvent;
import com.mygame.xinyue.logic.functionevent.EnterGameEvent;
import com.mygame.xinyue.logic.functionevent.PassBlockPointEvent;

@Service
public class TaskService {
    @EventListener
    public void EnterGameEvent(EnterGameEvent event) {
        // 进入游戏的时候，判断一下任务有没有实始化，没有初始化的，自动接收第一个任务
        TaskManager taskManager = event.getPlayerManager().getTaskManager();
        if (!taskManager.isInitTask()) {
            // 获取第一个任务的任务id
            String taskId = "1001";
            taskManager.receiveTask(taskId);
        }
    }

    @EventListener
    public void consumeGold(ConsumeGoldEvent event) {
        // 接收金币消耗事件
        this.updateTaskProgress(event.getPlayerManager().getTaskManager(),EnumTaskType.ConsumeGold, event.getGold());
    }

//    @EventListener
//    public void consumeDiamond(ConsumeDiamond event) {
//        this.updateTaskProgress(event.getPlayerManager().getTaskManager(),EnumTaskType.ConsumeDiamond, event.getDiamond());
//    }

    @EventListener
    public void passBlockPoint(PassBlockPointEvent event) {
        //通关事件影响多个任务类型的进度
        this.updateTaskProgress(event.getPlayerManager().getTaskManager(),EnumTaskType.PassBlockPoint, event.getPointId());
        this.updateTaskProgress(event.getPlayerManager().getTaskManager(),EnumTaskType.PassBlockPointTimes, event.getPointId());

    }
    private void updateTaskProgress(TaskManager taskManager, EnumTaskType taskType, Object value) {
        String taskId = taskManager.getNowReceiveTaskId();
        TaskDataConfig taskDataConfig = this.getTaskDataConfig(taskId);
        if (taskDataConfig.taskType == taskType.getType()) {//如果事件更新的任务类型，与当前接受的任务类型一致，更新任务进度
            taskType.getTaskProgress().updateProgress(taskManager, taskDataConfig, value);
        }
    }



    public TaskDataConfig getTaskDataConfig(String taskId) {
        // 根据taskId获取这个taskId对应的配置数据，这里直模拟返回一个
        return new TaskDataConfig();
    }

    public EnumTaskType getEnumTaskType(int taskType) {
        for (EnumTaskType enumTaskType : EnumTaskType.values()) {
            if (enumTaskType.getType() == taskType) {
                return enumTaskType;
            }
        }
        throw new IllegalArgumentException("任务类型不存在：" + taskType);
    }
}
