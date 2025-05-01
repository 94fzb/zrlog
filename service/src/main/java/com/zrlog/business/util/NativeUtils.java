package com.zrlog.business.util;

public class NativeUtils {

    public static String getRealFileArch() {
        String os = System.getProperty("os.name");
        if (os.startsWith("Windows")) {
            return "Windows-" + System.getProperty("os.arch");
        } else if (os.startsWith("Mac")) {
            return os.replace("Mac OS X", "Darwin") + "-" + System.getProperty("os.arch").replace("aarch64", "arm64");
        }
        return os + "-" + System.getProperty("os.arch").replace("aarch64", "arm64");
    }
}
