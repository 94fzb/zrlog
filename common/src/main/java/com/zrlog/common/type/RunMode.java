package com.zrlog.common.type;

import java.util.Objects;

public enum RunMode {

    JAVA, NATIVE, NATIVE_AGENT, NATIVE_LAMBDA;

    public boolean isNative() {
        return this == NATIVE || this == NATIVE_LAMBDA;
    }

    public boolean isLambda() {
        return this == NATIVE_LAMBDA;
    }


    public static boolean isLambdaMode() {
        String value = System.getenv("AWS_LAMBDA_FUNCTION_NAME");
        return Objects.nonNull(value);
    }
}
