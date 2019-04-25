package com.arch.ipc;

import android.os.Bundle;
import android.util.Log;

import com.arch.session.SessionEngine;

public class SessionIpcCenter extends AbsIpcCenter{

    private static final String TAG = "SessionIpcCenter";

    public static SessionIpcCenter sInstance = null;

    public static SessionIpcCenter getInstance() {
        synchronized (AbsIpcCenter.class) {
            if (sInstance == null) {
                synchronized (AbsIpcCenter.class) {
                    if (sInstance == null) {
                        sInstance = new SessionIpcCenter();
                    }
                }
            }
        }
        return sInstance;
    }

    private SessionIpcCenter() {

    }

    @Override
    public int ipcCall(int ipcMsg, Bundle inBundle, Bundle outBundle) {
        Log.i (TAG,"ipcCall ipcMsg = "+ipcMsg);
        int err = 0;
        if (SessionEngine.getSessionEngine() != null) {
            err = SessionEngine.getSessionEngine().callSessionRemoteMethod(ipcMsg, inBundle, outBundle);
        } else {
        }
        return err;
    }

    @Override
    public int onIpcCall(int ipcMsg, Bundle inBundle, Bundle outBundle) {
        Log.d(TAG,"onIpcCall entering...");
//        if (mLastForePid > 0) {
//            if (ipcMsg > 0 && ipcMsg < IpcMsg.B2F_IPC_MSG_BEGIN) {
//                mF2BSyncIpcReceiveCount++;
//                Log.i("F_IPC_COUNT", "F2B SyncIpc ReceiveCount: " + mF2BSyncIpcReceiveCount);
//                Log.d("F_IPC_COUNT", "F2B SyncIpc IpcMsg(receive): " + ipcMsg);
//            }
//        }
        return super.onIpcCall(ipcMsg, inBundle, outBundle);
    }
}
