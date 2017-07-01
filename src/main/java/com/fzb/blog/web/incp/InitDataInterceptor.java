package com.fzb.blog.web.incp;

import com.fzb.blog.service.CacheService;
import com.fzb.blog.util.BlogBuildInfoUtil;
import com.fzb.blog.util.ZrlogUtil;
import com.fzb.blog.web.config.ZrlogConfig;
import com.fzb.blog.web.controller.BaseController;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 缓存全局数据，并且存放在Servlet的Context里面，避免每次请求都需要重数据读取数据，当超过指定时间后，删除缓存数据，避免缓存到脏数据一直存在。
 */
public class InitDataInterceptor implements Interceptor {


    private static final Logger LOGGER = LoggerFactory.getLogger(InitDataInterceptor.class);
    //重最后一次请求开始计算时间，超过这个值后缓存被清除
    private static final int IDLE_TIME = 1000 * 120;
    private volatile long lastAccessTime;

    private CacheService cacheService = new CacheService();

    public InitDataInterceptor() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (lastAccessTime > 0 && System.currentTimeMillis() - lastAccessTime > IDLE_TIME) {
                    lastAccessTime = 0;
                    cacheService.refreshInitDataCache(null, true);
                }
            }
        }, 0, 60000);
    }

    private void doIntercept(Invocation invocation) {
        long start = System.currentTimeMillis();
        //未安装情况下无法设置缓存
        if (!ZrlogConfig.isInstalled()) {
            invocation.getController().render("/install/index.jsp");
        } else {
            if (invocation.getController() instanceof BaseController) {
                HttpServletRequest request = invocation.getController().getRequest();
                BaseController baseController = ((BaseController) invocation.getController());
                baseController.setAttr("requrl", ZrlogUtil.getFullUrl(request));
                cacheService.refreshInitDataCache(baseController, false);
                lastAccessTime = System.currentTimeMillis();
            }
        }
        invocation.invoke();
        //开发环境下面打印整个请求的耗时，便于优化代码
        if (BlogBuildInfoUtil.isDev()) {
            LOGGER.info(invocation.getActionKey() + " used time " + (System.currentTimeMillis() - start));
        }
    }

    @Override
    public void intercept(Invocation invocation) {
        doIntercept(invocation);
    }
}
