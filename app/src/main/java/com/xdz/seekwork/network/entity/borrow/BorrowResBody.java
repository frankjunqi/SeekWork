package com.xdz.seekwork.network.entity.borrow;

import java.io.Serializable;

/**
 * Created by kjh08490 on 2016/12/8.
 */

public class BorrowResBody implements Serializable {
    public int status;

    public String message;

    public BorrowObj data = new BorrowObj();

    public String server_time;

    public class BorrowObj implements Serializable {
        public boolean result;
        public String objectId;
    }
}
