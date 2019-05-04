package com.xdz.seekwork.serialport;

import java.io.Serializable;

public class ShipmentResult implements Serializable {

    public String getResultMsg() {
        return resultMsg;
    }

    public void setResultMsg(String resultMsg) {
        this.resultMsg = resultMsg;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    private String resultMsg;

    private boolean isSuccess;

}
