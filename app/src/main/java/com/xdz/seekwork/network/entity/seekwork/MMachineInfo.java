package com.xdz.seekwork.network.entity.seekwork;

import java.io.Serializable;

public class MMachineInfo implements Serializable {

    private boolean IsAuthorize;

    public boolean isAuthorize() {
        return IsAuthorize;
    }

    public void setAuthorize(boolean authorize) {
        IsAuthorize = authorize;
    }

    public String getMachineNo() {
        return MachineNo;
    }

    public void setMachineNo(String machineNo) {
        MachineNo = machineNo;
    }

    public String getContacts() {
        return Contacts;
    }

    public void setContacts(String contacts) {
        Contacts = contacts;
    }

    public String getNumbers() {
        return Numbers;
    }

    public void setNumbers(String numbers) {
        Numbers = numbers;
    }

    private String MachineNo;

    private String Contacts;

    private String Numbers;

}
