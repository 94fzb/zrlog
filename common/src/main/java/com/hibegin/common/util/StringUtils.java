package com.hibegin.common.util;


public class StringUtils {

    private StringUtils() {
    }

    public static boolean isEmpty(String str) {
        return str == null || "".equals(str);
    }

    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }
}
