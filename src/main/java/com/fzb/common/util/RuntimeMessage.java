package com.fzb.common.util;

import java.io.File;

public class RuntimeMessage {

    public static SystemType getSystemRm() {
        if (File.separatorChar == '/') {
            return SystemType.LINUX;
        }
        return SystemType.WINDOWS;
    }
}
