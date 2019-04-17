package com.xdz.seekwork.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import com.xdz.seekwork.SeekersoftApp;

import java.util.UUID;

/**
 * Created by kjh08490 on 2016/11/25.
 */

public class DeviceInfoTool {

    @SuppressLint("MissingPermission")
    public static String getDeviceId() {
        String deviceId = "";
        try {
            // 先获取androidid
            deviceId = Settings.Secure.getString(SeekersoftApp.getInstance().getContentResolver(), Settings.Secure.ANDROID_ID);
            // 在主流厂商生产的设备上，有一个很经常的bug，
            // 就是每个设备都会产生相同的ANDROID_ID：9774d56d682e549c
            if (TextUtils.isEmpty(deviceId) || "9774d56d682e549c".equals(deviceId)) {
                TelephonyManager telephonyManager = (TelephonyManager) SeekersoftApp.getInstance()
                        .getSystemService(Context.TELEPHONY_SERVICE);
                deviceId = telephonyManager.getDeviceId();
            }
            if (TextUtils.isEmpty(deviceId)) {
                deviceId = UUID.randomUUID().toString();
                deviceId = deviceId.replaceAll("-", "");
            }
        } catch (Exception e) {
            deviceId = UUID.randomUUID().toString();
            deviceId = deviceId.replaceAll("-", "");
        } finally {
            return deviceId;
        }
    }

    public static void handleConnect(Context mContext) {
        ConnectivityManager manager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeInfo = manager.getActiveNetworkInfo();
        if (activeInfo != null && activeInfo.isAvailable()) {
            SeekerSoftConstant.NETWORKCONNECT = true;
        } else {
            SeekerSoftConstant.NETWORKCONNECT = false;
        }
    }
}
