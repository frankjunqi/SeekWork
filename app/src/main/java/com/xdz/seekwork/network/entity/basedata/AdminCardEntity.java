package com.xdz.seekwork.network.entity.basedata;


import java.io.Serializable;

/**
 * Created by kjh08490 on 2016/11/30.
 */

public class AdminCardEntity implements Serializable {

    public boolean isDel;// 软删除的标记
    public String card; // card id
    public String objectId;
    public String createdAt;
    public String updatedAt;

}
