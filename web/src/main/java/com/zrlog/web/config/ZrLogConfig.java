package com.zrlog.web.config;

import com.hibegin.common.util.IOUtil;
import com.hibegin.common.util.LoggerUtil;
import com.hibegin.common.util.StringUtils;
import com.hibegin.dao.DAO;
import com.hibegin.http.server.api.HttpRequest;
import com.hibegin.http.server.api.HttpRequestListener;
import com.hibegin.http.server.api.HttpResponse;
import com.hibegin.http.server.api.Interceptor;
import com.hibegin.http.server.config.AbstractServerConfig;
import com.hibegin.http.server.config.RequestConfig;
import com.hibegin.http.server.config.ResponseConfig;
import com.hibegin.http.server.config.ServerConfig;
import com.hibegin.http.server.util.PathUtil;
import com.zaxxer.hikari.util.DriverDataSource;
import com.zrlog.admin.web.plugin.PluginCorePlugin;
import com.zrlog.admin.web.plugin.PluginCoreProcess;
import com.zrlog.admin.web.plugin.UpdateVersionPlugin;
import com.zrlog.blog.web.controller.api.ApiInstallController;
import com.zrlog.blog.web.plugin.CacheManagerPlugin;
import com.zrlog.blog.web.plugin.RequestStatisticsPlugin;
import com.zrlog.blog.web.version.UpgradeVersionHandler;
import com.zrlog.business.util.InstallUtils;
import com.zrlog.common.Constants;
import com.zrlog.common.InstallAction;
import com.zrlog.model.WebSite;
import com.zrlog.plugin.IPlugin;
import com.zrlog.plugin.Plugins;
import com.zrlog.util.BlogBuildInfoUtil;
import com.zrlog.util.DbConnectUtils;
import com.zrlog.util.I18nUtil;
import com.zrlog.util.ZrLogUtil;
import com.zrlog.web.inteceptor.GlobalBaseInterceptor;
import com.zrlog.web.inteceptor.MyI18nInterceptor;
import com.zrlog.web.inteceptor.RouterInterceptor;
import com.zrlog.web.inteceptor.StaticResourceInterceptor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 核心一些参数的配置。
 */
public class ZrLogConfig extends AbstractServerConfig implements InstallAction {

    private static final Logger LOGGER = LoggerUtil.getLogger(ZrLogConfig.class);


