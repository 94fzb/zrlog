package com.zrlog.web.config;

import com.hibegin.common.dao.DAO;
import com.hibegin.common.dao.DataSourceWrapperImpl;
import com.hibegin.common.util.EnvKit;
import com.hibegin.common.util.StringUtils;
import com.hibegin.common.util.http.HttpUtil;
import com.hibegin.http.server.api.HttpRequest;
import com.hibegin.http.server.config.RequestConfig;
import com.hibegin.http.server.config.ResponseConfig;
import com.hibegin.http.server.config.ServerConfig;
import com.hibegin.http.server.util.PathUtil;
import com.mysql.cj.jdbc.AbandonedConnectionCleanupThread;
import com.zrlog.admin.web.token.AdminTokenService;
import com.zrlog.business.cache.CacheServiceImpl;
import com.zrlog.business.cache.vo.BaseDataInitVO;
import com.zrlog.business.plugin.CacheManagerPlugin;
import com.zrlog.business.plugin.PluginCorePluginImpl;
import com.zrlog.business.plugin.StaticSitePlugin;
import com.zrlog.business.service.DbUpgradeService;
import com.zrlog.common.*;
import com.zrlog.common.type.RunMode;
import com.zrlog.data.DataSourceUtil;
import com.zrlog.plugin.IPlugin;
import com.zrlog.plugin.Plugins;
import com.zrlog.util.*;
import com.zrlog.web.WebSetup;
import com.zrlog.web.inteceptor.DefaultInterceptor;

import javax.sql.DataSource;
import java.io.File;
import java.util.*;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.logging.Level;

/**
 * 核心一些参数的配置。
 */
public class ZrLogConfigImpl extends ZrLogConfig {

    private final ServerConfig serverConfig;
    private final Plugins plugins;
    private final Updater updater;
    private final CacheService<BaseDataInitVO> cacheService;
    private final Map<String, Object> website = new TreeMap<>();
    private final long uptime;
    private final AdminResource adminResource;
    private DataSourceWrapperImpl dataSource;
    private final File dbPropertiesFile;
    private final AdminTokenService adminTokenService;
    private final boolean includeBlog;
    private final List<WebSetup> webSetups;
    private final File installLockFile;


    public ZrLogConfigImpl(Integer port, Updater updater, String contextPath) {
        this.uptime = System.currentTimeMillis();
        this.installLockFile = new File(PathUtil.getConfPath() + "/install.lock");
        this.dbPropertiesFile = DbUtils.initDbPropertiesFile(this);
        this.plugins = new Plugins();
        this.updater = updater;
        this.cacheService = new CacheServiceImpl();
        this.serverConfig = initServerConfig(contextPath, port);
        SetupConfig setupConfig = new SetupConfig(this, dbPropertiesFile, installLockFile, contextPath, cacheService, updater);
        this.includeBlog = setupConfig.isIncludeBlog();
        this.adminTokenService = setupConfig.getAdminTokenService();
        this.adminResource = setupConfig.getAdminResource();
        //config
        this.webSetups = setupConfig.getWebSetups();
        this.webSetups.forEach(WebSetup::setup);
        if (!includeBlog) {
            serverConfig.getInterceptors().add(DefaultInterceptor.class);
        }
    }


    /**
     * 配置的常用参数，这里可以看出来推崇使用代码进行配置的，而不是像Spring这样的通过预定配置方式。代码控制的好处在于高度可控制性，
     * 当然这也导致了很多程序员直接硬编码的问题。
     */
    @Override
    public ServerConfig getServerConfig() {
        return serverConfig;
    }


    private ServerConfig initServerConfig(String contextPath, Integer port) {
        ServerConfig serverConfig = new ServerConfig().setApplicationName("zrlog").setApplicationVersion(BlogBuildInfoUtil.getVersionInfo()).setDisablePrintWebServerInfo(true);
        serverConfig.setNativeImageAgent(Constants.runMode == RunMode.NATIVE_AGENT);
        serverConfig.setDisableSession(true);
        serverConfig.setPort(port);
        serverConfig.setContextPath(contextPath);
        serverConfig.setPidFilePathEnvKey("ZRLOG_PID_FILE");
        serverConfig.setServerPortFilePathEnvKey("ZRLOG_HTTP_PORT_FILE");
        serverConfig.setDisableSavePidFile(EnvKit.isFaaSMode());
        serverConfig.setHttpJsonMessageConverter(new ZrLogHttpJsonMessageConverter());
        serverConfig.addErrorHandle(400, new ZrLogErrorHandle(400, this));
        serverConfig.addErrorHandle(403, new ZrLogErrorHandle(403, this));
        serverConfig.addErrorHandle(404, new ZrLogErrorHandle(404, this));
        serverConfig.addErrorHandle(500, new ZrLogErrorHandle(500, this));
        serverConfig.setRequestExecutor(ThreadUtils.newFixedThreadPool(200));
        serverConfig.setDecodeExecutor(ThreadUtils.newFixedThreadPool(20));
        serverConfig.setRequestCheckerExecutor(new ScheduledThreadPoolExecutor(1, ThreadUtils::unstarted));
        serverConfig.addRequestListener(new ZrLogHttpRequestListener());
        getStaticResourcePath().forEach(e -> serverConfig.addStaticResourceMapper(e, e, ZrLogConfigImpl.class::getResourceAsStream));
        Runtime rt = Runtime.getRuntime();
        rt.addShutdownHook(new Thread(this::stop));
        return serverConfig;
    }

