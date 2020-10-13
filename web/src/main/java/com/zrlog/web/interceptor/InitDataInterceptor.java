package com.zrlog.web.interceptor;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.zrlog.util.ZrLogUtil;
import com.zrlog.business.cache.CacheService;
import com.zrlog.web.config.ZrLogConfig;
import com.zrlog.web.controller.BaseController;
import com.zrlog.web.handler.BlogArticleHandler;

import javax.servlet.http.HttpServletRequest;

/**
 * 缓存全局数据，并且存放在Servlet的Context里面，避免每次请求都需要重数据读取数据
 */
public class InitDataInterceptor implements Interceptor {

    private static volatile long lastAccessTime;

    private final CacheService cacheService = new CacheService();

    public static long getLastAccessTime() {
        return lastAccessTime;
    }

    private void doIntercept(Invocation invocation) {
        //未安装情况下,尝试跳转去安装
        if (ZrLogConfig.isInstalled()) {
            if (invocation.getController() instanceof BaseController) {
                HttpServletRequest request = invocation.getController().getRequest();
                BaseController baseController = (BaseController) invocation.getController();
                baseController.setAttr("requrl", ZrLogUtil.getFullUrl(request));
                cacheService.refreshInitDataCache(BlogArticleHandler.CACHE_HTML_PATH, baseController, false);
                lastAccessTime = System.currentTimeMillis();
            }
        }
        invocation.invoke();
    }

    @Override
    public void intercept(Invocation invocation) {
        doIntercept(invocation);
    }
}
