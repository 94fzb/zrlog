package com.zrlog.blog.web.handler;

import com.jfinal.core.JFinal;
import com.jfinal.handler.Handler;
import com.zrlog.blog.web.util.WebTools;
import com.zrlog.common.Constants;
import com.zrlog.util.BlogBuildInfoUtil;
import com.zrlog.util.I18nUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * 用于对静态文件的请求的检查
 */
public class GlobalResourceHandler extends Handler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalResourceHandler.class);
    private static final ThreadLocal<Long> REQUEST_START_TIME = new ThreadLocal<>();

    /**
     * 不希望部分技术人走后门，拦截一些不合法的请求
     */
    private static final Set<String> FORBIDDEN_URI_EXT_SET = new HashSet<>();
    private static final Set<String> FORBIDDEN_URI_SET = new HashSet<>();

    static {
        //由于程序的.jsp文件没有存放在WEB-INF目录，为了防止访问.jsp页面获得的没有数据的页面，或则是错误的页面。
        FORBIDDEN_URI_EXT_SET.add(".jsp");
        FORBIDDEN_URI_EXT_SET.add(".ftl");
        //这主要用在主题目录下面的配置文件。
        FORBIDDEN_URI_EXT_SET.add(".properties");
        //静态文件
        FORBIDDEN_URI_SET.add("/install/index.html");
        FORBIDDEN_URI_SET.add(Constants.ADMIN_URI_BASE_PATH + "/index.html");
    }

    @Override
    public void handle(final String target, HttpServletRequest request, HttpServletResponse response, boolean[] isHandled) {
        long start = System.currentTimeMillis();
        REQUEST_START_TIME.set(start);
        //便于Wappalyzer读取
        response.addHeader("X-ZrLog", BlogBuildInfoUtil.getVersion());
        try {
            if (FORBIDDEN_URI_EXT_SET.stream().anyMatch(target::endsWith) || FORBIDDEN_URI_SET.contains(target)) {
                //非法请求, 返回403
                try {
                    response.sendError(403);
                } catch (IOException ioException) {
                    throw new RuntimeException(ioException);
                }
                return;
            }
            request.setAttribute("basePath", WebTools.getHomeUrl(request));
            request.setAttribute("baseWithHostPath", WebTools.getHomeUrlWithHostNotProtocol(request));
            this.next.handle(target, request, response, isHandled);
        } finally {
            I18nUtil.removeI18n();
            //开发环境下面打印整个请求的耗时，便于优化代码
            if (BlogBuildInfoUtil.isDev()) {
                LOGGER.info("{} used time {}", request.getServletPath(), System.currentTimeMillis() - start);
            }
            REQUEST_START_TIME.remove();
        }
    }

    public static void printUserTime(String key) {
        if (JFinal.me().getConstants().getDevMode()) {
            LOGGER.info("key = " + key + " usedTime " + (System.currentTimeMillis() - REQUEST_START_TIME.get()));
        }
    }

}
