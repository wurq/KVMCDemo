package com.arch.kvmc;

import android.content.Context;

public class KVMC {

    private static String rootDir = null;

    public interface LibLoader { void loadLibrary(String libName); }

    public KVMC() {

    }

    public void init() {
        nativeInit ();
    }

    // call on program start
    public static String initialize(Context context) {
        String root = context.getFilesDir().getAbsolutePath() + "/kvmc";
        return initialize(root, null);
    }

    public static String initialize(String rootDir, LibLoader loader) {
        if (loader != null) {
            if (BuildConfig.FLAVOR.equals("other")) {
                loader.loadLibrary("c++_shared");
            }
            loader.loadLibrary("kvmc");
        } else {
            if (BuildConfig.FLAVOR.equals("other")) {
                System.loadLibrary("c++_shared");
            }
            System.loadLibrary("kvmc");
        }
        KVMC.rootDir = rootDir;
        jniInitialize(KVMC.rootDir);
        return rootDir;
    }

    private static native void jniInitialize(String rootDir) ;

    public native int  nativeInit( );


    public native int setString(String str );

    public native String getString( );
}
