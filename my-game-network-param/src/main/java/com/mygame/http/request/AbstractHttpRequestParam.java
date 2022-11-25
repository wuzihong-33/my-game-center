package com.mygame.http.request;

import com.mygame.common.error.GameErrorException;
import com.mygame.common.error.IServerError;

public abstract class AbstractHttpRequestParam {
    protected IServerError error;

    public void checkParam() {
        haveError();
        if (error != null) {
            throw new GameErrorException.Builder(error).message("异常类:{}", this.getClass().getName()).build();
        }
    }
    protected abstract void haveError();
}
