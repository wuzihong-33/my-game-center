package com.mygame.db.entity.manager;

import com.mygame.db.entity.Task;

public class TaskManager {
    private Task task;
    public TaskManager(Task task) {
        this.task = task;
    }

    public boolean isInitTask() {
        return task.getTaskId() != null;
    }

    public void receiveTask(String taskId) {
        task.setTaskId(taskId);
    }

    public void addValue(int value) {
       
        int newValue = (task.getValue() == null ? 0 : (int)task.getValue()) + value;
        task.setValue(newValue);
    }
    public void setValue(String value) {
      
        task.setValue(value);
    }
    public int getTaskIntValue() {
        return task.getValue() == null ? 0: (int)task.getValue();
    }
    //获取String类型的进度值
    public String getTaskStringValue() {
        return task.getValue() == null ? null : (String)task.getValue();
    }
    
    public void addManyIntValue(String key,int value) {
        Object oldValue = task.getManyValue().get(key);
        int newValue = value;
        if(oldValue != null) {
            newValue += (int)oldValue;
        }
        task.getManyValue().put(key, newValue);
    }
    public int getManayIntValue(String key) {
         Object objValue = task.getManyValue().get(key);
         int value = objValue == null? 0: (int)objValue;
         return value;
    }
    public String getNowReceiveTaskId() {
        return task.getTaskId();
    }
    
}
