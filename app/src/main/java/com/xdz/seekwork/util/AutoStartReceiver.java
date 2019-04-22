package com.xdz.seekwork.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.xdz.seekwork.MainActivity;


/**
 */

public class AutoStartReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // 开机启动
        Intent i = new Intent(context, MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }
}