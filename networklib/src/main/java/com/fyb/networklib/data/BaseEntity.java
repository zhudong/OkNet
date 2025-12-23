package com.fyb.networklib.data;

import java.io.Serializable;

public class BaseEntity<T> implements Serializable {

    private int code;
    private T data;
    private String msg;
    private boolean success;

    public boolean isSuccess() {
        return code == 2000;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}

