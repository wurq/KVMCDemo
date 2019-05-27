//
// Created by wurongqiu on 2019/5/13.
//


#ifndef KVMC_LOG
#define KVMC_LOG

#include <android/log.h>
#include <cstdint>
#include <cstring>
#include <errno.h>

#include <string>

//#ifdef ENABLE_MMKV_LOG

using namespace std;

#define APPNAME "KVMC"

bool g_isLogRedirecting = false;

#define __filename__ (strrchr(__FILE__, '/') + 1)


#define KVMCError(format, ...)                                          \
    _KVMCLogWithLevel(ANDROID_LOG_ERROR, __filename__, __func__, __LINE__, format, ##__VA_ARGS__)

//void KVMCError() {
//    _KVMCLogWithLevel(ANDROID_LOG_ERROR,)
//}

void _KVMCLogWithLevel(
        android_LogPriority level,
        const char *file,
        const char *func,
        int line,
        const char *format, ...)
        {
//    if (level >= g_currentLogLevel)

    {
        string message;
        char buffer[16];

        va_list args;
        va_start(args, format);
        auto length = std::vsnprintf(buffer, sizeof(buffer), format, args);
        va_end(args);

        if (length < 0) { // something wrong
            message = {};
        } else if (length < sizeof(buffer)) {
            message = string(buffer, length);
        } else {
            message.resize(length, '\0');
            va_start(args, format);
            std::vsnprintf(message.data(), length + 1, format, args);
            va_end(args);
        }

//        if (g_isLogRedirecting) {
////            kvmcLog((int) level, file, line, func, message);
//        } else {
            __android_log_print(level, APPNAME, "<%s:%d::%s> %s", file, line, func, message.c_str());
//        }
    }
}

#endif    // KVMC_LOG