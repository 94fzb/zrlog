package com.zrlog.web.config;

import com.hibegin.common.util.IOUtil;
import com.hibegin.common.util.LoggerUtil;
import com.hibegin.common.util.StringUtils;
import com.hibegin.dao.DAO;
import com.hibegin.http.server.api.HttpRequest;
import com.hibegin.http.server.api.HttpRequestListener;
import com.hibegin.http.server.api.HttpResponse;
import com.hibegin.http.server.api.Interceptor;
import com.hibegin.http.server.config.RequestConfig;
import com.hibegin.http.server.config.ResponseConfig;
import com.hibegin.http.server.config.ServerConfig;
import com.zaxxer.hikari.HikariDataSource;
import com.zrlog.admin.web.config.AdminRouters;
import com.zrlog.admin.web.interceptor.AdminInterceptor;
import com.zrlog.admin.web.interceptor.AdminPwaInterceptor;
import com.zrlog.admin.web.plugin.PluginCorePluginImpl;
import com.zrlog.admin.web.plugin.PluginCoreProcessImpl;
import com.zrlog.admin.web.plugin.UpdateVersionPlugin;
import com.zrlog.admin.web.token.AdminTokenService;
import com.zrlog.blog.web.config.BlogRouters;
import com.zrlog.blog.web.controller.api.ApiInstallController;
import com.zrlog.blog.web.interceptor.BlogApiInterceptor;
import com.zrlog.blog.web.interceptor.BlogInstallInterceptor;
import com.zrlog.blog.web.interceptor.BlogPageInterceptor;
import com.zrlog.blog.web.interceptor.PwaInterceptor;
import com.zrlog.blog.web.plugin.CacheManagerPlugin;
import com.zrlog.blog.web.plugin.RequestStatisticsPlugin;
import com.zrlog.blog.web.version.UpgradeVersionHandler;
import com.zrlog.business.cache.CacheServiceImpl;
import com.zrlog.business.plugin.StaticSitePlugin;
import com.zrlog.business.plugin.TemplateDownloadPlugin;
import com.zrlog.business.util.InstallUtils;
import com.zrlog.common.*;
import com.zrlog.common.type.RunMode;
import com.zrlog.model.WebSite;
import com.zrlog.plugin.IPlugin;
import com.zrlog.plugin.Plugins;
import com.zrlog.util.BlogBuildInfoUtil;
import com.zrlog.util.DbConnectUtils;
import com.zrlog.util.I18nUtil;
import com.zrlog.util.ZrLogUtil;
import com.zrlog.web.inteceptor.GlobalBaseInterceptor;
import com.zrlog.web.inteceptor.MyI18nInterceptor;
import com.zrlog.web.inteceptor.PluginInterceptor;
import com.zrlog.web.inteceptor.StaticResourceInterceptor;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
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

    public ZrLogConfigImpl(Integer port, Updater updater) {
        this.port = port;
        this.plugins = new Plugins();
        this.updater = updater;
        this.cacheService = new CacheServiceImpl();
        this.pluginCoreProcess = new PluginCoreProcessImpl(port);
        this.serverConfig = initServerConfig();
        this.uptime = System.currentTimeMillis();
    }

    /**
     * 配置的常用参数，这里可以看出来推崇使用代码进行配置的，而不是像Spring这样的通过预定配置方式。代码控制的好处在于高度可控制性，
     * 当然这也导致了很多程序员直接硬编码的问题。
     */
    @Override
    public ServerConfig getServerConfig() {
        return serverConfig;
    }

    private ServerConfig initServerConfig() {
        ServerConfig serverConfig = new ServerConfig().setApplicationName("zrlog")
                .setApplicationVersion(BlogBuildInfoUtil.getVersionInfo())
                .setDisablePrintWebServerInfo(true);
        serverConfig.setNativeImageAgent(Constants.runMode == RunMode.NATIVE_AGENT);
        serverConfig.setDisableSession(true);
        serverConfig.setPort(port);
        serverConfig.setPidFilePathEnvKey("ZRLOG_PID_FILE");
        serverConfig.setServerPortFilePathEnvKey("ZRLOG_HTTP_PORT_FILE");
        serverConfig.setHttpJsonMessageConverter(new ZrLogHttpJsonMessageConverter());
        serverConfig.addErrorHandle(400, new ZrLogErrorHandle(400));
        serverConfig.addErrorHandle(403, new ZrLogErrorHandle(403));
        serverConfig.addErrorHandle(404, new ZrLogErrorHandle(404));
        serverConfig.addErrorHandle(500, new ZrLogErrorHandle(500));
        serverConfig.setRequestExecutor(Executors.newVirtualThreadPerTaskExecutor());
        serverConfig.setDecodeExecutor(Executors.newVirtualThreadPerTaskExecutor());
        serverConfig.setRequestCheckerExecutor(new ScheduledThreadPoolExecutor(1, r -> {
            return Thread.ofVirtual().unstarted(r);
        }));
        serverConfig.addRequestListener(new HttpRequestListener() {
            @Override
            public void destroy(HttpRequest request, HttpResponse httpResponse) {
                I18nUtil.removeI18n();
            }

            @Override
            public void create(HttpRequest request, HttpResponse httpResponse) {

            }
        });
        StaticResourceInterceptor.staticResourcePath.forEach(e -> serverConfig.addStaticResourceMapper(e, e, ZrLogConfigImpl.class::getResourceAsStream));
        AdminRouters.configAdminRoute(serverConfig.getRouter());
        BlogRouters.configBlogRouter(serverConfig.getRouter());
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
        requestConfig.setMaxRequestBodySize(1024 * 1024 * 1024);
        return requestConfig;
    }

    @Override
    public ResponseConfig getResponseConfig() {
        ResponseConfig config = new ResponseConfig();
        config.setCharSet("utf-8");
        config.setEnableGzip(true);
        config.setGzipMimeTypes(List.of("text/", "application/javascript", "application/json"));
        return config;
    }

    public static boolean isTest() {
        return "junit-test".equals(InstallUtils.getSystemProp().getProperty("env"));
    }


    /**
     * 配置过滤器，这里配置时需要区分先后顺序的。由于提供拦截器并没有类似 Spring 的过滤器可以对请求路径的配置，这里并不是很优雅。
     * 及需要在对应 Interceptor 中自行通过路由进行拦截
     */
    public void configInterceptor(List<Class<? extends Interceptor>> interceptors) {
        //all
        interceptors.add(GlobalBaseInterceptor.class);
        interceptors.add(StaticResourceInterceptor.class);
        interceptors.add(MyI18nInterceptor.class);
        interceptors.add(PluginInterceptor.class);
        //admin
        interceptors.add(AdminPwaInterceptor.class);
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
        tryInitDbPropertiesFile();
        Properties dbProperties = Objects.requireNonNull(InstallUtils.getDbProp());
        int newDbVersion = tryDoUpgrade(dbProperties);
        //启动时候进行数据库连接
        HikariDataSource hikariDataSource = new HikariDataSource();
        hikariDataSource.setDataSourceProperties(dbProperties);
        hikariDataSource.setDriverClassName(dbProperties.getProperty("driverClass"));
        hikariDataSource.setUsername(dbProperties.getProperty("user"));
        hikariDataSource.setPassword(dbProperties.getProperty("password"));
        hikariDataSource.setJdbcUrl(dbProperties.getProperty("jdbcUrl"));
        DAO.setDs(hikariDataSource);
        //Test
        try (Connection connection = hikariDataSource.getConnection()) {
            LOGGER.info("Db connect success " + connection.getCatalog());
        }
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
                //这里使用独立的线程进行启动，主要是为了防止插件服务出问题后，影响整体，同时是避免启动过慢的问题。
                plugins.add(new PluginCorePluginImpl(Constants.getDbPropertiesFile(), pluginJvmArgsObj.toString(), pluginCoreProcess, UUID.randomUUID().toString().replace("-", "")));
                plugins.add(new UpdateVersionPlugin());
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
     * 将 docker 配置的 DB_PROPERTIES 写入到实际的文件中，便于程序读取
     */
    private static void tryInitDbPropertiesFile() throws IOException {
        if (StringUtils.isNotEmpty(ZrLogUtil.getDbInfoByEnv())) {
            IOUtil.writeBytesToFile(ZrLogUtil.getDbInfoByEnv().getBytes(), Constants.getDbPropertiesFile());
        }
        Properties properties = InstallUtils.getDbProp();
        if (Objects.isNull(properties)) {
            return;
        }
        if ("com.mysql.cj.jdbc.Driver".equals(properties.get("driverClass"))) {
            return;
        }
        try (FileOutputStream fileOutputStream = new FileOutputStream(Constants.getDbPropertiesFile())) {
            properties.put("driverClass", "com.mysql.cj.jdbc.Driver");
            properties.put("jdbcUrl", properties.get("jdbcUrl") + "&" + ApiInstallController.JDBC_URL_BASE_QUERY_PARAM);
            properties.store(fileOutputStream, "Support mysql8");
            LOGGER.info("Upgrade properties success");
        }
    }

    /**
     * 检查数据文件是否需要更新
     * 为了处理由于数据库表的更新，导致系统无法正常使用的情况，通过执行/conf/update-sql/目录下面的*.sql文件来变更数据库的表格式，
     * 来达到系统无需手动执行数据库脚本文件。
     */
    private int tryDoUpgrade(Properties dbProp) {
        Long currentVersion = ZrLogUtil.getCurrentSqlVersion(dbProp);
        if (Objects.isNull(currentVersion) || currentVersion < 0) {
            return -1;
        }
        List<Map.Entry<Integer, List<String>>> sqlList = ZrLogUtil.getExecSqlList(currentVersion);
        if (sqlList.isEmpty()) {
            return -1;
        }
        try (Connection connection = DbConnectUtils.getConnection(dbProp)) {
            if (Objects.isNull(connection)) {
                return -1;
            }
            for (Map.Entry<Integer, List<String>> entry : sqlList) {
                //执行需要更新的sql脚本
                try (Statement statement = connection.createStatement()) {
                    for (String sql : entry.getValue()) {
                        if (StringUtils.isEmpty(sql.trim())) {
                            continue;
                        }
                        statement.execute(sql);
                    }
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "execution sql ", e);
                    //有异常终止升级
                    return -1;
                }
                //执行需要转换的数据
                try {
                    UpgradeVersionHandler upgradeVersionHandler = (UpgradeVersionHandler) Class.forName("com.zrlog.web.version.V" + entry.getKey() + "UpgradeVersionHandler").getDeclaredConstructor().newInstance();
                    try {
                        upgradeVersionHandler.doUpgrade(connection);
                    } catch (Exception e) {
                        LOGGER.log(Level.SEVERE, "", e);
                        return -1;
                    }
                } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
                    if (Constants.debugLoggerPrintAble()) {
                        LOGGER.log(Level.WARNING, "Try exec upgrade method error, " + e.getMessage());
                    }
                }
            }
            return Constants.SQL_VERSION;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "", e);
            return -1;
        }
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
        Thread.ofVirtual().start(() -> {
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

    @Override
    public Map<String, Object> getPublicWebSite() {
        return website;
    }

    @Override
    public String getProgramUptime() {
        return toNamingDurationString(System.currentTimeMillis() - uptime, I18nUtil.getCurrentLocale().contains("en"));
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
}
