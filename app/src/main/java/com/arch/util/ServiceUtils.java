package com.arch.util;

import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;

public class ServiceUtils {
    private ServiceUtils() {
    }

    /**
     * 4.4在oppo系统下偶尔会抛出 SecurityException
     * 考虑到Android 8.0在后台调用startService时会抛出IllegalStateException
     *
     * @param context
     * @param intent
     */
    public static void startServiceSafely(Context context, Intent intent) {
        if (null == context) {
            return;
        }
        try {
            context.startService(intent);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void unbindSafely(Context context, ServiceConnection connection) {
        if (context == null || connection == null) {
            return;
        }
        try {
            context.unbindService(connection);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
