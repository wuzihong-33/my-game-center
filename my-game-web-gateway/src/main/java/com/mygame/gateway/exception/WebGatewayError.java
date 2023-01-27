package com.mygame.gateway.exception;

public enum WebGatewayError {
    UNKNOWN(-2, "网关服务器未知异常"),;
    
    private int errorCode;
    private String errorDesc;
    WebGatewayError(int errorCode, String errorDesc) {
        this.errorCode = errorCode;
        this.errorDesc = errorDesc;
    }

    public int getErrorCode() {
        return this.errorCode;
    }

    public String getErrorDesc() {
        return this.errorDesc;
    }

    @Override
    public String toString() {
        return "errorCode:" + errorCode + "; errorMsg:" + this.errorDesc;
    }
}
