package com.zrlog.web.filter;

import com.hibegin.common.util.EnvKit;
import com.hibegin.common.util.ObjectUtil;
import com.hibegin.http.server.PortDetector;
import com.hibegin.http.server.config.AbstractServerConfig;
import com.hibegin.http.server.web.SwsServletFilter;
import com.zrlog.common.Updater;
import com.zrlog.common.ZrLogConfig;
import com.zrlog.util.ArgsParser;
import com.zrlog.web.ApplicationUpdaterFactory;
import com.zrlog.web.config.ZrLogConfigImpl;

import java.io.File;
import java.lang.management.ManagementFactory;

/**
 * 适配标准 Servlet 容器
 */
public class ZrLogSwsServletFilter extends SwsServletFilter {

    String getWarFile() {
        String contextPath = getServletContext().getContextPath();
        String webappPath = new File(getServletContext().getRealPath("/")).getParent();
        if ("/".equals(contextPath) || "".equals(contextPath)) {
            return new File(webappPath, "ROOT.war").getAbsolutePath();
        }
        String warName = contextPath.startsWith("/") ? contextPath.substring(1) : contextPath;
        return new File(webappPath, warName + ".war").getAbsolutePath();
    }

    @Override
    protected AbstractServerConfig getServerConfig() {
        String[] runtimeArgs = ManagementFactory.getRuntimeMXBean().getInputArguments().toArray(new String[0]);
        Integer port = ObjectUtil.requireNonNullElse(PortDetector.detectHttpPort(), ArgsParser.getPort(runtimeArgs));
        String contextPath = getFilterConfig().getServletContext().getContextPath();
        return new ZrLogConfigImpl(port, getUpdater(), contextPath);
    }

    Updater getUpdater() {
        if (EnvKit.isDevMode()) {
            return ApplicationUpdaterFactory.create(new String[0], null);
        }
        return ApplicationUpdaterFactory.createWar(new File(getWarFile()));
    }

    @Override
    public void init() {
        super.init();
        ((ZrLogConfig) serverConfig).startPlugins(!EnvKit.isFaaSMode());
    }
}
