// ISessionConnection.aidl
package com.arch.session;

import com.arch.session.ISessionCallback;

interface ISessionConnection {
    int sessionCall(in int ipcMsg, in Bundle inBundle, inout Bundle inoutBundle);
   	void registerCallback(ISessionCallback callback);
    void unregisterCallback(ISessionCallback callback);
}
