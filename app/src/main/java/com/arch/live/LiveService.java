package com.arch.live;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.arch.session.SessionCenter;


public class LiveService extends Service {

    private final static String TAG = "MainService";

    public static boolean sIsServiceOn = false;

    public LiveService() {
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
//        throw new UnsupportedOperationException ("Not yet implemented");
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.i(TAG,"onStartCommand entering...");

        // 启动Live进程之后，注册到session进程
        // 注意：由于跨进程的原因每个Client进程的SessionCenter都是独立运行的
        SessionCenter.getInstance ().connectSessionEngineAsync ();

        sIsServiceOn = true;

        return START_STICKY;
    }

}
