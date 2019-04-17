package com.xdz.seekwork.network.entity.takeout;

import java.io.Serializable;

/**
 * Created by kjh08490 on 2016/12/8.
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
