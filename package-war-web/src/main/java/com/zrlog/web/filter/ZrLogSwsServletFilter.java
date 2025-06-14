package com.zrlog.web.filter;

import com.hibegin.http.server.PortDetector;
import com.hibegin.http.server.config.AbstractServerConfig;
import com.hibegin.http.server.util.PathUtil;
import com.hibegin.http.server.web.SwsServletFilter;
import com.zrlog.business.service.WarUpdater;
import com.zrlog.common.Constants;
import com.zrlog.web.config.ZrLogConfigImpl;

import java.io.File;

public class ZrLogSwsServletFilter extends SwsServletFilter {


    private String getWarName() {
        String contextPath = getServletContext().getContextPath();
        if ("/".equals(contextPath) || "".equals(contextPath)) {
            return "/ROOT.war";
        } else {
            return contextPath + ".war";
        }
    }

    @Override
    protected AbstractServerConfig getServerConfig() {
        Constants.zrLogConfig = new ZrLogConfigImpl(PortDetector.detectHttpPort(), new WarUpdater(new String[0], new File(new File(PathUtil.getRootPath()).getParent() + getWarName())), getFilterConfig().getServletContext().getContextPath());
        return  Constants.zrLogConfig;
    }

    @Override
    public void init() {
        super.init();
        Constants.zrLogConfig.startPluginsAsync();
    }
}
