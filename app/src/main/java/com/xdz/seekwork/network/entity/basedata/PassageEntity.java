package com.xdz.seekwork.network.entity.basedata;

import java.io.Serializable;

/**
 */

public class PassageEntity implements Serializable {
    public String flag;// 副柜的 ABCD 的标识
    public boolean isDel;// 软删除的标记
    public int capacity;// 容量
    public String product;// 产品id

    public String seqNo;// 货道序号
    public boolean borrowState;// 借还状态 当是借还柜子时候，这个借还的标记位置
    public int stock;// 货道库存数量
    public String used;// 借还最后者是谁
    public int whorlSize;// 螺纹尺寸
    public boolean isSend;// 销售/借还 true:销售  false:借还
    public String objectId;
    public String createdAt;
    public String updatedAt;

}
