package com.zrlog.blog.web.plugin;

import com.zrlog.common.Constants;
import com.zrlog.plugin.IPlugin;

import java.util.TimerTask;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 当超过指定时间后，删除缓存数据，避免缓存到脏数据一直存在。
 */
public class CacheManagerPlugin implements IPlugin {

    private final ScheduledExecutorService scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1, Thread.ofVirtual().factory());

    @Override
    public boolean start() {
        CacheManageTimerTask cacheManageTimerTask = new CacheManageTimerTask();
        cacheManageTimerTask.run();
        scheduledThreadPoolExecutor.scheduleAtFixedRate(cacheManageTimerTask, 1, 1, TimeUnit.HOURS);
        return true;
    }

    @Override
    public boolean stop() {
        scheduledThreadPoolExecutor.shutdown();
        return true;
    }

    private static class CacheManageTimerTask extends TimerTask {


        public CacheManageTimerTask() {
            try {
                Constants.zrLogConfig.getCacheService().refreshInitDataCacheAsync(null, true).get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public void run() {
            if (System.currentTimeMillis() - Constants.getLastAccessTime() > Constants.getInitDataMaxCacheTimeout()) {
                Constants.zrLogConfig.getCacheService().refreshInitDataCacheAsync(null, true).join();
            }
        }
    }
}
