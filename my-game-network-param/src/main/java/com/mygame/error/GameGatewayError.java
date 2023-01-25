package com.mygame.error;

import com.mygame.common.error.IServerError;

public enum GameGatewayError implements IServerError{
    TOKEN_ILLEGAL(101,"TOKEN非法"),
    TOKEN_EXPIRE(102,"TOKEN已过期"),
    REPEATED_CONNECT(103,"重复连接，可能异地登陆了"),
    ;
    private int errorCode;
    private String errorDesc;

    private GameGatewayError(int errorCode, String errorDesc) {
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
