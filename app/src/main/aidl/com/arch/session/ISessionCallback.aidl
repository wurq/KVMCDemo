// ISessionCallback.aidl
package com.arch.session;

interface ISessionCallback {
   	int onSessionCallback(in int callbackId, in Bundle inBundle, inout Bundle inoutBundle);
}
