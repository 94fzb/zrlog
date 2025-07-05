package com.zrlog.web.config;

import com.hibegin.common.util.IOUtil;
import com.hibegin.common.util.LoggerUtil;
import com.hibegin.common.util.StringUtils;
import com.hibegin.common.util.http.HttpUtil;
import com.hibegin.dao.DAO;
import com.hibegin.http.server.api.Interceptor;
import com.hibegin.http.server.config.RequestConfig;
import com.hibegin.http.server.config.ResponseConfig;
import com.hibegin.http.server.config.ServerConfig;
import com.hibegin.http.server.util.PathUtil;
import com.mysql.cj.jdbc.AbandonedConnectionCleanupThread;
import com.zaxxer.hikari.HikariDataSource;
import com.zrlog.admin.business.service.AdminResourceImpl;
import com.zrlog.admin.web.config.AdminRouters;
import com.zrlog.admin.web.interceptor.AdminCrossOriginInterceptor;
import com.zrlog.admin.web.interceptor.AdminInterceptor;
import com.zrlog.admin.web.interceptor.AdminPwaInterceptor;
import com.zrlog.admin.web.plugin.PluginCorePluginImpl;
import com.zrlog.admin.web.plugin.PluginCoreProcessImpl;
import com.zrlog.admin.web.token.AdminTokenService;
import com.zrlog.blog.web.config.BlogRouters;
import com.zrlog.blog.web.controller.api.ApiInstallController;
import com.zrlog.blog.web.interceptor.BlogApiInterceptor;
import com.zrlog.blog.web.interceptor.BlogInstallInterceptor;
import com.zrlog.blog.web.interceptor.BlogPageInterceptor;
import com.zrlog.blog.web.interceptor.PwaInterceptor;
import com.zrlog.blog.web.plugin.CacheManagerPlugin;
import com.zrlog.blog.web.plugin.RequestStatisticsPlugin;
import com.zrlog.business.cache.CacheServiceImpl;
import com.zrlog.business.plugin.StaticSitePlugin;
import com.zrlog.business.plugin.TemplateDownloadPlugin;
import com.zrlog.business.plugin.UpdateVersionInfoPlugin;
import com.zrlog.business.service.DbUpgradeService;
import com.zrlog.business.util.InstallUtils;
import com.zrlog.business.util.PluginUtils;
import com.zrlog.common.*;
import com.zrlog.common.type.RunMode;
import com.zrlog.common.vo.DatabaseConnectPoolInfo;
import com.zrlog.model.WebSite;
import com.zrlog.plugin.IPlugin;
import com.zrlog.plugin.Plugins;
import com.zrlog.util.*;
import com.zrlog.web.Application;
import com.zrlog.web.inteceptor.GlobalBaseInterceptor;
import com.zrlog.web.inteceptor.MyI18nInterceptor;
import com.zrlog.web.inteceptor.PluginInterceptor;
import com.zrlog.web.inteceptor.StaticResourceInterceptor;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLRecoverableException;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 核心一些参数的配置。
 */
public class ZrLogConfigImpl extends ZrLogConfig {

    private static final Logger LOGGER = LoggerUtil.getLogger(ZrLogConfigImpl.class);

    private final Integer port;
    private final ServerConfig serverConfig;
    private final Plugins plugins;
    private final Updater updater;
    private final CacheService cacheService;
    private final PluginCoreProcess pluginCoreProcess;
    private final Map<String, Object> website = new TreeMap<>();
    private final long uptime;
    private final AdminResource adminResource;
    private final String contextPath;
    private HikariDataSource dataSource;
    private final File dbPropertiesFile;

    static {
        disableHikariLogging();
    }

    private static void disableHikariLogging() {
        System.setProperty("org.slf4j.simpleLogger.log.com.zaxxer.hikari", "off");
    }

    public ZrLogConfigImpl(Integer port, Updater updater, String contextPath) {
        this.dbPropertiesFile = initDbPropertiesFile();

        this.contextPath = contextPath;
        this.port = port;
        this.plugins = new Plugins();
        this.updater = updater;
        this.cacheService = new CacheServiceImpl(contextPath);
        this.pluginCoreProcess = new PluginCoreProcessImpl();
        this.adminResource = new AdminResourceImpl(cacheService);
        this.serverConfig = initServerConfig(contextPath);
        this.uptime = System.currentTimeMillis();
        this.configRouter();
        this.configDatabaseWithRetry(20);

        if (ThreadUtils.isEnableLoom() && Constants.debugLoggerPrintAble()) {
            LOGGER.info("Java VirtualThread(loom) enabled");
        }
    }

    public static boolean isTest() {
        return "junit-test".equals(System.getProperties().getProperty("env"));
    }

