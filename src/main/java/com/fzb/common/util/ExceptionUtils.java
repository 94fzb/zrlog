package com.fzb.common.util;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ExceptionUtils {

    public static String recordStackTraceMsg(Exception e) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        e.printStackTrace(writer);
        StringBuffer buffer = stringWriter.getBuffer();
        return buffer.toString();
    }
}
