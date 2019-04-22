package com.xdz.seekwork.network.entity.error;

import java.io.Serializable;

/**
 */

public class ErrorResBody implements Serializable {
    public int status;
    public String message;
    public boolean data;
    public String server_time;
}
