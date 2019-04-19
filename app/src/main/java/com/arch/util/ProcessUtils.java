package com.arch.util;


import android.app.ActivityManager;
import android.content.Context;

import java.util.List;

@SuppressWarnings("WeakerAccess")
public class ProcessUtils {

    /**
     * 判断进程是否在运行。
     *
     * @param processName 进程名称
     */
    public static boolean isProcessAlive(String processName) {
        int pid = getPidByProcessName(processName);
        return pid > 0;
    }

    /**
     * 通过进程名字获取进程pid
     *
     * @param processName 进程名称
     * @return 进程id
     */
    public static int getPidByProcessName(String processName) {
        if (processName != null && AppProfile.getContext () != null) {
            ActivityManager activityManager = (ActivityManager)
                    AppProfile.getContext ().getSystemService(Context.ACTIVITY_SERVICE);
            if (activityManager != null) {
                List<ActivityManager.RunningAppProcessInfo> infoList =
                        activityManager.getRunningAppProcesses();
                if (infoList != null) {
                    for (ActivityManager.RunningAppProcessInfo info : infoList) {
                        if (info.processName != null &&
                                info.processName.equalsIgnoreCase(processName)) {
                            return info.pid;
                        }
                    }
                }
            }
        }
        return -1;
    }


    /**
     * 通过进程id获取进程的名字
     *
     * @param pid 进程id
     * @return 进程名称
     */
    public static String getProcessNameByPid(int pid) {
        if (AppProfile.getContext () != null) {
            ActivityManager activityManager = (ActivityManager)
                    AppProfile.getContext ().getSystemService(Context.ACTIVITY_SERVICE);
            if (activityManager != null) {
                List<ActivityManager.RunningAppProcessInfo> infoList = activityManager.getRunningAppProcesses();
                if (infoList != null) {
                    for (ActivityManager.RunningAppProcessInfo info : infoList) {
                        if (info.pid == pid) {
                            return info.processName;
                        }
                    }
                }
            }
        }
        return null;
    }

    public static String getCurrentProcessName() {
        int pid = android.os.Process.myPid();
        return getProcessNameByPid(pid);
    }

    /**
     * 杀进程。
     *
     * @param processName 进程名字
     */
    public static void killProcess(String processName) {
        int pid = getPidByProcessName(processName);
        if (pid > 0) {
            android.os.Process.killProcess(pid);
        }
    }

    /**
     * 杀进程。
     *
     * @param pid 进程id
     */
    public static void killProcess(int pid) {
        android.os.Process.killProcess(pid);
    }
}
