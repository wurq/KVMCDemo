package com.arch.session;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;

import com.arch.kvmcdemo.MainService;
import com.arch.util.AppProfile;
import com.arch.util.ProcessUtils;
import com.arch.util.ServiceUtils;

import java.lang.ref.WeakReference;

import static com.arch.util.ConstCommon.SessionConnect.MSG_TRY_CONNECT_SESSION_ENGINE;

public class SessionCenter {

    public static final String TAG = "SessionCenter";
    private static final int MAX_CONNECT_RETRY_COUNT = 10;


    static SessionCenterHandler mSessionCenterHandler;

    volatile static int sConnectRetryCount;
    private static SessionCenter mInstance;

    private volatile boolean mIsSessionStop = true;

    // 判断session进程有没有跟随main进程启动
    private volatile boolean mIsSessionConnect = false;

    public static boolean sIsServiceOn;

    ISessionConnection mSessionConnection ;
    ISessionCallback mSessionCallback;


    private SessionCenter() {
        mSessionCenterHandler = new SessionCenterHandler (this);
    }

    // ------------------------------------------------------
    public static SessionCenter getInstance() {
        if (mInstance == null) {
            synchronized (SessionCenter.class) {
                if (mInstance == null) {
                    mInstance = new SessionCenter();
                }
            }
        }
        return mInstance;
    }


    /**
     * （异步）连通到后台进程AIDL通道.
     */
    public synchronized void connectSessionEngineAsync() {
        Log.i(TAG, "connect session engine async");

        mIsSessionStop = false;
        sConnectRetryCount = 0;

        if (!mIsSessionConnect) {
            Message msg = Message.obtain();
            msg.what = MSG_TRY_CONNECT_SESSION_ENGINE;
            mSessionCenterHandler.sendMessageAtFrontOfQueue(msg);
        }

        // 在异步启动session的同时，启动main所在进程的service
        // 由于所在进程暂定为默认进程
        if (!MainService.sIsServiceOn) {
            mSessionCenterHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent fsIntent = new Intent(AppProfile.getContext(),MainService.class);
                    ServiceUtils.startServiceSafely(AppProfile.getContext(),fsIntent);
                }
            },1000);
        }

//
//        if (!LiveService.sIsServiceOn) {
//            mSessionCenterHandler.postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    Intent fsIntent = new Intent(AppProfile.getContext(),LiveService.class);
//                    ServiceUtils.startServiceSafely(AppProfile.getContext(),fsIntent);
//                }
//            }, 1000);
//        }
    }


    class SessionCenterHandler extends Handler {

        WeakReference<SessionCenter> mSessionCenter;

        public SessionCenterHandler(SessionCenter sessionCenter) {
            super(AppProfile.getContext().getMainLooper());
            mSessionCenter = new WeakReference<SessionCenter>(sessionCenter);
        }

        @Override
        public void handleMessage(Message msg) {
            SessionCenter sessionCenter = mSessionCenter.get ();
            if (sessionCenter != null) {
                switch (msg.what) {
                    case MSG_TRY_CONNECT_SESSION_ENGINE: {
                        removeMessages (msg.what);

                        if (!sessionCenter.mIsSessionConnect) {
                            boolean ret = sessionCenter.bindSessionEngine ();

                            if (ret) {
                                sendEmptyMessageDelayed (MSG_TRY_CONNECT_SESSION_ENGINE, 1000);
                            } else {
                                sendEmptyMessageDelayed (MSG_TRY_CONNECT_SESSION_ENGINE, 300);
                            }

                            // 当连接后台次数大于阈值时，重启
                            sConnectRetryCount++;
                            if (sConnectRetryCount >= MAX_CONNECT_RETRY_COUNT) {
                                sConnectRetryCount = 0;

                                ProcessUtils.killProcess (android.os.Process.myPid ());

                            }
                        }
                        else {
                        }
                    }
                    break;

                }

            }
        }
    }

    private boolean bindSessionEngine() {
        Log.i(TAG, "bind session engine");
        // 启动并连接后台
        Intent intent = new Intent(AppProfile.getContext (),SessionEngine.class);// new Intent();
        boolean ret = false;
        try {
            ret = AppProfile.getContext ().bindService(intent, mSessionEngineConn, Service.BIND_AUTO_CREATE);
        } catch (Exception e) {
            // do nothing
        }
        return ret;
    }


    /**
     * 后台进程的service connection。
     */
    ServiceConnection mSessionEngineConn = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i(TAG, "back engine is connected");
            onBackServiceConnected(name, service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i(TAG, "back engine is disconnected");
            onBackServiceDisConnected(name);
        }

        @Override
        public void onBindingDied(ComponentName name) {

        }

        @Override
        public void onNullBinding(ComponentName name) {

        }
    };

    private void onBackServiceDisConnected(ComponentName name) {
        mIsSessionConnect = false;
        if (mSessionConnection != null) {
            try {
                mSessionConnection.unregisterCallback(mSessionCallback);
                mSessionConnection = null;
            } catch (RemoteException e) {
                // do nothing
                Log.e(TAG, "session connect error @ onServiceDisconnected");
            }
        }

//        // 如果backEngine连接断开了，而又不是主动停掉的，强制重连
//        if (!isStop() /*&& !sIsExitSoftware*/) {
//            mForeMainHandler.sendEmptyMessage(MSG_TRY_CONNECT_BACK_ENGINE);
//            Log.i(TAG, "reconnect back engine");
//        }
    }

    private void onBackServiceConnected(ComponentName name, IBinder service) {
        if (service == null) {
            mIsSessionConnect = false;
            mSessionConnection = null;
            return;
        }

        mSessionConnection = ISessionConnection.Stub.asInterface(service);
        if (mSessionConnection != null) {
            try {
                mIsSessionConnect = true;
//                sConnectRetryCount = 0;


                mSessionConnection.registerCallback(mSessionCallback);
            } catch (RemoteException e) {
                Log.e(TAG, "session register error @ onServiceConnected:" + e.getMessage(), e);
            }
        }
    }

}
