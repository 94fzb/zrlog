package com.zrlog.web.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hibegin.common.util.IOUtil;
import com.hibegin.common.util.LoggerUtil;
import com.hibegin.common.util.StringUtils;
import com.hibegin.dao.DAO;
import com.hibegin.http.server.api.HttpRequest;
import com.hibegin.http.server.api.HttpRequestListener;
import com.hibegin.http.server.api.HttpResponse;
import com.hibegin.http.server.api.Interceptor;
import com.hibegin.http.server.config.*;
import com.zaxxer.hikari.util.DriverDataSource;
import com.zrlog.admin.web.config.AdminRouters;
import com.zrlog.admin.web.interceptor.AdminInterceptor;
import com.zrlog.admin.web.interceptor.AdminPwaInterceptor;
import com.zrlog.admin.web.plugin.PluginCorePlugin;
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
import com.zrlog.business.plugin.StaticHtmlPlugin;
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
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 核心一些参数的配置。
 */
public class ZrLogConfigImpl extends ZrLogConfig {

    private static final Logger LOGGER = LoggerUtil.getLogger(ZrLogConfigImpl.class);

    private final Integer port;
    private ServerConfig serverConfig;
    private final Plugins plugins;
    private final Updater updater;
    private final CacheService cacheService;
    private final PluginCoreProcess pluginCoreProcess;

    public ZrLogConfigImpl(Integer port, Updater updater) {
        this.port = port;
        this.plugins = new Plugins();
        this.updater = updater;
        this.cacheService = new CacheServiceImpl();
        this.pluginCoreProcess = new PluginCoreProcessImpl();
    }

    /**
     * 配置的常用参数，这里可以看出来推崇使用代码进行配置的，而不是像Spring这样的通过预定配置方式。代码控制的好处在于高度可控制性，
     * 当然这也导致了很多程序员直接硬编码的问题。
     */
    @Override
    public ServerConfig getServerConfig() {
        if (Objects.nonNull(serverConfig)) {
            return serverConfig;
        }
        serverConfig = new ServerConfig().setApplicationName("zrlog").setDisablePrintWebServerInfo(true);
        serverConfig.setNativeImageAgent(Constants.runMode == RunMode.NATIVE_AGENT);
        serverConfig.setDisableSession(true);
        serverConfig.setHttpJsonMessageConverter(new GsonHttpJsonMessageConverter() {
            @Override
            public String toJson(Object obj) throws Exception {
                GsonBuilder gsonBuilder = new GsonBuilder();
                gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter());
                Gson gson = gsonBuilder.create();
                return gson.toJson(obj);
            }

            @Override
            public Object fromJson(String jsonStr) throws Exception {
                return super.fromJson(jsonStr);
            }
        });
        serverConfig.addErrorHandle(400, new ZrLogErrorHandle(400));
        serverConfig.addErrorHandle(403, new ZrLogErrorHandle(403));
        serverConfig.addErrorHandle(404, new ZrLogErrorHandle(404));
        serverConfig.addErrorHandle(500, new ZrLogErrorHandle(500));
        serverConfig.setRequestExecutor(Executors.newVirtualThreadPerTaskExecutor());
        serverConfig.setDecodeExecutor(Executors.newVirtualThreadPerTaskExecutor());
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
        serverConfig.setPort(port);
        installFinish();
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

