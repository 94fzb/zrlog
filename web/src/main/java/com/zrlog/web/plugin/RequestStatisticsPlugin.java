package com.zrlog.web.plugin;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hibegin.common.util.StringUtils;
import com.jfinal.plugin.IPlugin;
import com.zrlog.model.Log;
import com.zrlog.model.WebSite;
import com.zrlog.util.ZrLogUtil;
import com.zrlog.web.config.ZrLogConfig;

import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class RequestStatisticsPlugin implements IPlugin {

    private ScheduledExecutorService clickSchedule = new ScheduledThreadPoolExecutor(1, new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            thread.setName("request-statistics-click-thread");
            return thread;
        }
    });
    private ScheduledExecutorService saveSchedule = new ScheduledThreadPoolExecutor(1, new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            thread.setName("request-statistics-save-thread");
            return thread;
        }
    });
    private static final String DB_KEY = "request_statistics";
    private static final String ARTICLE_DB_KEY = "article_request_statistics";
    /**
     * 保留一周的
     */
    private static final long REMOVE_TIME = 3600 * 24 * 1000 * 7L;


    private static List<RequestInfo> requestInfoList = Collections.synchronizedList(new ArrayList<>());
    private static Set<String> visitArticleSet = Collections.synchronizedSet(new HashSet<>());
    private ReentrantLock lock = new ReentrantLock();


    @Override
    public boolean start() {
        String value = new WebSite().getStringValueByName(DB_KEY);
        String articleValue = new WebSite().getStringValueByName(ARTICLE_DB_KEY);
        if (StringUtils.isNotEmpty(value)) {
            requestInfoList = Collections.synchronizedList(new Gson().fromJson(value, new TypeToken<Collection<RequestInfo>>() {
            }.getType()));
        }
        if (StringUtils.isNotEmpty(articleValue)) {
            visitArticleSet = Collections.synchronizedSet(new HashSet<>(new Gson().fromJson(articleValue, new TypeToken<Collection<String>>() {
            }.getType())));
        }
        saveSchedule.schedule(new TimerTask() {
            @Override
            public void run() {
                save();
            }
        }, 10, TimeUnit.SECONDS);
        clickSchedule.schedule(new TimerTask() {
            @Override
            public void run() {
                List<RequestInfo> removeList = new ArrayList<>();
                for (RequestInfo requestInfo : requestInfoList) {
                    String alias = getAlias(requestInfo.getRequestUri());
                    if (StringUtils.isNotEmpty(alias) && !requestInfo.isDeal()) {
                        String key = requestInfo.getIp() + "_" + alias;
                        if (!visitArticleSet.contains(key) && ZrLogUtil.isNormalBrowser(requestInfo.getUserAgent())) {
                            new Log().clickAdd(alias);
                            //若是公网地址才记录
                            visitArticleSet.add(key);
                        }
                        requestInfo.setDeal(true);
                    }
                    if (System.currentTimeMillis() - requestInfo.getRequestTime() > REMOVE_TIME) {
                        removeList.add(requestInfo);
                    }
                }
                requestInfoList.removeAll(removeList);
                save();
            }
        }, 60, TimeUnit.SECONDS);
        return true;
    }

    private String getAlias(String tUri) {
        String uri = tUri;
        for (String router : ZrLogConfig.articleRouterList()) {
            if (uri.startsWith(router)) {
                uri = uri.substring(router.length() + 1);
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

    private void save() {
        lock.lock();
        try {
            new WebSite().updateByKV(DB_KEY, new Gson().toJson(requestInfoList));
            new WebSite().updateByKV(ARTICLE_DB_KEY, new Gson().toJson(visitArticleSet));
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean stop() {
        clickSchedule.shutdownNow();
        saveSchedule.shutdownNow();
        return true;
    }

    public static void record(RequestInfo requestInfo) {
        requestInfoList.add(requestInfo);
    }
}

