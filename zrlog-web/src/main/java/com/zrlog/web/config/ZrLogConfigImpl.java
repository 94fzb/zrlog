package com.zrlog.web.config;

import com.hibegin.http.server.SimpleWebServer;
import com.hibegin.http.server.WebServerBuilder;
import com.mysql.cj.jdbc.AbandonedConnectionCleanupThread;
import com.zrlog.common.TokenService;
import com.zrlog.common.Updater;
import com.zrlog.common.ZrLogConfig;
import com.zrlog.common.vo.PublicWebSiteInfo;
import com.zrlog.plugin.Plugins;
import com.zrlog.web.WebSiteInfoProvider;
import com.zrlog.web.inteceptor.DefaultInterceptor;
import java.util.Objects;
import java.util.logging.Level;

public class ZrLogConfigImpl extends ZrLogConfig {

    private static final String MYSQL_CLEANUP_THREAD_CLASS_NAME = "com.mysql.cj.jdbc.AbandonedConnectionCleanupThread";
    private final SetupConfig setupConfig;
    private WebServerBuilder serverBuilder;

    public ZrLogConfigImpl(Integer port, Updater updater, String contextPath) {
        super(port, updater, contextPath);
        this.setupConfig = new SetupConfig(this, dbPropertiesFile, installLockFile, contextPath, webSetups, updater);
        initializeWebSetups();
        serverConfig.getInterceptors().add(DefaultInterceptor.class);
    }

    @Override
    protected TokenService initTokenService() {
        if (!isInstalled()) {
            return null;
        }
        PublicWebSiteInfo publicWebSiteInfo = getWebSetup(WebSiteInfoProvider.class).getPublicWebSiteInfo();
        return setupConfig.buildAdminTokenService(publicWebSiteInfo.getSession_timeout());
    }

    @Override
    public Plugins getBasePluginList() {
        return new Plugins();
    }

    public void setServerBuilder(WebServerBuilder serverBuilder) {
        this.serverBuilder = serverBuilder;
    }

    private boolean isMySqlCleanupThreadAvailable() {
        try {
            Class.forName(MYSQL_CLEANUP_THREAD_CLASS_NAME, false, ZrLogConfigImpl.class.getClassLoader());
            return true;
        } catch (ClassNotFoundException | LinkageError e) {
            return false;
        }
    }

    private void shutdownMySqlCleanupThread() {
        if (Objects.nonNull(this.dataSource) && this.dataSource.isWebApi()) {
            return;
        }
        if (!isMySqlCleanupThreadAvailable()) {
            return;
        }
        try {
            AbandonedConnectionCleanupThread.checkedShutdown();
        } catch (LinkageError e) {
            LOGGER.log(Level.FINE, "MySQL cleanup thread is unavailable", e);
        }
    }

    private void destroyWebServer() {
        if (Objects.isNull(serverBuilder)) {
            return;
        }
        SimpleWebServer webServer = serverBuilder.getWebServer();
        if (Objects.isNull(webServer)) {
            return;
        }
        webServer.destroy("by stop");
    }

    @Override
    public void stop() {
        try {
            super.stop();
            shutdownMySqlCleanupThread();
        } finally {
            destroyWebServer();
        }
    }
}
