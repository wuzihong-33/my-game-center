package com.mygame.game.messagedispatcher;

import java.lang.reflect.Method;

public class DispatcherMapping {
    private Object targetObject;//处理消息的目标对象
    private Method targetMethod;//处理消息的目标方法
    public DispatcherMapping(Object targetObj, Method targetMethod) {
        this.targetObject = targetObj;
        this.targetMethod = targetMethod;
    }

    public Object getTargetObject() {
        return targetObject;
    }

    public Method getTargetMethod() {
        return targetMethod;
    }
}
