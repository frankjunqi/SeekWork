package com.xdz.seekwork.network.entity.borrow;

import java.io.Serializable;

/**
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
