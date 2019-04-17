package com.xdz.seekwork.network.entity.basedata;


import java.io.Serializable;

/**
 * Created by kjh08490 on 2016/11/30.
 */

public class EmpPowerEntity implements Serializable {
    public String emp;
    public boolean isDel;// 软删除的标记
    public String unit;// 周期单位
    public String product;// 产品id
    public int count;// 数量
    public int used;
    public int period;// 周期
    public String objectId;
    public String createdAt;
    public String updatedAt;

}
