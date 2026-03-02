package com.mobilelearning.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtil {
    
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
    
    public static String format(Date date) {
        if (date == null) {
            return "-";
        }
        return sdf.format(date);
    }
    
    public static String formatLong(Date date) {
        if (date == null) {
            return "-";
        }
        SimpleDateFormat longFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        return longFormat.format(date);
    }
    
    public static String formatTime(long milliseconds) {
        long seconds = milliseconds / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        
        if (hours > 0) {
            return String.format(Locale.CHINA, "%d小时%d分钟", hours, minutes % 60);
        } else if (minutes > 0) {
            return String.format(Locale.CHINA, "%d分钟", minutes);
        } else {
            return String.format(Locale.CHINA, "%d秒", seconds % 60);
        }
    }
}
