package com.example.administrator.ijkplayer_test.utils;

import android.util.Log;

public class LogUtil {
    private static boolean debug=true;
    public static void logUtilMyself(String tag,String str){
        if(debug){
            Log.i(tag,str);
        }
    }
}
