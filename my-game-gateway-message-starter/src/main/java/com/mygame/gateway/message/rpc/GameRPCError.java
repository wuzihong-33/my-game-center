package com.mygame.gateway.message.rpc;

import com.mygame.common.error.IServerError;

public enum GameRPCError implements IServerError{
    NOT_FIND_SERVICE_INSTANCE(101,"没有找到服务实例"),
    TIME_OUT(102,"RPC接收超时，没有消息返回"),
    ;
    private int errorCode;
    private String errorDesc;

    GameRPCError(int errorCode, String errorDesc) {
        this.errorCode = errorCode;
        this.errorDesc = errorDesc;
    }

    @Override
    public int getErrorCode() {
        return errorCode;
    }

    @Override
    public String getErrorDesc() {
        return errorDesc;
    }

    @Override
    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append("errorCode:").append(this.errorCode).append("; errorMsg:").append(this.errorDesc);
        return msg.toString();
    }
}
