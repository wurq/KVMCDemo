//
// Created by wurongqiu on 2019/5/10.
//
#include <jni.h>
#include "KVMC.h"

KVMC *kvmc;

extern "C" JNIEXPORT jint JNICALL
Java_com_arch_kvmc_KVMC_nativeInit(JNIEnv *env,  jclass type, jstring mmap_id, jstring rootDir) {

    const char *mmapid = env->GetStringUTFChars(mmap_id, 0);

    kvmc = new KVMC(mmapid);//KVMC::getInstance(mmapid);

    env->ReleaseStringUTFChars(mmap_id, mmapid);

    if (!rootDir) {
        return -1;
    }

    const char *kstr = env->GetStringUTFChars(rootDir, nullptr);
    if (kstr) {
        kvmc->initializeKVMC(kstr);

        kvmc->loadFromFile();

        env->ReleaseStringUTFChars(rootDir, kstr);
    }
    return 0;
}


//extern "C"
//JNIEXPORT void JNICALL
//Java_com_arch_kvmc_KVMC_jniInitialize(JNIEnv *env, jclass type, jstring rootDir) {
////    const char *rootDir = env->GetStringUTFChars(rootDir_, 0);
//
//    if (!rootDir) {
//        return;
//    }
//
//    const char *kstr = env->GetStringUTFChars(rootDir, nullptr);
//    if (kstr) {
//        kvmc->initializeKVMC(kstr);
//        env->ReleaseStringUTFChars(rootDir, kstr);
//    }
//
////    env->ReleaseStringUTFChars(rootDir_, rootDir);
//}


extern "C"
JNIEXPORT jint JNICALL
Java_com_arch_kvmc_KVMC_setString(JNIEnv *env, jobject instance, jstring str_) {

    const char *str = env->GetStringUTFChars(str_, 0);

    kvmc->setStringWithKey("kk","vall");

    env->ReleaseStringUTFChars(str_, str);

    return 0;
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_arch_kvmc_KVMC_getString(JNIEnv *env, jobject instance) {

    char *   returnValue = new char[10];
    kvmc->getStringWithKey("kk",returnValue);

    return env->NewStringUTF(returnValue);
}

