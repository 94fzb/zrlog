package com.zrlog.web.setup.install;

import com.hibegin.common.util.LoggerUtil;
import com.hibegin.http.server.api.HttpErrorHandle;
import com.hibegin.http.server.api.HttpRequest;
import com.hibegin.http.server.config.ServerConfig;
import com.zrlog.common.Updater;
import com.zrlog.common.ZrLogConfig;
import com.zrlog.install.util.InstallNativeImageResourceUtils;
import com.zrlog.install.web.InstallAction;
import com.zrlog.install.web.InstallConstants;
import com.zrlog.install.web.config.InstallRouters;
import com.zrlog.install.web.interceptor.BlogInstallInterceptor;
import com.zrlog.web.WebSetup;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

public class InstallWebSetup implements WebSetup {

    private static final Logger LOGGER = LoggerUtil.getLogger(InstallWebSetup.class);
    private final ZrLogConfig zrLogConfig;
    private final File dbPropertiesFile;
    private final File lockFile;
    private final Updater updater;

    public InstallWebSetup(ZrLogConfig zrLogConfig, File dbPropertiesFile, File lockFile, Updater updater) {
        this.zrLogConfig = zrLogConfig;
        this.dbPropertiesFile = dbPropertiesFile;
        this.lockFile = lockFile;
        if (zrLogConfig.getServerConfig().isNativeImageAgent()) {
            InstallNativeImageResourceUtils.reg();
        }
        this.updater = updater;
    }

    @Override
    public void setup() {
        InstallConstants.installConfig = new ZrLogInstallConfig(zrLogConfig, dbPropertiesFile, lockFile, updater);
        ServerConfig serverConfig = zrLogConfig.getServerConfig();
        configureInstallErrorHandlers(serverConfig);
        InstallRouters.configRouter(serverConfig);
        zrLogConfig.getServerConfig().addInterceptor(BlogInstallInterceptor.class);
        InstallAction action = InstallConstants.installConfig.getAction();
        if (action.isInstalled()) {
            return;
        }
        LOGGER.log(Level.WARNING, "Installation is not complete. Visit http://your-host:port"
                + zrLogConfig.getServerConfig().getContextPath()
                + "/install to start installation.");
    }

    private static void configureInstallErrorHandlers(ServerConfig serverConfig) {
        HttpErrorHandle installErrorHandler = InstallConstants.installConfig.getErrorHandler();
        for (int status : new int[]{400, 403, 500}) {
            HttpErrorHandle fallback = serverConfig.getErrorHandle(status);
            serverConfig.addErrorHandle(status, (request, response, throwable) -> {
                if (isInstallApiRequest(request)) {
                    installErrorHandler.doHandle(request, response, throwable);
                } else if (fallback != null) {
                    fallback.doHandle(request, response, throwable);
                } else {
                    response.renderCode(status);
                }
            });
        }
    }

    private static boolean isInstallApiRequest(HttpRequest request) {
        if (request == null) {
            return false;
        }
        String uri = request.getUri();
        return "/api/install".equals(uri) || uri != null && uri.startsWith("/api/install/");
    }
}
