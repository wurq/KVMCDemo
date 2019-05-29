package com.arch.kvmc;

import android.content.Context;

public class KVMC {


    private static KVMC _kvmc = null;
    private static String rootDir = null;


    private static String MMAP_ID = "KVMC_DEMO_ID";

    public interface LibLoader { void loadLibrary(String libName); }

    public KVMC() {

    }

    public static KVMC getInstance() {
        synchronized (KVMC.class)  {
            if(_kvmc == null)  {
                _kvmc = new KVMC();
            }
        }

        return _kvmc;
    }

//    public void init() {
//        nativeInit (MMAP_ID);
//    }

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
        nativeInit (MMAP_ID,KVMC.rootDir);
//        jniInitialize(KVMC.rootDir);

        return rootDir;
    }

//    private static native void jniInitialize(String rootDir) ;

    public static native int  nativeInit(String mmap_id , String rootDir);


    public native int setString(String str );

    public native String getString( );

}
