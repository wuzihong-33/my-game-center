package com.mygame.http.response;

import com.mygame.common.error.IServerError;


/**
 * 统一的数据通信格式
 * JSON格式
 * {
 *     "code": 0, 
 *     "data" : {}
 * }
 * @param <T>
 */
public class ResponseEntity<T> {
    private int code;
    private String errorMsg;
    private T data;

    public ResponseEntity() {
    }
    public ResponseEntity(IServerError code) {
        super();
        this.code = code.getErrorCode();
        this.errorMsg = code.getErrorDesc();
    }
    public ResponseEntity(T data) {
        super();
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