    /**
     * 将 env 配置的 DB_PROPERTIES 写入到实际的文件中，便于程序读取
     */
    private static File initDbPropertiesFile() {
        File dbFiles = new File(PathUtil.getConfPath() + "/db.properties");
        dbFiles.getParentFile().mkdirs();
        try {
            if (!InstallUtils.isInstalled()) {
                return dbFiles;
            }
            if (StringUtils.isNotEmpty(ZrLogUtil.getDbInfoByEnv())) {
                IOUtil.writeBytesToFile(new String(ZrLogUtil.getDbInfoByEnv().getBytes()).replaceAll(" ", "\n").getBytes(), dbFiles);
            }
            Properties properties = getDbProp(dbFiles);
            if ("com.mysql.cj.jdbc.Driver".equals(properties.get("driverClass"))) {
                return dbFiles;
            }
            try (FileOutputStream fileOutputStream = new FileOutputStream(dbFiles)) {
                properties.put("driverClass", "com.mysql.cj.jdbc.Driver");
                properties.put("jdbcUrl", properties.get("jdbcUrl") + "&" + ApiInstallController.JDBC_URL_BASE_QUERY_PARAM);
                properties.store(fileOutputStream, "Support mysql8");
                LOGGER.info("Upgrade properties success");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "initDbPropertiesFile error " + e.getMessage());
        }
        return dbFiles;
    }

