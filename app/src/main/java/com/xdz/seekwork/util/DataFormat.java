package com.xdz.seekwork.util;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 */

public class DataFormat {


    public static void main(String[] args) throws Exception {
        String outDate = getNowTime();
        System.out.print(outDate);
    }

    /**
     * 获取日期 时分秒为0(不区分时区)
     *
     * @return
     */
    @SuppressLint("SimpleDateFormat")
    public static Calendar getCalendar() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        String dateString = format.format(c.getTime());
        try {
            c.setTime(format.parse(dateString));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return c;
    }

    public static String getNowTime() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
        Calendar c = Calendar.getInstance();
        return format.format(c.getTime());
    }
}
