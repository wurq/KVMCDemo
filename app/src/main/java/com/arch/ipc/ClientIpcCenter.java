package com.arch.ipc;

import android.os.Bundle;
import android.util.Log;

import com.arch.session.SessionCenter;

public class ClientIpcCenter  extends AbsIpcCenter{
    private static final String TAG = "SessionIpcCenter";

    public static ClientIpcCenter sInstance = null;

    private ClientIpcCenter() {

    }
    public static ClientIpcCenter getInstance() {
        synchronized (AbsIpcCenter.class) {
            if (sInstance == null) {
                synchronized (AbsIpcCenter.class) {
                    if (sInstance == null) {
                        sInstance = new ClientIpcCenter();
                    }
                }
            }
        }
        return sInstance;
    }

    @Override
    public int ipcCall(int ipcMsg, Bundle inBundle, Bundle outBundle) {
        int err;
        if (getInstance() != null) {
            err = SessionCenter.getInstance ().ipcCallSessionEngine(ipcMsg, inBundle, outBundle);
        } else {
            err = -1;
        }
        return err;
    }

    @Override
    public int onIpcCall(int ipcMsg, Bundle inBundle, Bundle outBundle) {
        Log.i(TAG,"onIpcCall ipcMsg = " + ipcMsg);
//        if (mLastForePid > 0) {
//            if (ipcMsg > 0 && ipcMsg < IpcMsg.F2B_ASYNC_IPC_MSG_BEGIN) {
//                mB2FSyncIpcReceiveCount++;
//                Log.i("B_IPC_COUNT", "[BACK] B2F SyncIpc ReceiveCount: " + mB2FSyncIpcReceiveCount);
//                Log.d("B_IPC_COUNT", "B2F SyncIpc IpcMsg(receive): " + ipcMsg);
//            }
//        }
        return super.onIpcCall(ipcMsg, inBundle, outBundle);
    }


}
