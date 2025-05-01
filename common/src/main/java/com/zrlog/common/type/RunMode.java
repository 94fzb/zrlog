package com.zrlog.common.type;

public enum RunMode {

    JAVA, NATIVE, NATIVE_AGENT;

    public boolean isNative() {
        return this == NATIVE;
    }
}
