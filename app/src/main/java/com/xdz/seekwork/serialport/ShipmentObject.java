package com.xdz.seekwork.serialport;

/**
 * 出货指令实体类
 */

public class ShipmentObject {
    public int objectId = 0;// 时间戳

    public int containerNum = 0x0B;// 货柜编号
    public int proHang = 0x01;// 行号
    public int proLie = 0x02;// 列号

    public int proNum = 0;// 物品存放编号
}
