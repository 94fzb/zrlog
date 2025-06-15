package com.zrlog.util;

import com.hibegin.common.util.LoggerUtil;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class ThreadUtils {

    static {
        LoggerUtil.getLogger(ThreadUtils.class).info("Java VirtualThread(loom) enabled");
    }

    public static Thread start(Runnable runnable) {
        return Thread.ofVirtual().start(runnable);
    }

    public static Thread unstarted(Runnable runnable) {
        return Thread.ofVirtual().unstarted(runnable);
    }

    public static ThreadFactory getThreadFactory() {
        return ThreadUtils::unstarted;
    }

    public static ExecutorService newFixedThreadPool(int maxSize) {
        return Executors.newVirtualThreadPerTaskExecutor();
    }
}
