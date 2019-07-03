package com.xdz.seekwork;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.tencent.bugly.crashreport.CrashReport;
import com.xdz.seekwork.util.DeviceInfoTool;
import com.xdz.seekwork.util.SeekerSoftConstant;

/**
 * Created by kjh08490 on 2016/11/18.
 */

public class SeekersoftApp extends Application {


    private static SeekersoftApp mSeekersoftApp;

    @Override
    public void onCreate() {
        super.onCreate();
        mSeekersoftApp = this;
        // 初始化bugly
        CrashReport.initCrashReport(this, "e14c2c60e1", true);
        // 初始化设备信息
        SeekerSoftConstant.DEVICEID = DeviceInfoTool.getDeviceId();

    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);
    }

    public static Application getInstance() {
        return mSeekersoftApp;
    }


}
