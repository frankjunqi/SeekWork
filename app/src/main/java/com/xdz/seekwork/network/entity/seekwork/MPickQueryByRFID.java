package com.xdz.seekwork.network.entity.seekwork;

import java.io.Serializable;

public class MPickQueryByRFID implements Serializable {

    public boolean isAuthorize() {
        return IsAuthorize;
    }

    public void setAuthorize(boolean authorize) {
        IsAuthorize = authorize;
    }

    public int getPrePickOrderID() {
        return PrePickOrderID;
    }

    public void setPrePickOrderID(int prePickOrderID) {
        PrePickOrderID = prePickOrderID;
    }

    // 是否授权
    private boolean IsAuthorize;
    // 预订单ID(此字段目前前段没用
    private int PrePickOrderID;

}
