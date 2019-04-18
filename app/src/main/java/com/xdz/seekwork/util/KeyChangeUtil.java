package com.xdz.seekwork.util;

import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 */

public class KeyChangeUtil {

    public static String getFlagInt(String charOne) {
        String outInt = "";
        if ("A".equals(charOne)) {
            outInt = "1";
        } else if ("B".equals(charOne)) {
            outInt = "2";
        } else if ("C".equals(charOne)) {
            outInt = "3";
        }
        return outInt;
    }

    public static String getNumber(String str) {
        if (str == null || TextUtils.isEmpty(str)) {
            return "";
        }
        String regEx = "[^0-9]";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(str);
        return m.replaceAll("").trim();
    }
}
