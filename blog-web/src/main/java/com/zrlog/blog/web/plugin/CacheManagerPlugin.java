package com.zrlog.blog.web.plugin;

import com.zrlog.common.Constants;
import com.zrlog.plugin.IPlugin;

import java.util.Objects;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 当超过指定时间后，删除缓存数据，避免缓存到脏数据一直存在。
 */
public class CacheManagerPlugin implements IPlugin {

    private ScheduledExecutorService scheduledThreadPoolExecutor;

    private boolean started = false;

    @Override
    public boolean start() {
        if (started) {
            return true;
        }
        scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1, Thread.ofVirtual().factory());
        scheduledThreadPoolExecutor.scheduleAtFixedRate(new CacheManageRunnable(), 1, 1, TimeUnit.HOURS);
        this.started = true;
        return true;
    }

    @Override
    public boolean isStarted() {
        return started;
    }

    @Override
    public boolean stop() {
        if (Objects.nonNull(scheduledThreadPoolExecutor)) {
            scheduledThreadPoolExecutor.shutdown();
            scheduledThreadPoolExecutor = null;
        }
        started = false;
        return true;
    }

    private static class CacheManageRunnable implements Runnable {

        @Override
        public void run() {
            if (System.currentTimeMillis() - Constants.getLastAccessTime() > Constants.getInitDataMaxCacheTimeout()) {
                Constants.zrLogConfig.getCacheService().refreshInitDataCacheAsync(null, true).join();
            }
        }
    }
}
