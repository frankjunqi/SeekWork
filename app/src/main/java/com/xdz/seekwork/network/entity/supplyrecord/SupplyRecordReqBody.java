package com.xdz.seekwork.network.entity.supplyrecord;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by kjh08490 on 2016/12/7.
 */

public class SupplyRecordReqBody implements Serializable {
    public String deviceId;
    public List<SupplyRecordObj> record = new ArrayList<SupplyRecordObj>();
}
