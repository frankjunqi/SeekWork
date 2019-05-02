package com.xdz.seekwork.network.entity.seekwork;

import java.io.Serializable;

public class MNum implements Serializable {
    public String getNo() {
        return No;
    }

    public void setNo(String no) {
        No = no;
    }

    public String getRoadCode() {
        return RoadCode;
    }

    public void setRoadCode(String roadCode) {
        RoadCode = roadCode;
    }

    public int getQty() {
        return Qty;
    }

    public void setQty(int qty) {
        Qty = qty;
    }

    //柜子编号   (此字段为获取数据时的No)
    private String No;
    //货道编号
    private String RoadCode;
    //用户界面上填写的差异数量
    private int Qty;
}
