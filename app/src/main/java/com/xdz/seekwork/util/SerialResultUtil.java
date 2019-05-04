package com.xdz.seekwork.util;

import android.text.TextUtils;
import android.util.Log;

import com.xdz.seekwork.serialport.ShipmentResult;

public class SerialResultUtil {


    public static ShipmentResult handleResult(String resultStr) {
        ShipmentResult shipmentResult = new ShipmentResult();

        // 串口没有响应数据
        if (TextUtils.isEmpty(resultStr)) {
            shipmentResult.setSuccess(false);
            shipmentResult.setResultMsg("串口无响应：请检查串口设备链接情况。");
            return shipmentResult;
        }

        // 螺纹柜  判断 开始4位是FF0E && 最后2位是FE && 总长度28位 && 20和21位 A0表示则成功出货，0F表示：失败 （电机没插好 或 限位开关有问题 或 电机卡死没有转动）
        if (resultStr.length() == 28 && resultStr.startsWith("FF0E")) {
            String result = resultStr.substring(18, 20);
            Log.e("tag = ", result);
            if ("A0".equals(result)) {
                shipmentResult.setSuccess(true);
                shipmentResult.setResultMsg("出货成功。");
            } else {
                shipmentResult.setSuccess(false);
                shipmentResult.setResultMsg("出货失败：电机没插好 或 限位开关有问题 或 电机卡死没有转动。");
            }
        } else if (resultStr.length() == 28 && resultStr.startsWith("FF0B")) {
            // 格子柜 判断 总长度22位 开始4位是FF0B; 格子柜没有成功状态
            shipmentResult.setSuccess(true);
            shipmentResult.setResultMsg("柜门打开成功。");
        } else {
            // 肯定失败操作
            shipmentResult.setSuccess(false);
            shipmentResult.setResultMsg("串口指令错误：货道号超出范围。");
        }
        return shipmentResult;
    }
}
