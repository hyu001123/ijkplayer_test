package com.example.administrator.ijkplayer_test.utils;

import java.util.Formatter;
import java.util.Locale;

public class TimeUtil {

    private static  StringBuilder mFormatBuilder = new StringBuilder();
    private static  Formatter mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());

    public static String IntToHMS(int timeMs) {
        int totalSeconds = timeMs / 1000;
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;
        mFormatBuilder.setLength(0);
        if (hours > 0) {
            return mFormatter.format("%02d:%02d", hours, minutes).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }
}

