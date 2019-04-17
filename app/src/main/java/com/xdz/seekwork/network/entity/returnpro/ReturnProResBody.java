package com.xdz.seekwork.network.entity.returnpro;

import java.io.Serializable;

/**
 * Created by kjh08490 on 2016/12/8.
 */

public class ReturnProResBody implements Serializable {
    public int status;
    public String message;
    public ReturnObj data = new ReturnObj();
    public String server_time;

    public class ReturnObj implements Serializable {
        public boolean result;
        public String objectId;
    }
}
