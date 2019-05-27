package com.arch.util;

public class ConstCommon {
    public static class NetWorkParams {
        public static final String DATA_CAN_WATCH = "DATA_CAN_WATCH";
    }

    public static class SessionConnect {
        public static final int MSG_TRY_CONNECT_SESSION_ENGINE = 0x00001;
        public static final int MSG_TRY_START_LIVE_TEST_VIEW = 0x00002;
    }

    public static class ProcessName {
        public static final String MAIN_PROCESS = "com.arch.kvmcdemo";
        public static final String SESSION_PROCESS = "com.arch.kvmcdemo:session";
        public static final String LIVE_PROCESS = "com.arch.kvmcdemo:live";
    }

    public static class IpcMsgType {
        public static final int B2L_TEST = 0x30001;
        public static final int M2B_TEST = 0x40001;
    }
}
