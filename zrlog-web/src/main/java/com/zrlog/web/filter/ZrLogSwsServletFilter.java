package com.zrlog.web.filter;

import com.hibegin.common.util.EnvKit;
import com.hibegin.common.util.ObjectUtil;
import com.hibegin.http.server.PortDetector;
import com.hibegin.http.server.config.AbstractServerConfig;
import com.hibegin.http.server.web.SwsServletFilter;
import com.zrlog.admin.web.plugin.WarUpdater;
import com.zrlog.common.Constants;
import com.zrlog.common.Updater;
import com.zrlog.util.ZrLogUtil;
import com.zrlog.web.util.UpdaterUtils;
import com.zrlog.web.config.ZrLogConfigImpl;

import java.io.File;
import java.lang.management.ManagementFactory;

/**
 * 适配标准 Servlet 容器
 */
public class ZrLogSwsServletFilter extends SwsServletFilter {


    private String getWarFile() {
        String contextPath = getServletContext().getContextPath();
        String webappPath = new File(getServletContext().getRealPath("/")).getParent();
        if ("/".equals(contextPath) || "".equals(contextPath)) {
            return webappPath + "/ROOT.war";
        } else {
            return webappPath + contextPath + ".war";
        }
    }

    @Override
    protected AbstractServerConfig getServerConfig() {
        Constants.zrLogConfig = new ZrLogConfigImpl(ObjectUtil.requireNonNullElse(PortDetector.detectHttpPort(), ZrLogUtil.getPort(ManagementFactory.getRuntimeMXBean().getInputArguments().toArray(new String[0]))),
                getUpdater(), getFilterConfig().getServletContext().getContextPath());
        return Constants.zrLogConfig;
    }

    private Updater getUpdater() {
        if (EnvKit.isDevMode()) {
            return UpdaterUtils.getUpdater(new String[0], null);
        }
        return new WarUpdater(new File(getWarFile()));
    }

    @Override
    public void init() {
        super.init();
        Constants.zrLogConfig.startPlugins(!EnvKit.isFaaSMode());
    }
}
