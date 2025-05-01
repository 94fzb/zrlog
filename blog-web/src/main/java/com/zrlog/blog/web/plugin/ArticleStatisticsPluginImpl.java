package com.zrlog.blog.web.plugin;

import com.hibegin.common.util.EnvKit;
import com.zrlog.business.plugin.ArticleStatisticsPlugin;
import com.zrlog.business.plugin.RequestInfo;
import com.zrlog.util.ThreadUtils;

import java.util.Objects;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ArticleStatisticsPluginImpl implements ArticleStatisticsPlugin {

    private ScheduledExecutorService clickSchedule;
    private final ArticleStatisticsRunnable runnable;

    public ArticleStatisticsPluginImpl() {
        this.runnable = new ArticleStatisticsRunnable();
    }

    @Override
    public void record(RequestInfo requestInfo) {
        String uri = requestInfo.getRequestUri();
        //不记录后台路由，同时不记录 api 请求
        if (uri.startsWith("/admin") || uri.startsWith("/api") || uri.equals("/install")) {
            return;
        }
        runnable.addTask(requestInfo);
    }

    @Override
    public boolean start() {
        //FaaS 模式下，不需要后台任务
        if (EnvKit.isFaaSMode()) {
            return true;
        }
        if (Objects.nonNull(clickSchedule)) {
            return true;
        }
        clickSchedule = new ScheduledThreadPoolExecutor(1, ThreadUtils.getThreadFactory());
        clickSchedule.scheduleAtFixedRate(runnable, 0, 10, TimeUnit.SECONDS);
        return true;
    }

    @Override
    public boolean isStarted() {
        return Objects.nonNull(clickSchedule);
    }


    @Override
    public boolean stop() {
        if (Objects.nonNull(clickSchedule)) {
            clickSchedule.shutdownNow();
            clickSchedule = null;
        }
        return true;
    }
}

