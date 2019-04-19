package com.arch.session;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Parcel;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;

import com.arch.live.LiveService;
import com.arch.util.AppProfile;
import com.arch.util.ServiceUtils;

public class SessionEngine extends Service {

    private final static String TAG = "SessionEngine";
    private static final int HANDLE_LIVE_ASYNC_CALL = 0x200001;

    public SessionEngine() {
    }

    /**
     * 服务是否已经启动
     */
    static boolean sIsEngineReady = false;

    /**
     * 进程间回调列表
     */
    RemoteCallbackList<ISessionCallback> mSessionCallbacks = new RemoteCallbackList<ISessionCallback>();

    ISessionConnection.Stub stub = new ISessionConnection.Stub() {

        @Override
        public boolean onTransact(int code, Parcel data, Parcel reply, int flags)
                throws RemoteException {
            try {
                Log.i(TAG, "onTransact entering...");
                return super.onTransact(code, data, reply, flags);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public int sessionCall(int ipcMsg, Bundle inBundle, Bundle inoutBundle) throws RemoteException {
            Log.i (TAG,"sessionCall entering...");
            // TODO: 2019/4/19 add ipc channel
            return 0;
        }

        @Override
        public void registerCallback(ISessionCallback callback) throws RemoteException {
            Log.i (TAG,"register callback " );
//            mSessionCallbacks.register (callback);
        }

        @Override
        public void unregisterCallback(ISessionCallback callback) throws RemoteException {
            Log.i (TAG,"unregister callback " );
//            mSessionCallbacks.unregister (callback);
        }
    };


    @Override
    public void onCreate() {
        Log.i(TAG,"onCreate entering...");

        if (!LiveService.sIsServiceOn) {
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent fsIntent = new Intent(AppProfile.getContext(),LiveService.class);
                    ServiceUtils.startServiceSafely(AppProfile.getContext(),fsIntent);
                }
            }, 1000);
        }


    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.i(TAG,"onStartCommand entering...");

//        Message msg = Message.obtain();
//        msg.what = HANDLE_LIVE_ASYNC_CALL;
//        msg.obj = intent;
//        mHandler.sendMessage(msg);

        return START_STICKY;
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
//        throw new UnsupportedOperationException("Not yet implemented");
        return stub;
    }


    private Handler mHandler = new Handler (){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HANDLE_LIVE_ASYNC_CALL: {
                    Intent intent = (Intent) msg.obj;
                    handleLiveAsyncCall(intent);
                }
                break;

                default:
                    break;
            }
        }
    };

    private void handleLiveAsyncCall(Intent intent) {
        Log.i (TAG,"handleLiveAsyncCall entering...");
    }
}
