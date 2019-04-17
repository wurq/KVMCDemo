package com.arch.application;

import android.app.Application;
import android.os.Process;
import android.util.Log;

public class KVMCDemoApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(" onCreate 开始", System.currentTimeMillis() + "  Process Id：" + Process.myPid());
//
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
    }

}
