package com.hibegin.common.util;


public class BooleanUtils {

    private BooleanUtils() {
    }

    public static boolean isTrue(String bool) {
        return "true".equals(bool);
    }

    public static boolean isFalse(String bool) {
        return bool == null || "false".equals(bool);
    }
}
