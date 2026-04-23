package com.zrlog.web.config;

import com.hibegin.common.util.LoggerUtil;
import com.zrlog.admin.web.token.AdminTokenService;
import com.zrlog.common.Updater;
import com.zrlog.common.ZrLogConfig;
import com.zrlog.web.WebSetup;
import com.zrlog.web.WebSetupContext;
import com.zrlog.web.WebSetupProvider;

import java.io.File;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.stream.Collectors;
import java.util.logging.Logger;

public class SetupConfig {

    protected static final Logger LOGGER = LoggerUtil.getLogger(ZrLogConfig.class);

    public AdminTokenService buildAdminTokenService(long sessionTimeout) {
        return new AdminTokenService(sessionTimeout);
    }

    public SetupConfig(ZrLogConfig zrLogConfig, File dbPropertiesFile,
                       File installLockFile, String contextPath,
                       List<WebSetup> webSetups,
                       Updater updater) {
        List<String> disableModules = List.of(Objects.requireNonNullElse(System.getenv("DISABLE_MODULES"), "").split(","));
        WebSetupContext webSetupContext = new WebSetupContext(zrLogConfig, dbPropertiesFile, installLockFile, contextPath, updater);
        List<WebSetupProvider> webSetupProviders = ServiceLoader.load(WebSetupProvider.class).stream()
                .map(ServiceLoader.Provider::get)
                .sorted(Comparator.comparingInt(WebSetupProvider::order).thenComparing(WebSetupProvider::name))
                .collect(Collectors.toList());
        boolean blogLoaded = false;
        for (WebSetupProvider webSetupProvider : webSetupProviders) {
            String name = webSetupProvider.name();
            if (disableModules.contains(name)) {
                continue;
            }
            try {
                WebSetup webSetup = webSetupProvider.create(webSetupContext);
                if (Objects.nonNull(webSetup)) {
                    webSetups.add(webSetup);
                    blogLoaded = blogLoaded || Objects.equals("blog", name);
                }
            } catch (Throwable e) {
                LOGGER.warning("Setup " + name + " web error: " + e.getMessage());
            }
        }
    }
}
