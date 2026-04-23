package com.zrlog.web.config;

import com.hibegin.common.util.LoggerUtil;
import com.zrlog.admin.web.token.AdminTokenService;
import com.zrlog.common.Updater;
import com.zrlog.common.ZrLogConfig;
import com.zrlog.web.WebSetup;
import com.zrlog.web.WebSetupContext;
import com.zrlog.web.WebSetupProvider;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.Set;
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
        Set<String> disableModules = parseDisableModules();
        WebSetupContext webSetupContext = new WebSetupContext(zrLogConfig, dbPropertiesFile, installLockFile, contextPath, updater);
        List<WebSetupProvider> webSetupProviders = loadWebSetupProviders();
        LOGGER.info("Discovered web modules: " + names(webSetupProviders));
        if (!disableModules.isEmpty()) {
            LOGGER.info("Disabled web modules: " + disableModules);
        }
        List<String> loadedModules = new ArrayList<>();
        for (WebSetupProvider webSetupProvider : webSetupProviders) {
            String name = providerName(webSetupProvider);
            if (disableModules.contains(name)) {
                LOGGER.info("Skip disabled web module: " + name);
                continue;
            }
            try {
                WebSetup webSetup = webSetupProvider.create(webSetupContext);
                if (Objects.nonNull(webSetup)) {
                    webSetups.add(webSetup);
                    loadedModules.add(name);
                } else {
                    LOGGER.warning("Skip web module " + name + ", provider returned null WebSetup");
                }
            } catch (Throwable e) {
                LOGGER.warning("Setup " + name + " web error: " + e.getMessage());
            }
        }
        LOGGER.info("Loaded web modules: " + loadedModules);
    }

    private static Set<String> parseDisableModules() {
        String disableModulesEnv = Objects.requireNonNullElse(System.getenv("DISABLE_MODULES"), "");
        return List.of(disableModulesEnv.split(",")).stream()
                .map(String::trim)
                .filter(e -> !e.isEmpty())
                .collect(Collectors.toSet());
    }

    private static List<WebSetupProvider> loadWebSetupProviders() {
        Map<String, WebSetupProvider> webSetupProviderMap = new LinkedHashMap<>();
        List<WebSetupProvider> discoveredProviders = new ArrayList<>();
        ServiceLoader.load(WebSetupProvider.class).stream().forEach(provider -> {
            try {
                discoveredProviders.add(provider.get());
            } catch (Throwable e) {
                LOGGER.warning("Load web module provider " + provider.type().getName() + " error: " + e.getMessage());
            }
        });
        discoveredProviders.stream()
                .sorted(Comparator.comparingInt(WebSetupProvider::order).thenComparing(SetupConfig::providerName))
                .forEach(webSetupProvider -> {
                    String name = providerName(webSetupProvider);
                    if (name.isEmpty()) {
                        LOGGER.warning("Skip unnamed web module provider: " + webSetupProvider.getClass().getName());
                        return;
                    }
                    if (webSetupProviderMap.containsKey(name)) {
                        LOGGER.warning("Skip duplicated web module provider " + webSetupProvider.getClass().getName()
                                + ", module name: " + name + ", used provider: " + webSetupProviderMap.get(name).getClass().getName());
                        return;
                    }
                    webSetupProviderMap.put(name, webSetupProvider);
                });
        return new ArrayList<>(webSetupProviderMap.values());
    }

    private static List<String> names(List<WebSetupProvider> webSetupProviders) {
        List<String> names = new ArrayList<>();
        for (WebSetupProvider webSetupProvider : webSetupProviders) {
            names.add(providerName(webSetupProvider));
        }
        return names;
    }

    private static String providerName(WebSetupProvider webSetupProvider) {
        return Objects.requireNonNullElse(webSetupProvider.name(), "").trim();
    }
}
