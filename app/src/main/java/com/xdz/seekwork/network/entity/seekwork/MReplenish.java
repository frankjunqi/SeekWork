package com.xdz.seekwork.network.entity.seekwork;

import java.io.Serializable;
import java.util.List;

public class MReplenish implements Serializable {
    //当前刷卡的卡号
    private String cardNo;

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public String getMachineCode() {
        return MachineCode;
    }

    public void setMachineCode(String machineCode) {
        MachineCode = machineCode;
    }

    public List<MNum> getRodeList() {
        return RodeList;
    }

    public void setRodeList(List<MNum> rodeList) {
        RodeList = rodeList;
    }

    //当前设备编号
    private String MachineCode;
    //提交的数据列表
    private List<MNum> RodeList;

}
