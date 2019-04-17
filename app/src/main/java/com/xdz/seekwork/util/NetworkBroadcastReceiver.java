package com.xdz.seekwork.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


/**
 * 监听网络变化
 * Created by kjh08490 on 2016/12/14.
 */

public class NetworkBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeInfo = manager.getActiveNetworkInfo();
            if (activeInfo != null && activeInfo.isAvailable()) {
                /////////////网络连接(如果之前是断网情况下，则连上网络需要同步所有数据到服务端)
                if (!SeekerSoftConstant.NETWORKCONNECT) {
                    SeekerSoftConstant.NETWORKCONNECT = true;
                }
            } else {
                ////////网络断开
                SeekerSoftConstant.NETWORKCONNECT = false;
            }
        }
    }

}