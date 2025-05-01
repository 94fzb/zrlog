package com.zrlog.business.plugin;

import com.hibegin.common.util.EnvKit;
import com.zrlog.common.Constants;
import com.zrlog.common.ZrLogConfig;
import com.zrlog.plugin.IPlugin;
import com.zrlog.util.ThreadUtils;

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

    private final ZrLogConfig zrLogConfig;

    public CacheManagerPlugin(ZrLogConfig zrLogConfig) {
        this.zrLogConfig = zrLogConfig;
    }

    @Override
    public boolean autoStart() {
        return !EnvKit.isFaaSMode();
    }

    @Override
    public boolean start() {
        if (started) {
            return true;
        }
        scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1, ThreadUtils.getThreadFactory());
        scheduledThreadPoolExecutor.scheduleAtFixedRate(() -> {
            if (System.currentTimeMillis() - Constants.getLastAccessTime() > getInitDataMaxCacheTimeout()) {
                zrLogConfig.getCacheService().refreshInitData();
            }
        }, 0, getInitDataMaxCacheTimeout(), TimeUnit.SECONDS);
        this.started = true;
        return true;
    }

    private int getInitDataMaxCacheTimeout() {
        Object dbSettingSize = Constants.getStringByFromWebSite("cache_timeout_minutes");
        if (dbSettingSize != null) {
            try {
                return (int) (Double.parseDouble(dbSettingSize.toString()) * 60 * 1000);
            } catch (Exception e) {
                //ignore
            }
        }
        return 5 * 60 * 1000;
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
}
