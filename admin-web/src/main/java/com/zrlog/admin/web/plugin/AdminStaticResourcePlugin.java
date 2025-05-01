package com.zrlog.admin.web.plugin;

import com.hibegin.common.BaseLockObject;
import com.hibegin.common.util.EnvKit;
import com.hibegin.common.util.FileUtils;
import com.hibegin.common.util.IOUtil;
import com.hibegin.common.util.LoggerUtil;
import com.hibegin.http.server.ApplicationContext;
import com.zrlog.admin.business.AdminConstants;
import com.zrlog.admin.business.service.AdminResource;
import com.zrlog.business.plugin.StaticSitePlugin;
import com.zrlog.common.ZrLogConfig;
import com.zrlog.data.cache.CacheServiceImpl;
import com.zrlog.model.WebSite;
import com.zrlog.util.ThreadUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

public class AdminStaticResourcePlugin extends BaseLockObject implements StaticSitePlugin {

    private static final Logger LOGGER = LoggerUtil.getLogger(AdminStaticResourcePlugin.class);

    private final AdminResource adminResource;

    private static final String ID_CACHE_KEY = "admin_static_version";

    private final Map<String, StaticSitePlugin.HandleState> handleStatusPageMap = new ConcurrentHashMap<>();
    private final String contextPath;
    private final ZrLogConfig zrLogConfig;
    private final ApplicationContext applicationContext;
    private final ReentrantLock parseLock = new ReentrantLock();
    private final ExecutorService executorService = ThreadUtils.newFixedThreadPool(20);
    private final List<File> cacheFiles = new CopyOnWriteArrayList<>();


    public AdminStaticResourcePlugin(ZrLogConfig zrLogConfig, AdminResource adminResource, String contextPath) {
        this.adminResource = adminResource;
        this.contextPath = contextPath;
        this.zrLogConfig = zrLogConfig;
        this.applicationContext = new ApplicationContext(zrLogConfig.getServerConfig());
        this.applicationContext.init();
        File cacheFolder = getCacheFile("/admin");
        if (cacheFolder.exists()) {
            FileUtils.deleteFile(cacheFolder.toString());
        }
    }

    @Override
    public boolean autoStart() {
        if (StaticSitePlugin.isDisabled()) {
            return false;
        }
        if (EnvKit.isFaaSMode()) {
            String adminResourceBuildId = new WebSite().getStringValueByName(ID_CACHE_KEY);
            return !Objects.equals(adminResourceBuildId, adminResource.getStaticResourceBuildId());
        }
        return true;
    }

    @Override
    public void copyResourceToCacheFolder(String resourceName) {
        if (adminResource.isAdminMainJs(resourceName)) {
            InputStream inputStream = CacheServiceImpl.class.getResourceAsStream(resourceName);
            String stringInputStream = IOUtil.getStringInputStream(inputStream);
            String adminPath = "admin/";
            String newStr = stringInputStream.replace("\"/admin/\"", "document.currentScript.baseURI + \"" + adminPath + "\"");
            saveToCacheFolder(new ByteArrayInputStream(newStr.getBytes()), resourceName);
            return;
        }
        StaticSitePlugin.super.copyResourceToCacheFolder(resourceName);
    }

    @Override
    public boolean start() {
        cacheFiles.clear();
        lock.lock();
        try {
            //do fetch
            doGenerator();
        } finally {
            lock.unlock();
        }
        return true;
    }

    @Override
    public boolean isStarted() {
        return lock.isLocked();
    }

    @Override
    public boolean stop() {
        return false;
    }

    private void doGenerator() {
        if (StaticSitePlugin.isDisabled()) {
            return;
        }

        //admin resource
        adminResource.getAdminStaticResourceUris().forEach(this::copyResourceToCacheFolder);
        handleStatusPageMap.clear();
        //从首页开始查找
        AdminConstants.adminResource.getAdminStaticCacheUris().forEach(uri -> {
            handleStatusPageMap.put(contextPath + uri, StaticSitePlugin.HandleState.NEW);
        });
        doFetch(zrLogConfig, applicationContext);
    }

    @Override
    public String getVersionFileName() {
        return "admin-version.txt";
    }

    @Override
    public String getSiteVersion() {
        return adminResource.getStaticResourceBuildId();
    }

    @Override
    public String getDbCacheKey() {
        return ID_CACHE_KEY;
    }

    @Override
    public String getContextPath() {
        return contextPath;
    }

    @Override
    public String getDefaultLang() {
        return "zh_CN";
    }

    @Override
    public Map<String, HandleState> getHandleStatusPageMap() {
        return handleStatusPageMap;
    }

    @Override
    public Lock getParseLock() {
        return parseLock;
    }

    @Override
    public Executor getExecutorService() {
        return executorService;
    }

    @Override
    public List<File> getCacheFiles() {
        return cacheFiles;
    }
}
