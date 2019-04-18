package com.xdz.seekwork.network.entity.basedata;


import java.io.Serializable;

/**
 */

public class AdminCardEntity implements Serializable {

    public boolean isDel;// 软删除的标记
    public String card; // card id
    public String objectId;
    public String createdAt;
    public String updatedAt;

}
