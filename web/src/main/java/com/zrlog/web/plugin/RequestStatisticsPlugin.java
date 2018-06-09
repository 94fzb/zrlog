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
import java.util.concurrent.locks.ReentrantLock;

public class RequestStatisticsPlugin implements IPlugin {

    private Timer clickTimer = new Timer();
    private Timer saveTimer = new Timer();
    private static final String DB_KEY = "request_statistics";
    private static final String ARTICLE_DB_KEY = "article_request_statistics";


    private static List<RequestInfo> requestInfoList = Collections.synchronizedList(new ArrayList<>());
    private static Set<String> visitArticleSet = Collections.synchronizedSet(new HashSet<>());
    private ReentrantLock lock = new ReentrantLock();


    @Override
    public boolean start() {
        String value = WebSite.dao.getStringValueByName(DB_KEY);
        String articleValue = WebSite.dao.getStringValueByName(ARTICLE_DB_KEY);
        if (StringUtils.isNotEmpty(value)) {
            requestInfoList = Collections.synchronizedList(new Gson().fromJson(value, new TypeToken<Collection<RequestInfo>>() {
            }.getType()));
        }
        if (StringUtils.isNotEmpty(articleValue)) {
            visitArticleSet = Collections.synchronizedSet(new HashSet<>(new Gson().fromJson(articleValue, new TypeToken<Collection<String>>() {
            }.getType())));
        }
        saveTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                save();
            }
        }, 0, 10000);
        clickTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                for (RequestInfo requestInfo : requestInfoList) {
                    String alias = getAlias(requestInfo.getRequestUri());
                    if (StringUtils.isNotEmpty(alias) && !requestInfo.isDeal()) {
                        String key = requestInfo.getIp() + "_" + alias;
                        if (!visitArticleSet.contains(key)) {
                            Log.dao.clickAdd(alias);
                            //若不是公网地址才记录
                            if (!ZrLogUtil.isInternalHostName(requestInfo.getIp())) {
                                visitArticleSet.add(key);
                            }
                        }
                        requestInfo.setDeal(true);
                    }
                }
                save();
            }
        }, 0, 60000);
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
        lock.tryLock();
        try {
            WebSite.dao.updateByKV(DB_KEY, new Gson().toJson(requestInfoList));
            WebSite.dao.updateByKV(ARTICLE_DB_KEY, new Gson().toJson(visitArticleSet));
        } finally {
            lock.unlock();
        }
    }

    @Override
    public boolean stop() {
        clickTimer.cancel();
        saveTimer.cancel();
        return true;
    }

    public static void record(RequestInfo requestInfo) {
        requestInfoList.add(requestInfo);
    }
}

