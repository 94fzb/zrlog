package com.zrlog.web.config;

import com.hibegin.common.util.FileUtils;
import com.hibegin.common.util.IOUtil;
import com.hibegin.common.util.StringUtils;
import com.jfinal.config.*;
import com.jfinal.core.JFinal;
import com.jfinal.kit.PathKit;
import com.jfinal.plugin.IPlugin;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.IDataSourceProvider;
import com.jfinal.plugin.hikaricp.HikariCpPlugin;
import com.jfinal.render.ViewType;
import com.jfinal.template.Engine;
import com.zrlog.admin.web.config.AdminRoutes;
import com.zrlog.admin.web.plugin.PluginCorePlugin;
import com.zrlog.admin.web.plugin.PluginCoreProcess;
import com.zrlog.admin.web.plugin.UpdateVersionPlugin;
import com.zrlog.blog.web.config.BlogRoutes;
import com.zrlog.blog.web.controller.api.ApiInstallController;
import com.zrlog.blog.web.handler.BlogArticleHandler;
import com.zrlog.blog.web.handler.GlobalResourceHandler;
import com.zrlog.blog.web.handler.PluginHandler;
import com.zrlog.blog.web.interceptor.InitDataInterceptor;
import com.zrlog.blog.web.interceptor.MyI18nInterceptor;
import com.zrlog.blog.web.plugin.CacheManagerPlugin;
import com.zrlog.blog.web.plugin.RequestStatisticsPlugin;
import com.zrlog.blog.web.version.UpgradeVersionHandler;
import com.zrlog.business.service.InstallAction;
import com.zrlog.business.util.InstallUtils;
import com.zrlog.model.*;
import com.zrlog.util.BlogBuildInfoUtil;
import com.zrlog.util.ZrLogUtil;
import com.zrlog.web.inteceptor.RouterInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

/**
 * JFinal核心一些参数的配置。
 */
