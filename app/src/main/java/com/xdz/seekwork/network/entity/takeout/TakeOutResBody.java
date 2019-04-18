package com.xdz.seekwork.network.entity.takeout;

import java.io.Serializable;

/**
 */

public class TakeOutResBody implements Serializable {
    public int status;
    public String message;
    public TakeOutObj data = new TakeOutObj();
    public String server_time;

    public class TakeOutObj implements Serializable {
        public boolean result;
        public String objectId;
    }
}
