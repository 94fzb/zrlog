package com.zrlog.web.config;

import com.hibegin.common.util.LoggerUtil;
import com.zrlog.admin.business.service.AdminResource;
import com.zrlog.admin.web.token.AdminTokenService;
import com.zrlog.common.ZrLogConfig;
import com.zrlog.web.WebSetup;
import com.zrlog.web.setup.BaseWebSetup;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

public class SetupConfig {

    protected static final Logger LOGGER = LoggerUtil.getLogger(ZrLogConfig.class);

    private final ZrLogConfig zrLogConfig;
    private final File dbPropertiesFile;
    private final File installLockFile;
    private AdminTokenService adminTokenService;
    private final List<WebSetup> webSetups = new ArrayList<>();
    private boolean includeBlog;

    public boolean isIncludeBlog() {
        return includeBlog;
    }

    public AdminTokenService getAdminTokenService() {
        return adminTokenService;
    }

    public List<WebSetup> getWebSetups() {
        return webSetups;
    }

    public SetupConfig(ZrLogConfig zrLogConfig, File dbPropertiesFile,
                       File installLockFile, String contextPath) {
        this.zrLogConfig = zrLogConfig;
        this.dbPropertiesFile = dbPropertiesFile;
        this.installLockFile = installLockFile;
        this.webSetups.add(new BaseWebSetup(zrLogConfig));
        try {
            this.adminTokenService = new AdminTokenService();
            AbstractMap.SimpleEntry<WebSetup, AdminResource> webSetupObjectSimpleEntry = setupAdmin(contextPath);
            if (Objects.nonNull(webSetupObjectSimpleEntry.getKey())) {
                webSetups.add(webSetupObjectSimpleEntry.getKey());
            }
        } catch (Throwable e) {
            LOGGER.warning("Setup admin web error: " + e.getMessage());
        }
        try {
            WebSetup webSetup = setupInstall();
            webSetups.add(webSetup);
        } catch (Throwable e) {
            LOGGER.warning("Setup install web error: " + e.getMessage());
        }
        try {
            WebSetup webSetup = setupBlog(contextPath);
            webSetups.add(webSetup);
            this.includeBlog = true;
        } catch (Throwable e) {
            LOGGER.warning("Setup blog error: " + e.getMessage());
        }
    }

    private AbstractMap.SimpleEntry<WebSetup, AdminResource> setupAdmin(String contextPath) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Class.forName("com.zrlog.admin.web.token.AdminTokenService");
        Class<?> aClass = Class.forName("com.zrlog.admin.web.AdminWebSetup");
        AdminResource adminResource = (AdminResource) Class.forName("com.zrlog.admin.business.service.AdminResourceImpl").getConstructor(String.class).newInstance(contextPath);
        return new AbstractMap.SimpleEntry<>((WebSetup) aClass.getConstructor(ZrLogConfig.class, Class.forName("com.zrlog.admin.business.service.AdminResource"), String.class).newInstance(zrLogConfig, adminResource, contextPath), adminResource);
    }

    private WebSetup setupInstall() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Class<?> aClass = Class.forName("com.zrlog.web.setup.install.InstallWebSetup");
        return (WebSetup) aClass.getConstructor(ZrLogConfig.class, File.class, File.class).newInstance(zrLogConfig, dbPropertiesFile, installLockFile);
    }

    private WebSetup setupBlog(String contextPath) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Class<?> aClass = Class.forName("com.zrlog.blog.web.BlogWebSetup");
        return (WebSetup) aClass.getConstructor(ZrLogConfig.class, String.class).newInstance(zrLogConfig, contextPath);
    }
}
