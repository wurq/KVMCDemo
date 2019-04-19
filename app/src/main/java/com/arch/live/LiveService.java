package com.arch.live;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

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

//        Message msg = Message.obtain();
//        msg.what = HANDLE_ASYNC_IPC_CALL;
//        msg.obj = intent;
//        mHandler.sendMessage(msg);

        //CrashHandle.getInstance().init(this);

        return START_STICKY;
    }

  /*  @Override
    public void onDestroy() {
        Log.d(TAG, "ForeService onDestroy");

        sIsServiceOn = false;
        sInstance = null;

        if (sKillAfterStop) {
            sKillAfterStop = false;

            SessionCenter.killProcessAsync(1000);
        }

        super.onDestroy();
    }



    private Handler mHandler = new Handler (){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HANDLE_ASYNC_IPC_CALL: {
                    Intent intent = (Intent) msg.obj;
                    handleAsyncCall(intent);
                }
                break;

                case TRY_TO_CLOSE_FORE_SERVICE: { //停止前台service
                    removeMessages(TRY_TO_CLOSE_FORE_SERVICE);

                    // 停service
                    ForeService.stopNow();

                    // 回收内存
                    System.gc();

                    Log.i(TAG, "前台Service 停止了");
                }
                break;


                default:
                    break;
            }
        }
    };
*/
}
