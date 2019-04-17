package com.xdz.seekwork.util;

import android.text.TextUtils;

/**
 * Created by Frank on 17/4/4.
 */

public class LogCat {

    public static final void e(String msg) {
        if (SeekerSoftConstant.DEBUG) {
            if (TextUtils.isEmpty(msg)) {
                System.out.println("nothing msg...");
            } else {
                System.out.println(msg);
            }
        }
    }
}
