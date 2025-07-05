package com.zrlog.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class ThreadUtils {

    public static Thread start(Runnable runnable) {
        return Thread.ofVirtual().start(runnable);
    }

    public static boolean isEnableLoom() {
        return true;
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