    /**
     * 配置的常用参数，这里可以看出来推崇使用代码进行配置的，而不是像Spring这样的通过预定配置方式。代码控制的好处在于高度可控制性，
     * 当然这也导致了很多程序员直接硬编码的问题。
     */
    @Override
    public ServerConfig getServerConfig() {
        ServerConfig serverConfig = new ServerConfig();
        serverConfig.setDisableSession(true);
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
        StaticResourceInterceptor.staticResourcePath.forEach(e -> serverConfig.addStaticResourceMapper(e, e, ZrLogConfig.class::getResourceAsStream));
        RouterUtils.configAdminRoute(serverConfig.getRouter());
        RouterUtils.configBlogRouter(serverConfig.getRouter());
        configInterceptor(serverConfig.getInterceptors());
        String webPort = System.getenv("PORT");
        if (Objects.nonNull(webPort)) {
            serverConfig.setPort(Integer.parseInt(webPort));
        } else {
            serverConfig.setPort(8080);
        }
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
     * 的拦截器，这里配置时需要区分先后顺序的。由于提供拦截器并没有类似Spring的拦截器可以对请求路径的配置，这里并不是很优雅。
     * 及需要在对应Interception中自行通过路由进行拦截。详细可以看 RouterInterceptor 这拦截器的代码,
     * 的Handler和其Interception有点类似，使用上需要主要一点（IHandler可以用于拦截处理静态资源文件，而Interception不会处理静态资源请求）
     */
    public void configInterceptor(List<Class<? extends Interceptor>> interceptors) {
        interceptors.add(GlobalBaseInterceptor.class);
        interceptors.add(StaticResourceInterceptor.class);
        interceptors.add(MyI18nInterceptor.class);
        interceptors.add(RouterInterceptor.class);
    }

    /***
     * 加载插件，比如提供的HikariCp，ZrLog自动检查更新，加载ZrLog提供的插件。
     */
    private void configPlugin(Plugins plugins) {
        // 如果没有安装的情况下不初始化数据
        if (!InstallUtils.isInstalled()) {
            LOGGER.log(Level.WARNING, "Not found lock file(" + PathUtil.getConfPath() + "/install.lock), Please visit the" + " http://yourHostName:port/install start installation");
            return;
        }

        try {
            tryInitDbPropertiesFile();
        } catch (Exception e){
            LOGGER.log(Level.SEVERE, "Init db properties exception ", e);
            System.exit(-1);
        }
        Properties dbProperties = InstallUtils.getDbProp();
        try (Connection connection=DbConnectUtils.getConnection(Objects.requireNonNull(dbProperties))){
            LOGGER.info("Db connect success " + connection.getCatalog());
        } catch (Exception e){
            LOGGER.log(Level.SEVERE,"Connect Database error ",e);
            System.exit(-1);
        }
        try {

            tryDoUpgrade(dbProperties);
            //启动时候进行数据库连接
            DAO.setDs(new DriverDataSource(dbProperties.getProperty("jdbcUrl"),
                    dbProperties.getProperty("driverClass"), dbProperties, dbProperties.getProperty("user"), dbProperties.getProperty("password")));
            Object pluginJvmArgsObj = BlogBuildInfoUtil.getBlogProp().get("pluginJvmArgs");
            if (pluginJvmArgsObj == null) {
                pluginJvmArgsObj = "";
            }
            if (!isTest()) {
                //这里使用独立的线程进行启动，主要是为了防止插件服务出问题后，影响整体，同时是避免启动过慢的问题。
                plugins.add(new PluginCorePlugin(Constants.getDbPropertiesFile(), pluginJvmArgsObj.toString()));
                plugins.add(new UpdateVersionPlugin());
                plugins.add(new CacheManagerPlugin());
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
        if(Objects.isNull(properties)){
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
        String currentVersion = ZrLogUtil.getCurrentSqlVersion(dbProp);
        List<Map.Entry<Integer, List<String>>> sqlList = ZrLogUtil.getExecSqlList(currentVersion);
        if (sqlList.isEmpty()) {
            return;
        }
        try (Connection connection = DbConnectUtils.getConnection(dbProp)){
            for (Map.Entry<Integer, List<String>> entry : sqlList) {

                if (Objects.isNull(connection)) {
                    return;
                }
                //执行需要更新的sql脚本
                Statement statement = connection.createStatement();
                try {
                    for (String sql : entry.getValue()) {
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
                    UpgradeVersionHandler upgradeVersionHandler = (UpgradeVersionHandler) Class.forName("com" + ".zrlog.web.version.V" + entry.getKey() + "UpgradeVersionHandler").getDeclaredConstructor().newInstance();
                    try {
                        upgradeVersionHandler.doUpgrade(connection);
                    } catch (Exception e) {
                        LOGGER.log(Level.SEVERE, "", e);
                        return;
                    }
                } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
                    LOGGER.log(Level.WARNING, "Try exec upgrade method error, " + e.getMessage());
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "", e);
        }
    }

    /**
     * 重写了的stop方法，目的是为了，系统正常停止后（如使用sh catalina.sh stop，进行自动更新时）,正常关闭插件，防治内存泄漏。
     */
    public void onStop() {
        PluginCoreProcess.getInstance().stopPluginCore();
        for (IPlugin plugin : Constants.plugins) {
            plugin.stop();
        }
    }


    @Override
    public void installFinish() {
        configPlugin(Constants.plugins);
        for (IPlugin plugin : Constants.plugins) {
            plugin.start();
        }
        if (!InstallUtils.isInstalled()) {
            return;
        }
        int updatedVersion = Constants.SQL_VERSION;
        if (updatedVersion > 0) {
            try {
                new WebSite().updateByKV(Constants.ZRLOG_SQL_VERSION_KEY, updatedVersion + "");
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
