//
// Created by wurongqiu on 2019/5/10.
//
#include <jni.h>
#include "KVMC.h"

KVMC *kvmc;

extern "C" JNIEXPORT jint JNICALL
Java_com_arch_kvmc_KVMC_nativeInit(JNIEnv *env, jobject instance) {

    kvmc = KVMC::getInstance(mmapID,ashmemFd);


    return 0;
}


extern "C"
JNIEXPORT void JNICALL
Java_com_arch_kvmc_KVMC_jniInitialize(JNIEnv *env, jclass type, jstring rootDir) {
//    const char *rootDir = env->GetStringUTFChars(rootDir_, 0);

    if (!rootDir) {
        return;
    }

    const char *kstr = env->GetStringUTFChars(rootDir, nullptr);
    if (kstr) {
        KVMC::initializeKVMC(kstr);
        env->ReleaseStringUTFChars(rootDir, kstr);
    }

//    env->ReleaseStringUTFChars(rootDir_, rootDir);
}


extern "C"
JNIEXPORT jint JNICALL
Java_com_arch_kvmc_KVMC_setString(JNIEnv *env, jobject instance, jstring str_) {

    const char *str = env->GetStringUTFChars(str_, 0);

    // TODO

    env->ReleaseStringUTFChars(str_, str);

}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_arch_kvmc_KVMC_getString(JNIEnv *env, jobject instance) {

    // TODO


    return env->NewStringUTF(returnValue);
}