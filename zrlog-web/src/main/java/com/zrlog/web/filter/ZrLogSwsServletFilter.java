package com.zrlog.web.filter;

import com.hibegin.common.util.EnvKit;
import com.hibegin.common.util.ObjectUtil;
import com.hibegin.http.server.PortDetector;
import com.hibegin.http.server.config.AbstractServerConfig;
import com.hibegin.http.server.web.SwsServletFilter;
import com.zrlog.common.updater.WarUpdater;
import com.zrlog.common.Constants;
import com.zrlog.common.Updater;
import com.zrlog.util.ArgsParser;
import com.zrlog.web.util.UpdaterUtils;
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
        Constants.zrLogConfig = new ZrLogConfigImpl(port, getUpdater(), contextPath);
        return Constants.zrLogConfig;
    }

    Updater getUpdater() {
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
