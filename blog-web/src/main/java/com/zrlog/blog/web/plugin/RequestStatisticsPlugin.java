package com.zrlog.blog.web.plugin;

import com.hibegin.common.util.StringUtils;
import com.jfinal.plugin.IPlugin;
import com.zrlog.common.Constants;
import com.zrlog.model.Log;
import com.zrlog.util.ZrLogUtil;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TimerTask;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class RequestStatisticsPlugin implements IPlugin {

    /**
     * 保留2分钟的，2分钟内不重复记数量
     */
    private static final long REMOVE_TIME = 2 * 1000 * 60L;
    private static final List<RequestInfo> requestInfoList = Collections.synchronizedList(new ArrayList<>());
    private static final ReentrantLock saveLock = new ReentrantLock();
    private final ScheduledExecutorService clickSchedule = new ScheduledThreadPoolExecutor(1, r -> {
        Thread thread = new Thread(r);
        thread.setName("request-statistics-click-thread");
        return thread;
    });

    public static void record(RequestInfo requestInfo) {
        saveLock.lock();
        try {
            requestInfoList.add(requestInfo);
        } finally {
            saveLock.unlock();
        }
    }

    @Override
    public boolean start() {
        clickSchedule.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                saveLock.lock();
                try {
                    List<RequestInfo> removeList = new ArrayList<>();
                    for (RequestInfo requestInfo : requestInfoList) {
                        String alias = getAlias(requestInfo.getRequestUri());
                        if (StringUtils.isNotEmpty(alias) && !requestInfo.isDeal()) {
                            if (ZrLogUtil.isNormalBrowser(requestInfo.getUserAgent())) {
                                new Log().clickAdd(alias);
                            }
                            requestInfo.setDeal(true);
                        }
                        if (System.currentTimeMillis() - requestInfo.getRequestTime() > REMOVE_TIME) {
                            removeList.add(requestInfo);
                        }
                    }
                    requestInfoList.removeAll(removeList);
                } catch (Exception e) {
                    LoggerFactory.getLogger(RequestStatisticsPlugin.class).error("Save error", e);
                } finally {
                    saveLock.unlock();
                }
            }
        }, 0, 10, TimeUnit.SECONDS);
        return true;
    }

    private String getAlias(String tUri) {
        String uri = tUri;
        for (String router : Constants.articleRouterList()) {
            if (uri.startsWith(router)) {
                uri = uri.substring(router.length());
            }
            if (uri.endsWith(".html")) {
                uri = uri.substring(0, uri.length() - ".html".length());
            }
        }
        if (uri.startsWith("/")) {
            uri = uri.substring(1);
        }
        if (uri.contains("/")) {
            return "";
        }
        return uri;
    }

    @Override
    public boolean stop() {
        clickSchedule.shutdownNow();
        return true;
    }
}

