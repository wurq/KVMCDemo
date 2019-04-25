package com.arch.ipc;

import android.os.Bundle;
import android.util.Log;

import java.util.HashMap;

public abstract class AbsIpcCenter {
    public static final String IPC_MSG_ID = "msg_id_530";
    private static final String TAG = "AbsIpcCenter";
    HashMap<Integer, IIpcReceiver> mIpcExtraReceiverMap = new HashMap<Integer, IIpcReceiver> ();

    /**
     * 注册IPC消息监听器
     * @param ipcMsg
     * @param receiver 监听器
     */
    public void registerIpcReceiver(int ipcMsg, IIpcReceiver receiver) {
        mIpcExtraReceiverMap.put(ipcMsg, receiver);
    }

    /**
     * 注销IPC消息监听器
     * @param receiver 监听器
     */
    public void unregisterIpcReceiver(IIpcReceiver receiver) {
        mIpcExtraReceiverMap.remove(receiver);
    }


    /**
     * 发起同步AIDL IPC调用。
     * @param ipcMsg ipc消息id
     * @param inBundle 输入数据
     * @param outBundle 输出数据
     * @return 错误码
     */
    public abstract int ipcCall(int ipcMsg, Bundle inBundle, Bundle outBundle);

    public int onIpcCall(int ipcMsg, Bundle inBundle, Bundle outBundle) {
        Log.d(TAG,"Ipc Center onIpcCall: ipcMsg = " + ipcMsg);
        int err;
        IIpcReceiver receiver = mIpcExtraReceiverMap.get(ipcMsg);
        Log.d(TAG,"Ipc Center onIpcCall: receiver = " );
        if (receiver == null) {
            err = -4;
        } else {
            err = receiver.onIpcCall(ipcMsg, inBundle, outBundle);
        }
        return err;
    }

    public interface IIpcReceiver {
        /**
         * ipc调用回调，同步方法
         *
         * @param ipcMsg    ipc消息id
         * @param inBundle  输入数据
         * @param outBundle 输出数据，当ipcMsg为异步消息时，outBundle为null
         * @return 错误码
         */
        public int onIpcCall(final int ipcMsg, Bundle inBundle, Bundle outBundle);
    }
}
