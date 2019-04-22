package com.xdz.seekwork.network.api;

import java.io.Serializable;

public class SrvResult<T> implements Serializable {

    public String getMsg() {
        return Msg;
    }

    public void setMsg(String msg) {
        Msg = msg;
    }

    public int getStatus() {
        return Status;
    }

    public void setStatus(int status) {
        Status = status;
    }

    public T getData() {
        return Data;
    }

    public void setData(T data) {
        Data = data;
    }

    private String Msg;

    private int Status;

    private T Data;

}
