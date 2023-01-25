package com.mygame.gateway.message.context;

import com.mygame.gateway.message.channel.AbstractGameChannelHandlerContext;

public class UserEventContext<T> {
    private T dataManager;
    private AbstractGameChannelHandlerContext ctx;

    public UserEventContext(T dataManager, AbstractGameChannelHandlerContext ctx) {
        super();
        this.dataManager= dataManager;
        this.ctx = ctx;
    }

    public T getDataManager() {
        return dataManager;
    }

    public AbstractGameChannelHandlerContext getCtx() {
        return ctx;
    }

}
