package com.mygame.xinyue.logic.task;

import com.mygame.db.entity.manager.TaskManager;
import com.mygame.xinyue.dataconfig.TaskDataConfig;


/**
 * 管理任务进度接口
 */
public interface ITaskProgress {
     //更新任务进度的接口,taskDataConfig是任务的配置数据，data是任务进度变化的进度，因为这个值的类型是多个的，有的是int
     //有的是String，有的是list等，所以使用Object类

     /**
      * 更新任务进度
      * @param taskManager
      * @param taskDataConfig
      * @param data
      */
     void updateProgress(TaskManager taskManager, TaskDataConfig taskDataConfig, Object data);

     /**
      * 判断任务是否完成，表示可以获取奖励
      * @param taskManager
      * @param taskDataConfig
      * @return
      */
     boolean isFinish(TaskManager taskManager, TaskDataConfig taskDataConfig);


     /**
      * 获取当前任务进度值
      * @param taskManager
      * @param taskDataConfig
      * @return
      */
     Object getProgessValue(TaskManager taskManager, TaskDataConfig taskDataConfig);
}
