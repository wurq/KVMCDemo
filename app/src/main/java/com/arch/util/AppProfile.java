package com.arch.util;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

public class AppProfile {
    public static final String AppName = "KVMCDemoApplication";

    public static final String PRODUCT_NAME = "KVMCDemo";

    /* package */ static Context sContext;

    static long mlAppStartTime;

    public static Context getContext() {
        return sContext;
    }


    public static void setContext(Context context) {
        sContext = context;
    }

    public static final String getPackageName() {
        return sContext.getPackageName();
    }

    public static final long getStartTime() {
        return mlAppStartTime;
    }

    public static void setStartTime( long startTime)  {
        mlAppStartTime = startTime;
    }

    public static void gotoBackground(@NonNull Context context) {
        PackageManager pm = context.getPackageManager();
        ResolveInfo homeInfo = pm.resolveActivity(new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME), 0);

        if (homeInfo == null) return;

        ActivityInfo ai = homeInfo.activityInfo;
        Intent startIntent = new Intent(Intent.ACTION_MAIN);
        startIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        startIntent.setComponent(new ComponentName (ai.packageName, ai.name));
        startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(startIntent);
    }

    public static boolean isMainProcess() {
        int pid = android.os.Process.myPid();
        ActivityManager am = (ActivityManager) sContext.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcessInfo : am.getRunningAppProcesses()) {
            if (pid == appProcessInfo.pid) {
                if (TextUtils.equals(sContext.getPackageName(), appProcessInfo.processName)) {
                    Log.i("XXXX", appProcessInfo.processName + "; uid = " + appProcessInfo.uid +
                            "; pid = " + pid);
                    return true;
                }
            }
        }
        return false;
    }

}
