package com.arch.kvmcdemo;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;

public class MainService extends Service {

    private final static String TAG = "MainService";
    private static final int HANDLE_MAIN_ASYNC_CALL = 0x100001;
    public static boolean sIsServiceOn = false;

    public MainService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.i(TAG,"onStartCommand entering...");

        Message msg = Message.obtain();
        msg.what = HANDLE_MAIN_ASYNC_CALL;
        msg.obj = intent;
        mHandler.sendMessage(msg);

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "MainService onDestroy");

        sIsServiceOn = false;
//        sInstance = null;

//        if (sKillAfterStop) {
//            sKillAfterStop = false;
//
//            SessionCenter.killProcessAsync(1000);
//        }

        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
//        throw new UnsupportedOperationException ("Not yet implemented");
        return null;
    }

    private Handler mHandler = new Handler (){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HANDLE_MAIN_ASYNC_CALL: {
                    Intent intent = (Intent) msg.obj;
                    handleMainAsyncCall(intent);
                }
                break;

                default:
                    break;
            }
        }
    };

    private void handleMainAsyncCall(Intent intent) {
        Log.i (TAG,"handleMainAsyncCall entering...");
    }


}
