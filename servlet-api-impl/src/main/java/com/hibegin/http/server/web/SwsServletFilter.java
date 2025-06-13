package com.hibegin.http.server.web;

import com.hibegin.http.server.ApplicationContext;
import com.hibegin.http.server.PortDetector;
import com.hibegin.http.server.handler.SwsRequestHandlerRunnable;
import com.hibegin.http.server.impl.SwsHttpServletRequestWrapper;
import com.hibegin.http.server.impl.SwsHttpServletResponseWrapper;
import com.hibegin.http.server.util.PathUtil;
import com.zrlog.business.service.WarUpdater;
import com.zrlog.common.Constants;
import com.zrlog.web.config.ZrLogConfigImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.File;

public class SwsServletFilter extends HttpFilter {

    private ApplicationContext applicationContext;


    private String getWarName() {
        String contextPath = getServletContext().getContextPath();
        if ("/".equals(contextPath) || "".equals(contextPath)) {
            return "/ROOT.war";
        } else {
            return contextPath + ".war";
        }
    }

    @Override
    public void init() {
        System.getProperties().put("sws.conf.path", getServletContext().getRealPath("/WEB-INF/"));
        Constants.zrLogConfig = new ZrLogConfigImpl(PortDetector.detectHttpPort(), new WarUpdater(new String[0], new File(new File(PathUtil.getRootPath()).getParent() + getWarName())), getFilterConfig().getServletContext().getContextPath());
        applicationContext = new ApplicationContext(Constants.zrLogConfig.getServerConfig());
        applicationContext.init();
        Constants.zrLogConfig.startPluginsAsync();
    }

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) {
        SwsHttpServletRequestWrapper requestWrapper = new SwsHttpServletRequestWrapper(req, Constants.zrLogConfig.getRequestConfig(), applicationContext);
        new SwsRequestHandlerRunnable(requestWrapper, new SwsHttpServletResponseWrapper(requestWrapper, Constants.zrLogConfig.getResponseConfig(), res), res).run();
    }
}