public class ZrLogConfig extends JFinalConfig implements InstallAction {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZrLogConfig.class);

    private static final String DEFAULT_PREVIEW_DB_HOST = "demo.blog.zrlog.com";

    private static String jdbcUrl;

    /**
     * 存放为config的属性，是为了安装完成后还获得JFinal的插件列表对象
     */
    private Plugins plugins;

    private boolean haveSqlUpdated = false;

    /**
     * 读取Zrlog的一些配置，主要是避免硬编码的问题
     */
    public ZrLogConfig() {
        //bae磁盘空间有限，且无管理方式，删掉升级过程中产生的备份文件
        if (ZrLogUtil.isBae()) {
            FileUtils.deleteFile("/home/bae/backup");
        }
    }

    public static boolean isTest() {
        return "junit-test".equals(InstallUtils.getSystemProp().getProperty("env"));
    }

    public static boolean isPreviewDb() {
        return jdbcUrl != null && jdbcUrl.contains(DEFAULT_PREVIEW_DB_HOST) && !ZrLogUtil.isInternalHostName(DEFAULT_PREVIEW_DB_HOST);
    }

    private String getUpgradeSqlBasePath() {
        return PathKit.getWebRootPath() + "/WEB-INF/update-sql";
    }

    /**
     * 配置JFinal提供过简易版本的ORM（其实这里是叫Active+Record）。
     */
    private ActiveRecordPlugin getActiveRecordPlugin(IDataSourceProvider dataSourceProvider) {
        ActiveRecordPlugin arp = new ActiveRecordPlugin("c3p0Plugin" + new Random().nextInt(), dataSourceProvider);
        arp.addMapping(User.TABLE_NAME, "userId", User.class);
        arp.addMapping(Log.TABLE_NAME, "logId", Log.class);
        arp.addMapping(Type.TABLE_NAME, "typeId", Type.class);
        arp.addMapping(Link.TABLE_NAME, "linkId", Link.class);
        arp.addMapping(Comment.TABLE_NAME, "commentId", Comment.class);
        arp.addMapping(LogNav.TABLE_NAME, "navId", LogNav.class);
        arp.addMapping(WebSite.TABLE_NAME, "siteId", WebSite.class);
        arp.addMapping(Plugin.TABLE_NAME, "pluginId", Plugin.class);
        arp.addMapping(Tag.TABLE_NAME, "tagId", Tag.class);
        return arp;
    }

    /**
     * 配置JFinal的常用参数，这里可以看出来JFinal推崇使用代码进行配置的，而不是像Spring这样的通过预定配置方式。代码控制的好处在于高度可控制性，
     * 当然这也导致了很多程序员直接硬编码的问题。
     */
    @Override
    public void configConstant(Constants con) {
        con.setDevMode(BlogBuildInfoUtil.isDev());
        con.setViewType(ViewType.JSP);
        con.setEncoding("utf-8");
        con.setJsonDatePattern("yyyy-MM-dd");
        con.setError404View(com.zrlog.common.Constants.NOT_FOUND_PAGE);
        con.setError500View(com.zrlog.common.Constants.ERROR_PAGE);
        con.setError403View(com.zrlog.common.Constants.FORBIDDEN_PAGE);
        con.setBaseUploadPath(PathKit.getWebRootPath() + com.zrlog.common.Constants.ATTACHED_FOLDER);
        //最大的提交的body的大小
        con.setMaxPostSize(1024 * 1024 * 1024);
    }

    /**
     * JFinal的Handler和其Interception有点类似，使用上需要主要一点（IHandler可以用于拦截处理静态资源文件，而Interception不会处理静态资源请求）
     */
    @Override
    public void configHandler(Handlers handlers) {
        handlers.add(new GlobalResourceHandler());
        handlers.add(new BlogArticleHandler());
        handlers.add(new PluginHandler());
    }

    /**
     * JFinal的拦截器，这里配置时需要区分先后顺序的。由于JFinal提供拦截器并没有类似Spring的拦截器可以对请求路径的配置，这里并不是很优雅。
     * 及需要在对应Interception中自行通过路由进行拦截。详细可以看 RouterInterceptor 这拦截器的代码
     */
    @Override
    public void configInterceptor(Interceptors interceptors) {
        interceptors.add(new InitDataInterceptor());
        interceptors.add(new MyI18nInterceptor());
        interceptors.add(new RouterInterceptor());
    }

    /***
     * 加载JFinal的插件，比如JFinal提供的C3P0Plugin，Zrlog自动检查更新，加载Zrlog提供的插件。
     */
    @Override
    public void configPlugin(Plugins plugins) {
        // 如果没有安装的情况下不初始化数据
        if (InstallUtils.isInstalled()) {
            if (StringUtils.isNotEmpty(ZrLogUtil.getDbInfoByEnv())) {
                IOUtil.writeBytesToFile(ZrLogUtil.getDbInfoByEnv().getBytes(),
                        new File(InstallUtils.getDbPropertiesFilePath()));
            }
            try {
                Properties dbProperties = InstallUtils.getDbProp();
                tryUpgradeDbPropertiesFile(InstallUtils.getDbPropertiesFilePath(),
                        Objects.requireNonNull(dbProperties));
                tryDoUpgrade(getUpgradeSqlBasePath(), dbProperties.getProperty("jdbcUrl"), dbProperties.getProperty(
                        "user"), dbProperties.getProperty("password"), dbProperties.getProperty("driverClass"));
                jdbcUrl = dbProperties.getProperty("jdbcUrl");

                // 启动时候进行数据库连接
                HikariCpPlugin dataSourcePlugin = new HikariCpPlugin(dbProperties.getProperty("jdbcUrl"),
                        dbProperties.getProperty("user"), dbProperties.getProperty("password"));
                plugins.add(dataSourcePlugin);
                // 添加表与实体的映射关系
                plugins.add(getActiveRecordPlugin(dataSourcePlugin));
                Object pluginJvmArgsObj = BlogBuildInfoUtil.getBlogProp().get("pluginJvmArgs");
                if (pluginJvmArgsObj == null) {
                    pluginJvmArgsObj = "";
                }
                if (!isTest()) {
                    //这里使用独立的线程进行启动，主要是为了防止插件服务出问题后，影响整体，同时是避免启动过慢的问题。
                    plugins.add(new PluginCorePlugin(InstallUtils.getDbPropertiesFilePath(),
                            pluginJvmArgsObj.toString()));
                    plugins.add(new UpdateVersionPlugin());
                    plugins.add(new CacheManagerPlugin());
                }
                plugins.add(new RequestStatisticsPlugin());
            } catch (Exception e) {
                LOGGER.warn("configPlugin exception ", e);
            }
        } else {
            LOGGER.warn("Not found lock file(" + PathKit.getWebRootPath() + "/WEB-INF/install.lock), Please visit the" +
                    " http://yourHostName:port" + JFinal.me().getContextPath() + "/install start installation");
        }

        JFinal.me().getServletContext().setAttribute("plugins", plugins);
        this.plugins = plugins;
    }

    /**
     * 版本低于1.10.1进行升级
     */
    private void tryUpgradeDbPropertiesFile(String dbFile, Properties properties) throws IOException {
        if (!"com.mysql.cj.jdbc.Driver".equals(properties.get("driverClass"))) {
            properties.put("driverClass", "com.mysql.cj.jdbc.Driver");
            properties.put("jdbcUrl", properties.get("jdbcUrl") + "&" + ApiInstallController.JDBC_URL_BASE_QUERY_PARAM);
            properties.store(new FileOutputStream(dbFile), "Support mysql8");
        }
    }

    /**
     * 设置系统参数到Servlet的Context用于后台管理的主页面的展示，读取Zrlog的版本信息等。
     */
    @Override
    public void onStart() {
        super.onStart();

        JFinal.me().getServletContext().setAttribute("config", this);
        if (haveSqlUpdated) {
            int updatedVersion = ZrLogUtil.getSqlVersion(getUpgradeSqlBasePath());
            if (updatedVersion > 0) {
                new WebSite().updateByKV(com.zrlog.common.Constants.ZRLOG_SQL_VERSION_KEY, updatedVersion + "");
            }
        }
    }

    /**
     * 检查数据文件是否需要更新
     * 为了处理由于数据库表的更新，导致系统无法正常使用的情况，通过执行/WEB-INF/update-sql/目录下面的*.sql文件来变更数据库的表格式，
     * 来达到系统无需手动执行数据库脚本文件。
     */
    private void tryDoUpgrade(String basePath, String jdbcUrl, String user, String password, String driverClass) {
        String currentVersion = ZrLogUtil.getCurrentSqlVersion(jdbcUrl, user, password, driverClass);
        List<Map.Entry<Integer, List<String>>> sqlList = ZrLogUtil.getExecSqlList(currentVersion, basePath);
        if (!sqlList.isEmpty()) {
            try {
                for (Map.Entry<Integer, List<String>> entry : sqlList) {
                    Connection connection = ZrLogUtil.getConnection(jdbcUrl, user, password, driverClass);
                    if (connection != null) {
                        //执行需要更新的sql脚本
                        Statement statement = connection.createStatement();
                        try {
                            for (String sql : entry.getValue()) {
                                statement.execute(sql);
                            }
                        } catch (Exception e) {
                            LOGGER.error("execution sql ", e);
                            //有异常终止升级
                            return;
                        } finally {
                            if (statement != null) {
                                try {
                                    statement.close();
                                } catch (SQLException e) {
                                    LOGGER.error("", e);
                                }
                            }
                        }
                        //执行需要转换的数据
                        try {
                            UpgradeVersionHandler upgradeVersionHandler = (UpgradeVersionHandler) Class.forName("com" +
                                    ".zrlog.web.version.V" + entry.getKey() + "UpgradeVersionHandler").getDeclaredConstructor().newInstance();
                            try {
                                upgradeVersionHandler.doUpgrade(connection);
                            } catch (Exception e) {
                                LOGGER.error("", e);
                                return;
                            }
                        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
                            LOGGER.warn("Try exec upgrade method error, " + e.getMessage());
                        } finally {
                            connection.close();
                        }
                    }
                }
            } catch (Exception e) {
                LOGGER.error("", e);
            }
            haveSqlUpdated = true;
        }
    }

    /**
     * 重写了JFinal的stop方法，目的是为了，系统正常停止后（如使用sh catalina.sh stop，进行自动更新时）,正常关闭插件，防治内存泄漏。
     */
    @Override
    public void onStop() {
        PluginCoreProcess.getInstance().stopPluginCore();
        for (IPlugin plugin : plugins.getPluginList()) {
            plugin.stop();
        }
    }

    /**
     * 配置JFinal的路由，其对应的ActionMapper，目前Zrlog拦截了所有，主要表现为将/的路由分配给了PostController使用，当然使用/post，也是
     * 一样的效果，如果需要扩展前台页面添加对应的路由即可，需要注意的，Zrlog统一了页面的渲染。
     * 即还需要在 VisitorInterception 根据request域中的key判断，然后渲染对应的页面
     *
     * @param routes （JFinal的Routes，类servlet的Mapper，但是JFinal是自己通过request URI path自己作的路由）
     */
    @Override
    public void configRoute(Routes routes) {
        // 后台管理者
        routes.add(new AdminRoutes());
        routes.add(new BlogRoutes());
    }

    @Override
    public void configEngine(Engine engine) {

    }

    @Override
    public void installFinish() {
        configPlugin(plugins);
        List<IPlugin> pluginList = plugins.getPluginList();
        for (IPlugin plugin : pluginList) {
            plugin.start();
        }
    }
}