    @Override
    public RequestConfig getRequestConfig() {
        RequestConfig requestConfig = new RequestConfig();
        //最大的提交的body的大小
        requestConfig.setDisableSession(true);
        requestConfig.setRouter(serverConfig.getRouter());
        requestConfig.setMaxRequestBodySize(1024 * 1024 * 1024);
        return requestConfig;
    }

    @Override
    public ResponseConfig getResponseConfig() {
        ResponseConfig config = new ResponseConfig();
        config.setCharSet("utf-8");
        config.setEnableGzip(true);
        config.setGzipMimeTypes(Arrays.asList("text/", "application/javascript", "application/json"));
        return config;
    }

    @Override
    public void configDatabase() {
        // 如果没有安装的情况下不初始化数据
        if (!isInstalled()) {
            LOGGER.log(Level.WARNING, "Not found lock file(" + installLockFile + "), Please visit the" + " http://yourHostName:port/install start installation");
            return;
        }
        Properties dbProperties = DbUtils.getDbProp(dbPropertiesFile);
        //启动时候进行数据库连接
        dataSource = DataSourceUtil.buildDataSource(dbProperties);
        new DbUpgradeService(dataSource).tryDoUpgrade();
        DAO.setDs(dataSource);
    }


    /***
     * 加载插件，ZrLog自动检查更新，加载ZrLog提供的插件。
     */
    private Plugins getPluginList() {
        Plugins plugins = new Plugins();
        if (isTest()) {
            return plugins;
        }
        try {
            plugins.add(new PluginCorePluginImpl(dbPropertiesFile));
            plugins.add(new CacheManagerPlugin(this));
            webSetups.forEach(e -> plugins.addAll(e.getPlugins()));
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "configPlugin exception ", e);
        }
        return plugins;
    }

    @Override
    public Plugins getPlugins() {
        return plugins;
    }

    @Override
    public Updater getUpdater() {
        return updater;
    }

    @Override
    public CacheService<BaseDataInitVO> getCacheService() {
        return cacheService;
    }

    @Override
    public TokenService getTokenService() {
        return adminTokenService;
    }

    @Override
    public void startPluginsAsync() {
        if (!Constants.zrLogConfig.isInstalled()) {
            return;
        }
        this.plugins.forEach(IPlugin::stop);
        this.plugins.clear();
        this.plugins.addAll(getPluginList());
        ThreadUtils.start(() -> {
            this.cacheService.refreshInitDataCacheAsync(null, true).join();
            for (IPlugin plugin : plugins) {
                if (!plugin.autoStart()) {
                    continue;
                }
                if (plugin.isStarted()) {
                    continue;
                }
                try {
                    plugin.start();
                } catch (Exception e) {
                    LOGGER.severe("plugin error, " + e.getMessage());
                }
            }
        });
    }

    @Override
    public String getProgramUptime() {
        return ParseUtil.toNamingDurationString(System.currentTimeMillis() - uptime, I18nUtil.getCurrentLocale().contains("en"));
    }

    @Override
    public DataSource getDataSource() {
        return dataSource;
    }

    @Override
    public void stop() {
        try {
            PluginUtils.stopAllPlugin();
            if (Objects.nonNull(dataSource)) {
                dataSource.close();
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "", e);
        } finally {
            AbandonedConnectionCleanupThread.checkedShutdown();
            HttpUtil.getInstance().closeHttpClient();
        }
    }

    @Override
    public AdminResource getAdminResource() {
        return adminResource;
    }

    @Override
    public void refreshPluginCacheData(long version) {
        PluginUtils.refreshPluginCacheData(includeBlog, version);
    }

    @Override
    public boolean isStaticPluginRequest(HttpRequest request) {
        if (!includeBlog) {
            return false;
        }
        try {
            StaticSitePlugin staticSitePluginImpl = getPlugin(StaticSitePlugin.class);
            if (Objects.isNull(staticSitePluginImpl)) {
                return false;
            }
            return staticSitePluginImpl.isStaticPluginRequest(request);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean isInstalled() {
        if (StringUtils.isNotEmpty(ZrLogUtil.getDbInfoByEnv())) {
            return true;
        }
        return installLockFile.exists();
    }

}
