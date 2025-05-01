package com.zrlog.web.config;

import com.hibegin.common.util.LoggerUtil;
import com.zrlog.admin.web.token.AdminTokenService;
import com.zrlog.business.cache.vo.BaseDataInitVO;
import com.zrlog.common.AdminResource;
import com.zrlog.common.CacheService;
import com.zrlog.common.Updater;
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
    private AdminResource adminResource;
    private boolean includeBlog;

    public boolean isIncludeBlog() {
        return includeBlog;
    }

    public AdminResource getAdminResource() {
        return adminResource;
    }

    public AdminTokenService getAdminTokenService() {
        return adminTokenService;
    }

    public List<WebSetup> getWebSetups() {
        return webSetups;
    }

    public SetupConfig(ZrLogConfig zrLogConfig, File dbPropertiesFile,
                       File installLockFile, String contextPath,
                       CacheService<BaseDataInitVO> cacheService,
                       Updater updater) {
        this.zrLogConfig = zrLogConfig;
        this.dbPropertiesFile = dbPropertiesFile;
        this.installLockFile = installLockFile;
        this.webSetups.add(new BaseWebSetup(zrLogConfig, updater));
        try {
            this.adminTokenService = new AdminTokenService();
            AbstractMap.SimpleEntry<WebSetup, AdminResource> webSetupObjectSimpleEntry = setupAdmin(cacheService, contextPath);
            if (Objects.nonNull(webSetupObjectSimpleEntry.getKey())) {
                webSetups.add(webSetupObjectSimpleEntry.getKey());
            }
            this.adminResource = webSetupObjectSimpleEntry.getValue();
        } catch (Throwable e) {
            LOGGER.warning("Setup error: " + e.getMessage());
        }
        try {
            WebSetup webSetup = setupInstall();
            webSetups.add(webSetup);
        } catch (Throwable e) {
            LOGGER.warning("Setup error: " + e.getMessage());
        }
        try {
            WebSetup webSetup = setupBlog(contextPath);
            webSetups.add(webSetup);
            this.includeBlog = true;
        } catch (Throwable e) {
            LOGGER.warning("Setup error: " + e.getMessage());
        }
    }

    private AbstractMap.SimpleEntry<WebSetup, AdminResource> setupAdmin(CacheService<BaseDataInitVO> cacheService, String contextPath) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        Class.forName("com.zrlog.admin.web.token.AdminTokenService");
        Class<?> aClass = Class.forName("com.zrlog.admin.web.AdminWebSetup");
        Class<?> cachesServiceClass = Class.forName("com.zrlog.common.CacheService");
        AdminResource adminResource = (AdminResource) Class.forName("com.zrlog.admin.business.service.AdminResourceImpl").getConstructor(cachesServiceClass, String.class).newInstance(cacheService, contextPath);
        return new AbstractMap.SimpleEntry<>((WebSetup) aClass.getConstructor(ZrLogConfig.class, Class.forName("com.zrlog.common.AdminResource")).newInstance(zrLogConfig, adminResource), adminResource);
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
