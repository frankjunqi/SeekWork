package com.xdz.seekwork.network.entity.seekwork;

import java.io.Serializable;

public class MRoad implements Serializable {

    // 1:格子 2: 弹簧
    private String CabType;

    //柜子NO (提交的时候必传)
    private String No;
    //柜子类型名称 (返回的字符串固定的几种 1:主柜 2:A 3:B 4:C)
    private String CabNo;
    //货道编号
    private String RoadCode;
    //物理地址 10,11
    private String RealCode;
    //产品名称
    private String ProductName;

    private int Capacity;

    public String getCabType() {
        return CabType;
    }

    public void setCabType(String cabType) {
        CabType = cabType;
    }

    public String getNo() {
        return No;
    }

    public void setNo(String no) {
        No = no;
    }

    public String getCabNo() {
        return CabNo;
    }

    public void setCabNo(String cabNo) {
        CabNo = cabNo;
    }

    public String getRoadCode() {
        return RoadCode;
    }

    public void setRoadCode(String roadCode) {
        RoadCode = roadCode;
    }

    public String getRealCode() {
        return RealCode;
    }

    public void setRealCode(String realCode) {
        RealCode = realCode;
    }

    public String getProductName() {
        return ProductName;
    }

    public void setProductName(String productName) {
        ProductName = productName;
    }

    public int getCapacity() {
        return Capacity;
    }

    public void setCapacity(int capacity) {
        Capacity = capacity;
    }

    public int getQty() {
        return Qty;
    }

    public void setQty(int qty) {
        Qty = qty;
    }

    public int getLackNum() {
        return LackNum;
    }

    public void setLackNum(int lackNum) {
        LackNum = lackNum;
    }

    public boolean isDis() {
        return IsDis;
    }

    public void setDis(boolean dis) {
        IsDis = dis;
    }

    //用户界面上填写的差异数量
    private int Qty;
    //补货数
    private int LackNum;

    private boolean IsDis;

    public String getBorderRoad() {
        if ("主柜".equals(getCabNo())) {
            return getRoadCode();
        }
        return getCabNo() + getRoadCode();
    }

    public int getChaLackNum() {
        return chaLackNum;
    }

    public void setChaLackNum(int chaLackNum) {
        this.chaLackNum = chaLackNum;
    }

    // 差异补货的数量
    private int chaLackNum;


    public int getContainer() {
        if ("主柜".equals(getCabNo())) {
            return 0x01;
        } else if ("A".equals(getCabNo())) {
            return 0x02;
        } else if ("B".equals(getCabNo())) {
            return 0x03;
        } else if ("C".equals(getCabNo())) {
            return 0x04;
        } else {
            return 0x01;
        }
    }

}