    /***
     * 加载插件，比如提供的HikariCp，ZrLog自动检查更新，加载ZrLog提供的插件。
     */
    private void configPlugin(Plugins plugins) {
        // 如果没有安装的情况下不初始化数据
        if (!InstallUtils.isInstalled()) {
            LOGGER.log(Level.WARNING, "Not found lock file(" + InstallUtils.getLockFile() + "), Please visit the" + " http://yourHostName:port/install start installation");
            return;
        }

        try {
            tryInitDbPropertiesFile();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Init db properties exception ", e);
            System.exit(-1);
        }
        Properties dbProperties = InstallUtils.getDbProp();
        try (Connection connection = DbConnectUtils.getConnection(Objects.requireNonNull(dbProperties))) {
            LOGGER.info("Db connect success " + connection.getCatalog());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Connect Database error ", e);
            System.exit(-1);
        }
        try {
            tryDoUpgrade(dbProperties);
            //启动时候进行数据库连接
            DAO.setDs(new DriverDataSource(dbProperties.getProperty("jdbcUrl"),
                    dbProperties.getProperty("driverClass"), dbProperties, dbProperties.getProperty("user"), dbProperties.getProperty("password")));
            if (!isTest()) {
                Object pluginJvmArgsObj = BlogBuildInfoUtil.getBlogProp().get("pluginJvmArgs");
                if (pluginJvmArgsObj == null) {
                    pluginJvmArgsObj = "";
                }
                //这里使用独立的线程进行启动，主要是为了防止插件服务出问题后，影响整体，同时是避免启动过慢的问题。
                plugins.add(new PluginCorePlugin(Constants.getDbPropertiesFile(), pluginJvmArgsObj.toString(), pluginCoreProcess, UUID.randomUUID().toString().replace("-", "")));
                plugins.add(new UpdateVersionPlugin());
                plugins.add(new CacheManagerPlugin());
                //需要缓存加载完毕后执行的插件
                plugins.add(new TemplateDownloadPlugin());
                plugins.add(new StaticHtmlPlugin(this));
            }
            plugins.add(new RequestStatisticsPlugin());
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "configPlugin exception ", e);
        }
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
    private void tryDoUpgrade(Properties dbProp) {
        Long currentVersion = ZrLogUtil.getCurrentSqlVersion(dbProp);
        if (Objects.isNull(currentVersion) || currentVersion < 0) {
            return;
        }
        List<Map.Entry<Integer, List<String>>> sqlList = ZrLogUtil.getExecSqlList(currentVersion);
        if (sqlList.isEmpty()) {
            return;
        }
        try (Connection connection = DbConnectUtils.getConnection(dbProp)) {
            for (Map.Entry<Integer, List<String>> entry : sqlList) {
                if (Objects.isNull(connection)) {
                    return;
                }
                //执行需要更新的sql脚本
                Statement statement = connection.createStatement();
                try {
                    for (String sql : entry.getValue()) {
                        if(StringUtils.isEmpty(sql.trim())){
                            continue;
                        }
                        statement.execute(sql);
                    }
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "execution sql ", e);
                    //有异常终止升级
                    return;
                } finally {
                    if (statement != null) {
                        try {
                            statement.close();
                        } catch (SQLException e) {
                            LOGGER.log(Level.SEVERE, "", e);
                        }
                    }
                }
                //执行需要转换的数据
                try {
                    UpgradeVersionHandler upgradeVersionHandler = (UpgradeVersionHandler) Class.forName("com.zrlog.web.version.V" + entry.getKey() + "UpgradeVersionHandler").getDeclaredConstructor().newInstance();
                    try {
                        upgradeVersionHandler.doUpgrade(connection);
                    } catch (Exception e) {
                        LOGGER.log(Level.SEVERE, "", e);
                        return;
                    }
                } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
                    if(Constants.debugLoggerPrintAble()) {
                        LOGGER.log(Level.WARNING, "Try exec upgrade method error, " + e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "", e);
        }
    }

    /**
     * 系统停止后，关闭插件相关进程服务，防治内存泄漏
     */
    private void onStop() {
        pluginCoreProcess.stopPluginCore();
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
    public void installFinish() {
        configPlugin(plugins);
        for (IPlugin plugin : plugins) {
            try {
                plugin.start();
            } catch (Exception e) {
                LOGGER.severe("plugin error, " + e.getMessage());
            }
        }
        if (!InstallUtils.isInstalled()) {
            return;
        }
        int updatedVersion = Constants.SQL_VERSION;
        if (updatedVersion <= 0) {
            return;
        }
        try {
            new WebSite().updateByKV(Constants.ZRLOG_SQL_VERSION_KEY, updatedVersion + "");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
