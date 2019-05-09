package com.xdz.seekwork.util;

public class StringUtil {
    public StringUtil() {
    }

    public static String[] secToTimes(long time) {
        String[] timeStrs = new String[3];
        if (time <= 0L) {
            timeStrs[0] = "00";
            timeStrs[1] = "00";
            timeStrs[2] = "00";
            return timeStrs;
        } else {
            int minute = (int) (time / 60L);
            int second;
            if (minute < 60) {
                second = (int) (time % 60L);
                timeStrs[0] = "00";
                timeStrs[1] = unitFormat(minute);
                timeStrs[2] = unitFormat(second);
            } else {
                int hour = minute / 60;
                if (hour > 99) {
                    timeStrs[0] = "99";
                    timeStrs[1] = "59";
                    timeStrs[2] = "59";
                    return timeStrs;
                }

                minute %= 60;
                second = (int) (time - (long) (hour * 3600) - (long) (minute * 60));
                timeStrs[0] = unitFormat(hour);
                timeStrs[1] = unitFormat(minute);
                timeStrs[2] = unitFormat(second);
            }

            return timeStrs;
        }
    }

    private static String unitFormat(int i) {
        String retStr;
        if (i >= 0 && i < 10) {
            retStr = "0" + Integer.toString(i);
        } else {
            retStr = "" + i;
        }

        return retStr;
    }
}

