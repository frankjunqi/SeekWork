package com.xdz.seekwork.util;

import android.text.TextUtils;
import android.util.Log;

/**
 */

public class LogCat {

    public static final void e(String msg) {
        if (SeekerSoftConstant.DEBUG) {
            if (TextUtils.isEmpty(msg)) {
                System.out.println("nothing msg...");
            } else {
                Log.e("Tag,", msg);
            }
        }
    }
}
