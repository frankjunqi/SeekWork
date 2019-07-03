package com.xdz.seekwork.serialport;

/**
 * 出货指令实体类
 */

public class ShipmentCommad {


    /**
     * @param RealRoad 11,10 用逗号隔开
     */
    public ShipmentCommad(String RealRoad) {
        setReaRoad(RealRoad);
    }

    public String getReaRoad() {
        return ReaRoad;
    }

    public void setReaRoad(String reaRoad) {
        ReaRoad = reaRoad;
        String[] strs = reaRoad.split(",");
        if (strs != null && strs.length == 2) {
            setProHang(Integer.valueOf(strs[0]));
            setProLie(Integer.valueOf(strs[1]));
        }
    }

    public int getProHang() {
        return proHang;
    }

    public void setProHang(int proHang) {
        this.proHang = proHang;
    }

    public int getProLie() {
        return proLie;
    }

    public void setProLie(int proLie) {
        this.proLie = proLie;
    }

    private String ReaRoad;

    public int getContainerNum() {
        return containerNum;
    }

    public void setContainerNum(int containerNum) {
        this.containerNum = containerNum;
    }

    private int containerNum = 0x01;// 货柜编号 0x01 0x02 0x03 0x04 开始
    private int proHang = 0x01;// 行号
    private int proLie = 0x02;// 列号

    public boolean isGEZI() {
        return isGEZI;
    }

    public void setGEZI(boolean GEZI) {
        isGEZI = GEZI;
    }

    private boolean isGEZI = false;
}
