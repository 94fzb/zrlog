package com.zrlog.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class ThreadUtils {

    public static Thread start(Runnable runnable) {
        Thread thread = new Thread(runnable);
        thread.start();
        return thread;
    }

    public static boolean isEnableLoom() {
        return false;
    }

    public static Thread unstarted(Runnable runnable) {
        return new Thread(runnable);
    }

    public static ThreadFactory getThreadFactory() {
        return Thread::new;
    }

    public static ExecutorService newFixedThreadPool(int maxSize) {
        return Executors.newFixedThreadPool(maxSize);
    }
}
