package com.xdz.seekwork.serialport;

/**
 * 出货指令实体类
 */

public class ShipmentCommad {


    public ShipmentCommad(int RealRoad) {
        setReaRoad(RealRoad);
    }

    public int getReaRoad() {
        return ReaRoad;
    }

    public void setReaRoad(int reaRoad) {
        ReaRoad = reaRoad;
        if (ReaRoad > 0 && ReaRoad < 100) {
            proHang = ReaRoad / 10;
            proLie = ReaRoad % 10;
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

    private int ReaRoad;
    public int containerNum = 0x0B;// 货柜编号
    private int proHang = 0x01;// 行号
    private int proLie = 0x02;// 列号
}
