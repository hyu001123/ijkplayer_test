package com.example.administrator.ijkplayer_test;



import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import org.litepal.LitePalApplication;

public class mApplication extends LitePalApplication{

    private static final boolean Constants_DEBUG =true;
    private static mApplication instance;
    private RefWatcher mRefWatcher;
    @Override
    public void onCreate() {
        super.onCreate();
        instance=this;
        mRefWatcher= Constants_DEBUG ? LeakCanary.install(this): RefWatcher.DISABLED;
    }

    public static mApplication getInstance(){
        return instance;
    }

    public static RefWatcher getRefWatcher(){
        return getInstance().mRefWatcher;
    }
}
