package com.mygame.game.common;

import com.alibaba.fastjson.JSON;

/**
 * json作为序列化和反序列化的方式
 * @param <T>
 */
public abstract class AbstractJsonGameMessage<T> extends AbstractGameMessage {
    private T bodyObj;

    public AbstractJsonGameMessage() {
        if (this.getBodyObjClass() != null) {
            try {
                bodyObj = this.getBodyObjClass().newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
                bodyObj = null;
            }

        }
    }

    @Override
    protected byte[] encode() {
        String str = JSON.toJSONString(bodyObj);
        return str.getBytes();
    }

    @Override
    protected void decode(byte[] body) {
        String str = new String(body);
        bodyObj = JSON.parseObject(str, this.getBodyObjClass());
    }

    @Override
    protected boolean isBodyMsgNull() {
        return this.bodyObj == null;
    }

    protected abstract Class<T> getBodyObjClass();

    public T getBodyObj() {
        return bodyObj;
    }

    public void setBodyObj(T bodyObj) {
        this.bodyObj = bodyObj;
    }

    @Override
    public String toString() {
        String msg = null;
        if (this.bodyObj != null) {
            msg = JSON.toJSONString(bodyObj);
        }
        return "Header:" + this.getHeader() + ", " + this.getClass().getSimpleName() + "=[bodyObj=" + msg + "]";
    }



}
