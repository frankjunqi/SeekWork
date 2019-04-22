package com.xdz.seekwork.network.entity.supplyrecord;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 */

public class SupplyRecordReqBody implements Serializable {
    public String deviceId;
    public List<SupplyRecordObj> record = new ArrayList<SupplyRecordObj>();
}
