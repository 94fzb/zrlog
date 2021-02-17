package com.zrlog.blog.web.plugin;

import com.jfinal.plugin.IPlugin;
import com.zrlog.blog.web.interceptor.InitDataInterceptor;
import com.zrlog.business.cache.CacheService;
import com.zrlog.common.Constants;

import java.util.TimerTask;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 当超过指定时间后，删除缓存数据，避免缓存到脏数据一直存在。
 */
public class CacheCleanerPlugin implements IPlugin {

    private final ScheduledExecutorService scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1, r -> {
        Thread thread = new Thread(r);
        thread.setName("cache-clean-plugin-thread");
        return thread;
    });

    private final CacheService cacheService = new CacheService();

    @Override
    public boolean start() {
        scheduledThreadPoolExecutor.schedule(new TimerTask() {
            @Override
            public void run() {
                if (System.currentTimeMillis() - InitDataInterceptor.getLastAccessTime() > Constants.getInitDataMaxCacheTimeout()) {
                    cacheService.refreshInitDataCache(null, true);
                }
            }
        }, 6000, TimeUnit.SECONDS);
        return true;
    }

    @Override
    public boolean stop() {
        scheduledThreadPoolExecutor.shutdown();
        return true;
    }
}
