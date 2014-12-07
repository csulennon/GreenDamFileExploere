package com.cmcm.greendamexplorer.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TextUtil {

    public static final long SIZE_KB = 1024L;
    public static final long SIZE_MB = 1024 * 1024L;
    public static final long SIZE_GB = 1024L * 1024L * 1024L;

    public static final long TIME_SIZE_SECOND = 1000;
    public static final long TIME_SIZE_MIN =  60 * 1000;
    public static final long TIME_SIZAE_HOUR = 60 * 60 * 1000;

    public static boolean isEmpty(String str) {
        if (str == null || "".equals(str.trim())) {
            return true;
        }
        return false;
    }

    /**
     * 获得时间字符串
     * 
     * @param millis
     * @return
     */
    public static String getDateStringString(long millis) {
        Date date = new Date(millis);
        String dateString = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINESE).format(date);
        return dateString;
    }

    // String time = "2009-10-21 10:35:05";// 时间格式的字符串
    /**
     * 2009-10-21 10:35:05
     * 
     * @param time
     * @return
     */
    public static Date StringToDate(String time) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINESE);
        Date s = null;
        try {
            s = formatter.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        System.out.println("TIME:::" + s);
        return s;
    }

    public static String getSizeSting(long size) {

        if (size < SIZE_KB) {
            return size + "B";
        }

        if (size < SIZE_MB) {
            return Math.round(size * 100.0 / SIZE_KB) / 100.0 + "KB";
        }

        if (size < SIZE_GB) {
            return Math.round(size * 100.0 / SIZE_MB) / 100.0 + "MB";
        }

        return Math.round(size * 100.0 / SIZE_GB) / 100.0 + "G";

    }

    public static String getDurationToString(long time) {
        String duration = null;

        if (time < TIME_SIZE_MIN) {
            return Math.round(time * 100.0 / TIME_SIZE_SECOND) / 100 + "秒";
        }

        if (time < TIME_SIZAE_HOUR) {
            duration = time / TIME_SIZE_MIN + "分 " + time % TIME_SIZE_MIN / TIME_SIZE_SECOND + "秒";
            return duration;
        }
        duration = time / TIME_SIZAE_HOUR + "小时 " + time % TIME_SIZAE_HOUR + "分 " + time % TIME_SIZE_MIN / TIME_SIZE_SECOND + "秒";
        return duration;

    }
}