    static String toNamingDurationString(long milliseconds, boolean en) {
        long days = TimeUnit.MILLISECONDS.toDays(milliseconds);
        long hours = TimeUnit.MILLISECONDS.toHours(milliseconds) % 24;
        long minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds) % 60;
        long seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds) % 60;
        if (en) {
            return String.format("%dd %dh %dm %ds", days, hours, minutes, seconds);
        }
        return String.format("%d天 %d时 %d分 %d秒", days, hours, minutes, seconds);
    }

    /**
     * 配置的常用参数，这里可以看出来推崇使用代码进行配置的，而不是像Spring这样的通过预定配置方式。代码控制的好处在于高度可控制性，
     * 当然这也导致了很多程序员直接硬编码的问题。
     */
    @Override
    public ServerConfig getServerConfig() {
        return serverConfig;
    }

    private void configRouter() {
        AdminRouters.configAdminRoute(serverConfig.getRouter(), adminResource);
        BlogRouters.configBlogRouter(serverConfig.getRouter());
    }

    private ServerConfig initServerConfig(String contextPath) {
        ServerConfig serverConfig = new ServerConfig().setApplicationName("zrlog").setApplicationVersion(BlogBuildInfoUtil.getVersionInfo()).setDisablePrintWebServerInfo(true);
        serverConfig.setNativeImageAgent(Constants.runMode == RunMode.NATIVE_AGENT);
        serverConfig.setDisableSession(true);
        serverConfig.setPort(port);
        serverConfig.setContextPath(contextPath);
        serverConfig.setPidFilePathEnvKey("ZRLOG_PID_FILE");
        serverConfig.setServerPortFilePathEnvKey("ZRLOG_HTTP_PORT_FILE");
        serverConfig.setDisableSavePidFile(RunMode.isLambdaMode());
        serverConfig.setHttpJsonMessageConverter(new ZrLogHttpJsonMessageConverter());
        serverConfig.addErrorHandle(400, new ZrLogErrorHandle(400));
        serverConfig.addErrorHandle(403, new ZrLogErrorHandle(403));
        serverConfig.addErrorHandle(404, new ZrLogErrorHandle(404));
        serverConfig.addErrorHandle(500, new ZrLogErrorHandle(500));
        serverConfig.setRequestExecutor(Executors.newFixedThreadPool(200));
        serverConfig.setDecodeExecutor(Executors.newFixedThreadPool(20));
        serverConfig.setRequestCheckerExecutor(new ScheduledThreadPoolExecutor(1, ThreadUtils::unstarted));
        serverConfig.addRequestListener(new ZrLogHttpRequestListener());
        StaticResourceInterceptor.staticResourcePath.forEach(e -> serverConfig.addStaticResourceMapper(e, e, ZrLogConfigImpl.class::getResourceAsStream));
        configInterceptor(serverConfig.getInterceptors());
        Runtime rt = Runtime.getRuntime();
        rt.addShutdownHook(new Thread(this::onStop));
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

    /**
     * 配置过滤器，这里配置时需要区分先后顺序的。由于提供拦截器并没有类似 Spring 的过滤器可以对请求路径的配置，这里并不是很优雅。
     * 及需要在对应 Interceptor 中自行通过路由进行拦截
     */
    private void configInterceptor(List<Class<? extends Interceptor>> interceptors) {
        //all
        interceptors.add(GlobalBaseInterceptor.class);
        interceptors.add(StaticResourceInterceptor.class);
        interceptors.add(MyI18nInterceptor.class);
        interceptors.add(PluginInterceptor.class);
        //admin
        interceptors.add(AdminPwaInterceptor.class);
        interceptors.add(AdminCrossOriginInterceptor.class);
        interceptors.add(AdminInterceptor.class);
        //blog
        interceptors.add(PwaInterceptor.class);
        interceptors.add(BlogInstallInterceptor.class);
        interceptors.add(BlogApiInterceptor.class);
        interceptors.add(BlogPageInterceptor.class);
    }

    @Override
    public void configDatabase() throws Exception {
        // 如果没有安装的情况下不初始化数据
        if (!InstallUtils.isInstalled()) {
            LOGGER.log(Level.WARNING, "Not found lock file(" + InstallUtils.getLockFile() + "), Please visit the" + " http://yourHostName:port/install start installation");
            return;
        }
        Properties dbProperties = getDbProp(dbPropertiesFile);
        //启动时候进行数据库连接
        dataSource = new HikariDataSource();
        dataSource.setDataSourceProperties(dbProperties);
        dataSource.setDriverClassName(dbProperties.getProperty("driverClass"));
        dataSource.setUsername(dbProperties.getProperty("user"));
        dataSource.setPassword(dbProperties.getProperty("password"));
        dataSource.setMinimumIdle(3);
        dataSource.setJdbcUrl(dbProperties.getProperty("jdbcUrl"));
        DAO.setDs(dataSource);
        int newDbVersion = new DbUpgradeService(dataSource).tryDoUpgrade();
        if (newDbVersion > 0) {
            new WebSite().updateByKV(Constants.ZRLOG_SQL_VERSION_KEY, newDbVersion + "");
        }
    }

    /***
     * 加载插件，比如提供的HikariCp，ZrLog自动检查更新，加载ZrLog提供的插件。
     */
    private Plugins getPluginList() {
        Plugins plugins = new Plugins();
        try {
            if (!isTest()) {
                Object pluginJvmArgsObj = BlogBuildInfoUtil.getBlogProp().get("pluginJvmArgs");
                if (pluginJvmArgsObj == null) {
                    pluginJvmArgsObj = "";
                }
                String folder = RunMode.isLambdaMode() ? ZrLogUtil.getLambdaRoot() + "/conf/plugins" : PathUtil.getConfPath() + "/plugins";
                //这里使用独立的线程进行启动，主要是为了防止插件服务出问题后，影响整体，同时是避免启动过慢的问题。
                plugins.add(new PluginCorePluginImpl(dbPropertiesFile, new File(folder), pluginJvmArgsObj.toString(), pluginCoreProcess, UUID.randomUUID().toString().replace("-", "")));
                plugins.add(new UpdateVersionInfoPlugin());
                plugins.add(new CacheManagerPlugin());
                //需要缓存加载完毕后执行的插件
                plugins.add(new TemplateDownloadPlugin());
                plugins.add(new StaticSitePlugin(this));
            }
            plugins.add(new RequestStatisticsPlugin());
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "configPlugin exception ", e);
        }
        return plugins;
    }


    /**
     * 系统停止后，关闭插件相关进程服务，防治内存泄漏
     */
    private void onStop() {
        for (IPlugin plugin : plugins) {
            plugin.stop();
        }
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
    public CacheService getCacheService() {
        return cacheService;
    }

    @Override
    public TokenService getTokenService() {
        return new AdminTokenService();
    }

    @Override
    public void startPluginsAsync() {
        if (!InstallUtils.isInstalled()) {
            return;
        }
        ThreadUtils.start(() -> {
            this.plugins.forEach(IPlugin::stop);
            this.plugins.clear();
            this.plugins.addAll(getPluginList());
            this.cacheService.refreshInitDataCacheAsync(null, true).join();
            for (IPlugin plugin : plugins) {
                if (plugin.isStarted()) {
                    continue;
                }
                if (plugin instanceof StaticSitePlugin) {
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

    private void configDatabaseWithRetry(int timeoutInSeconds) {
        try {
            configDatabase();
        } catch (Exception e) {
            if (timeoutInSeconds > 0 && e instanceof SQLRecoverableException) {
                int seekSeconds = 5;
                try {
                    Thread.sleep(seekSeconds * 1000);
                } catch (InterruptedException ex) {
                    //ignore
                }
                configDatabaseWithRetry(timeoutInSeconds - seekSeconds);
                return;
            }
            LoggerUtil.getLogger(Application.class).warning("Config database error " + e.getMessage());
            if (Constants.runMode != RunMode.NATIVE_AGENT && !ZrLogUtil.isWarMode(updater)) {
                System.exit(-1);
            }
        }
    }

    @Override
    public Map<String, Object> getPublicWebSite() {
        return website;
    }

    @Override
    public String getProgramUptime() {
        return toNamingDurationString(System.currentTimeMillis() - uptime, I18nUtil.getCurrentLocale().contains("en"));
    }

    @Override
    public String getContextPath() {
        return contextPath;
    }

    @Override
    public DatabaseConnectPoolInfo getDatabaseConnectPoolInfo() {
        if (Objects.isNull(dataSource)) {
            return new DatabaseConnectPoolInfo(0, 0);
        }
        return new DatabaseConnectPoolInfo(dataSource.getHikariPoolMXBean().getActiveConnections(), dataSource.getHikariPoolMXBean().getTotalConnections());
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
    public File getDbPropertiesFile() {
        return dbPropertiesFile;
    }

    private static Properties getDbProp(File dbPropertiesFile) {
        Properties dbProperties = new Properties();
        try (FileInputStream in = new FileInputStream(dbPropertiesFile)) {
            dbProperties.load(in);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return dbProperties;
    }
}
