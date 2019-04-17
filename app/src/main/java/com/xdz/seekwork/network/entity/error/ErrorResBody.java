package com.xdz.seekwork.network.entity.error;

import java.io.Serializable;

/**
 * Created by kjh08490 on 2016/12/7.
 */

public class ErrorResBody implements Serializable {
    public int status;
    public String message;
    public boolean data;
    public String server_time;
}
