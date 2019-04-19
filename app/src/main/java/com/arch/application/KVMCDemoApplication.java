package com.arch.application;

import android.app.Application;
import android.content.Context;
import android.os.Process;
import android.util.Log;

import com.arch.session.SessionCenter;
import com.arch.util.AppProfile;
import com.arch.util.ProcessUtils;

public class KVMCDemoApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(" onCreate 开始", System.currentTimeMillis() + "  Process Id：" + Process.myPid());
//
        init ();
        initSessionProcess ();
    }


    private void init() {
        Context context = getApplicationContext();
        AppProfile.setContext(context);
    }

    private void initSessionProcess() {
        if(ProcessUtils.getCurrentProcessName ().contentEquals ( "com.arch.kvmcdemo")) {
            SessionCenter.getInstance ().connectSessionEngineAsync ();
        }
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
