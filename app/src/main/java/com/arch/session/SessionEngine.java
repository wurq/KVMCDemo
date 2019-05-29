package com.arch.session;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.DeadObjectException;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Parcel;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.util.Log;

import com.arch.ipc.AbsIpcCenter;
import com.arch.ipc.SessionIpcCenter;
import com.arch.kvmc.KVMC;
import com.arch.live.LiveService;
import com.arch.util.AppProfile;
import com.arch.util.ConstCommon;
import com.arch.util.ServiceUtils;

public class SessionEngine extends Service {

    private final static String TAG = "SessionEngine";
    private static SessionEngine sInstance;
    private static final int HANDLE_LIVE_ASYNC_CALL = 0x200001;


    /**
     * 构造函数，由系统调用
     */
    public SessionEngine() {
        super();
        sInstance = this;
    }

    public static SessionEngine getSessionEngine() {
        return sInstance;
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
            int error = 0;

            try {
                error = handleIpcCall(ipcMsg, inBundle, inoutBundle);
            }
            catch (Exception e) {
                return -1;
            }

            return error;
        }

        @Override
        public void registerCallback(ISessionCallback callback) throws RemoteException {
            Log.i (TAG,"register callback " );
            new AssertionError (callback == null);
            mSessionCallbacks.register (callback);
        }

        @Override
        public void unregisterCallback(ISessionCallback callback) throws RemoteException {
            Log.i (TAG,"unregister callback " );
            mSessionCallbacks.unregister (callback);
        }
    };


    /**
     * 处理IPC同步请求
     */
    int handleIpcCall(int ipcMsg, Bundle inBundle, Bundle outBundle) {
        Log.d(TAG,"handleIpcCall entering...");
        // 远程传递的Bundle,其classloader是未设置的
        inBundle.setClassLoader(this.getClass().getClassLoader());

        // 处理client端过来的数据请求
        return SessionIpcCenter.getInstance().onIpcCall(ipcMsg, inBundle, outBundle);
    }

    @Override
    public void onCreate() {
        Log.i(TAG,"onCreate entering...");

        // Session 进程启动后，启动除main进程之外的其他进程，main进程已启动。
        // TODO: 2019/4/19  
        //  暂定方案，后续更新方案为根据实际业务需要启动其他进程
        if (!LiveService.sIsServiceOn) {
            Log.i(TAG,"LiveService.sIsServiceOn = " + LiveService.sIsServiceOn);

            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.i(TAG,"start live process entering..." );

                    Intent fsIntent = new Intent(AppProfile.getContext(),LiveService.class);
                    ServiceUtils.startServiceSafely(AppProfile.getContext(),fsIntent);
                    Log.i(TAG,"start live process leaving..." );

                }
            }, 1000);
        }

        Message msg = Message.obtain();
        msg.what = HANDLE_LIVE_ASYNC_CALL;
        msg.obj = null;
        mHandler.sendMessageDelayed (msg,3000);
//        mHandler.sendMessage(msg);


        SessionIpcCenter.getInstance ().registerIpcReceiver (ConstCommon.IpcMsgType.M2B_TEST, new AbsIpcCenter.IIpcReceiver () {
            @Override
            public int onIpcCall(int ipcMsg, Bundle inBundle, Bundle outBundle) {
                Log.i (TAG,"inBundle testvalue2 = "+ inBundle.get ("testvalue2"));

                return 0;
            }
        });
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

    public synchronized int callSessionRemoteMethod(int ipcMsg, Bundle inBundle, Bundle outBundle) {
        int err = -1;

        if (inBundle == null) {
            inBundle = new Bundle();
        }
        if (outBundle == null) {
            outBundle = new Bundle();
        }

        try {
            int count = mSessionCallbacks.beginBroadcast();

            for (int i = count-1; i >= 0; --i) {
                try {
                    ISessionCallback callBack = mSessionCallbacks.getBroadcastItem(i);
                    if (callBack == null) {
                        continue;
                    }

                    Log.i(TAG,"callBack.onSessionCallback preparing...");
                    err = callBack.onSessionCallback (ipcMsg, inBundle, outBundle);
                } catch (DeadObjectException e) {
                    //
                } catch (RemoteException e) {
                    // 将异常吃掉不让aidl err导致crash
                    Log.w(TAG, "invoke fore engine method(" + ipcMsg + "), - aidl err");
                }
            }
        } catch (Exception e) {
            Log.w(TAG, "Session invoke  engine method(" + ipcMsg + "), - aidl err:" + e.getMessage());
        } finally {
            if (mSessionCallbacks != null) {
                mSessionCallbacks.finishBroadcast();
                Log.w(TAG, "callSessionRemoteMethod after finish Broadcast ipcMsg:" + ipcMsg);
            }
            Log.w(TAG, "Session callSessionRemoteMethod final ipcMsg: " + ipcMsg);
        }

        return err;
    }

    private void handleLiveAsyncCall(Intent intent) {
        Log.i (TAG,"handleLiveAsyncCall entering...");
        Bundle inBundle = new Bundle();
        inBundle.putInt("testvalue", 2);
        testWriteData ();
        SessionIpcCenter.getInstance ().ipcCall (ConstCommon.IpcMsgType.B2L_TEST,inBundle,null);
    }

    private void testWriteData() {
        Log.i (TAG,"testWriteData entering...");
        KVMC.getInstance ().initialize (AppProfile.getContext ());
        KVMC.getInstance ().setString ("asf");

    }
}
