package com.example.administrator.ijkplayer_test.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.lang.reflect.Method;

public class NavUtils {
    /**
     * 检查是否有虚拟按键
     * @return
     */
    public static boolean checkDeviceHasVavigationBar(Context context){
        boolean hasNavigationBar=false;
        int id=context.getResources().getIdentifier("config_showNavigationBar","bool","android");
        if(id>0){
            hasNavigationBar=context.getResources().getBoolean(id);
        }
        try {
            Class clz = Class.forName("android.os.SystemProperties");
            Method method = clz.getMethod("get", String.class);
            String navibarOverride = (String) method.invoke(clz, "qemu.hw.mainkeys");
            if(navibarOverride.equals("0")){
                hasNavigationBar=true;
            }else{
                hasNavigationBar=false;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return hasNavigationBar;
    }

    public static int getNavigationHeight(Context context){
        if(!checkDeviceHasVavigationBar(context)){
            return 0;
        }
        Resources resource = context.getResources();
        int resourceId = resource.getIdentifier("navigation_bar_height", "dimen", "android");
        int height=resource.getDimensionPixelSize(resourceId);
        return height;
    }

    public static void setEditOnFocus(Context context, EditText et){
        et.setFocusableInTouchMode(true);
        et.requestFocus();
        InputMethodManager inputMethodManager=(InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(et,0);
    }

    public static void closeSoftKeyboard(Context context){
        InputMethodManager inputMethodManager=(InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if(inputMethodManager!=null&&((Activity)context).getCurrentFocus()!=null){
            inputMethodManager.hideSoftInputFromWindow(((Activity)context).getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
