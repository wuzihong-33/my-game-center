package com.mygame.http.response;

import com.alibaba.fastjson.JSONObject;
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

    /**
     * 将JSON字符串反序列化封装成ResponseEntity<T>实例
     * @param response
     * @param t
     * @param <T>
     * @return
     */
    public static <T> ResponseEntity<T> parseObject(String response, Class<T> t) {
        JSONObject root = JSONObject.parseObject(response);
        int code = root.getIntValue("code");
        ResponseEntity<T> result = new ResponseEntity<>();
        if (code == 0) {
            JSONObject dataJson = root.getJSONObject("data");
            T data = dataJson.toJavaObject(t);
            result.setData(data);
        } else {
            String errorMsg = root.getString("errorMsg");
            result.setCode(code);
            result.setErrorMsg(errorMsg);
        }
        return result;
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
