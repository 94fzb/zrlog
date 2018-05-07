package com.zrlog.web.plugin;

import com.jfinal.plugin.IPlugin;
import com.zrlog.common.Constants;
import com.zrlog.service.CacheService;
import com.zrlog.web.handler.GlobalResourceHandler;
import com.zrlog.web.interceptor.InitDataInterceptor;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 当超过指定时间后，删除缓存数据，避免缓存到脏数据一直存在。
 */
public class CacheCleanerPlugin implements IPlugin {

    private Timer timer = new Timer();

    private CacheService cacheService = new CacheService();

    @Override
    public boolean start() {
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (InitDataInterceptor.getLastAccessTime() > 0 && System.currentTimeMillis() - InitDataInterceptor.getLastAccessTime() > Constants.getMaxCacheTimeout()) {
                    InitDataInterceptor.setLastAccessTime(0L);
                    cacheService.refreshInitDataCache(GlobalResourceHandler.CACHE_HTML_PATH, null, true);
                }
            }
        }, 0, 60000);
        return true;
    }

    @Override
    public boolean stop() {
        timer.cancel();
        return true;
    }
}
