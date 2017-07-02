package com.fzb.blog.web.config;

import com.fzb.blog.model.*;
import com.fzb.blog.service.InstallService;
import com.fzb.blog.util.BlogBuildInfoUtil;
import com.fzb.blog.util.ZrlogUtil;
import com.fzb.blog.web.controller.blog.APIController;
import com.fzb.blog.web.controller.blog.InstallController;
import com.fzb.blog.web.controller.blog.PostController;
import com.fzb.blog.web.handler.PluginHandler;
import com.fzb.blog.web.handler.StaticFileCheckHandler;
import com.fzb.blog.web.incp.BlackListInterceptor;
import com.fzb.blog.web.incp.InitDataInterceptor;
import com.fzb.blog.web.incp.MyI18NInterceptor;
import com.fzb.blog.web.incp.RouterInterceptor;
import com.fzb.blog.web.plugin.PluginConfig;
import com.fzb.blog.web.plugin.UpdateVersionPlugin;
import com.fzb.common.util.http.HttpUtil;
import com.fzb.common.util.http.handle.HttpFileHandle;
import com.jfinal.config.*;
import com.jfinal.core.JFinal;
import com.jfinal.ext.interceptor.SessionInViewInterceptor;
import com.jfinal.kit.PathKit;
import com.jfinal.plugin.IPlugin;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.c3p0.C3p0Plugin;
import com.jfinal.render.ViewType;
import com.jfinal.template.Engine;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Random;

/**
 * JFinal核心一些参数的配置。
 */
public class ZrlogConfig extends JFinalConfig {

    private static final Logger LOGGER = Logger.getLogger(ZrlogConfig.class);
    //插件服务的下载地址
    private static final String PLUGIN_CORE_DOWNLOAD_URL = com.fzb.blog.common.Constants.ZRLOG_RESOURCE_DOWNLOAD_URL + "/plugin/core/plugin-core.jar";
    //存放Zrlog的一些系统参数
    private Properties systemProperties = new Properties();
    private Properties dbProperties = new Properties();
    // 读取系统参数
    private Properties systemProp = System.getProperties();
    //存放为config的属性，是为了安装完成后还获得JFinal的插件列表对象
    private Plugins plugins;

    /**
     * 读取Zrlog的一些配置，主要是避免硬编码的问题
     */
    public ZrlogConfig() {
        try {
            systemProperties.load(ZrlogConfig.class.getResourceAsStream("/zrlog.properties"));
        } catch (IOException e) {
            LOGGER.error("load systemProperties error", e);
        }
    }

    /**
     * 通过检查WEB-INF目录下面是否有 install.lock 文件来判断是否已经安装过了，这里为静态工具方法，方便其他类调用。
     */
    public static boolean isInstalled() {
        return new InstallService(PathKit.getWebRootPath() + "/WEB-INF").checkInstall();
    }

    /**
     * 运行Zrlog的插件，当WEB-INF/plugins/这里目录下面不存在plugin-core.jar时，会通过网络请求下载最新的plugin核心服务，也可以通过
     * 这种方式进行插件的及时更新。
     * plugin-core也是一个java进程，通过调用系统命令的命令进行启动的。
     *
     * @param dbPropertiesPath
     * @param pluginJvmArgs
     */
    private void runBlogPlugin(final String dbPropertiesPath, final String pluginJvmArgs) {
        //这里使用独立的线程进行启动，主要是为了防止插件服务出问题后，影响整体，同时是避免启动过慢的问题。
        new Thread() {
            @Override
            public void run() {
                PluginConfig.stopPluginCore();
                //加载 zrlog 提供的插件
                File pluginCoreFile = new File(PathKit.getWebRootPath() + "/WEB-INF/plugins/plugin-core.jar");
                if (!pluginCoreFile.exists()) {
                    pluginCoreFile.getParentFile().mkdirs();
                    String filePath = pluginCoreFile.getParentFile().toString();
                    try {
                        LOGGER.info("plugin-core.jar not exists will download from " + PLUGIN_CORE_DOWNLOAD_URL);
                        HttpUtil.getInstance().sendGetRequest(PLUGIN_CORE_DOWNLOAD_URL + "?_=" + System.currentTimeMillis(), new HashMap<String, String[]>(), new HttpFileHandle(filePath), new HashMap<String, String>());
                    } catch (IOException e) {
                        LOGGER.warn("download plugin core error", e);
                    }
                }
                int port = PluginConfig.pluginServerStart(pluginCoreFile, dbPropertiesPath, pluginJvmArgs, PathKit.getWebRootPath(), BlogBuildInfoUtil.getVersion());
                JFinal.me().getServletContext().setAttribute("pluginServerPort", port);
                JFinal.me().getServletContext().setAttribute("pluginServer", "http://localhost:" + port);
            }
        }.start();

    }

    /**
     * 配置JFinal提供过简易版本的ORM（其实这里是叫Active+Record）。
     *
     * @param c3p0Plugin
     * @return
     */
    private ActiveRecordPlugin getActiveRecordPlugin(C3p0Plugin c3p0Plugin) {
        ActiveRecordPlugin arp = new ActiveRecordPlugin("c3p0Plugin" + new Random().nextInt(), c3p0Plugin);
        arp.addMapping("user", "userId", User.class);
        arp.addMapping("log", "logId", Log.class);
        arp.addMapping("type", "typeId", Type.class);
        arp.addMapping("link", "linkId", Link.class);
        arp.addMapping("comment", "commentId", Comment.class);
        arp.addMapping("lognav", "navId", LogNav.class);
        arp.addMapping("website", "siteId", WebSite.class);
        arp.addMapping("plugin", "pluginId", Plugin.class);
        arp.addMapping("tag", "tagId", Tag.class);
        return arp;
    }

    /**
     * 配置JFinal的常用参数，这里可以看出来JFinal推崇使用代码进行配置的，而不是像Spring这样的通过预定配置方式。代码控制的好处在于高度可控制性，
     * 当然这也导致了很多程序员直接硬编码的问题。
     *
     * @param con
     */
    public void configConstant(Constants con) {
        con.setDevMode(BlogBuildInfoUtil.isDev());
        con.setViewType(ViewType.JSP);
        con.setEncoding("utf-8");
        con.setI18nDefaultBaseName(com.fzb.blog.common.Constants.I18N);
        con.setI18nDefaultLocale("zh_CN");
        con.setError404View("/error/404.html");
        con.setError500View("/error/500.html");
        con.setError403View("/error/403.html");
        con.setBaseUploadPath(PathKit.getWebRootPath() + "/attached");
    }

    /**
     * JFinal的Handler和其Interception有点类似，使用上需要主要一点（IHandler可以用于拦截处理静态资源文件，而Interception不会处理静态资源请求）
     *
     * @param handlers
     */
    public void configHandler(Handlers handlers) {
        handlers.add(new PluginHandler());
        handlers.add(new StaticFileCheckHandler());
    }

    /**
     * JFinal的拦截器，这里配置时需要区分先后顺序的。由于JFinal提供拦截器并没有类似Spring的拦截器可以对请求路径的配置，这里并不是很优雅。
     * 及需要在对应Interception中自行通过路由进行拦截。详细可以看 RouterInterceptor 这拦截器的代码
     *
     * @param incp
     */
    public void configInterceptor(Interceptors incp) {
        incp.add(new SessionInViewInterceptor());
        incp.add(new InitDataInterceptor());
        incp.add(new MyI18NInterceptor());
        incp.add(new BlackListInterceptor());
        incp.add(new RouterInterceptor());
    }

    /***
     * 加载JFinal的插件，比如JFinal提供的C3P0Plugin，Zrlog自动检查更新，加载Zrlog提供的插件。
     */
    public void configPlugin(Plugins plugins) {
        FileInputStream in = null;
        try {
            // 如果没有安装的情况下不初始化数据
            if (isInstalled()) {
                String dbPropertiesFile = PathKit.getWebRootPath() + "/WEB-INF/db.properties";
                try {
                    in = new FileInputStream(dbPropertiesFile);
                    dbProperties.load(in);
                } catch (IOException e) {
                    LOGGER.error("load db.systemProperties error", e);
                }
                systemProp.put("dbServer.version", ZrlogUtil.getDatabaseServerVersion(dbProperties.getProperty("jdbcUrl"), dbProperties.getProperty("user"),
                        dbProperties.getProperty("password"), dbProperties.getProperty("driverClass")));
                // 启动时候进行数据库连接
                C3p0Plugin c3p0Plugin = new C3p0Plugin(dbProperties.getProperty("jdbcUrl"),
                        dbProperties.getProperty("user"), dbProperties.getProperty("password"));
                plugins.add(c3p0Plugin);
                // 添加表与实体的映射关系
                plugins.add(getActiveRecordPlugin(c3p0Plugin));
                Object pluginJvmArgsObj = systemProperties.get("pluginJvmArgs");
                if (pluginJvmArgsObj == null) {
                    pluginJvmArgsObj = "";
                }
                runBlogPlugin(dbPropertiesFile, pluginJvmArgsObj.toString());
                plugins.add(new UpdateVersionPlugin());
            }
        } catch (Exception e) {
            LOGGER.warn("configPlugin exception ", e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    LOGGER.error("close stream error", e);
                }
            }
        }
        JFinal.me().getServletContext().setAttribute("plugins", plugins);
        this.plugins = plugins;
    }

    /**
     * 设置系统参数到Servlet的Context用于后台管理的主页面的展示，读取Zrlog的版本信息等。
     * 为了处理由于数据库表的更新，导致系统无法正常使用的情况，通过执行/WEB-INF/update-sql/目录下面的*.sql文件来变更数据库的表格式，
     * 来达到系统无需手动执行数据库脚本文件。
     */
    @Override
    public void afterJFinalStart() {
        super.afterJFinalStart();
        if (!isInstalled()) {
            LOGGER.warn("Not found lock file(" + PathKit.getWebRootPath() + "/WEB-INF/install.lock), Please visit the http://yourHostName:port" + JFinal.me().getContextPath() + "/install installation");
        } else {
            //检查数据文件是否需要更新
            String sqlVersion = WebSite.dao.getValueByName(com.fzb.blog.common.Constants.ZRLOG_SQL_VERSION_KEY);
            Integer updatedVersion = ZrlogUtil.doUpgrade(sqlVersion, PathKit.getWebRootPath() + "/WEB-INF/update-sql", dbProperties.getProperty("jdbcUrl"), dbProperties.getProperty("user"),
                    dbProperties.getProperty("password"), dbProperties.getProperty("driverClass"));
            if (updatedVersion > 0) {
                WebSite.dao.updateByKV(com.fzb.blog.common.Constants.ZRLOG_SQL_VERSION_KEY, updatedVersion + "");
            }
        }
        systemProp.setProperty("zrlog.runtime.path", JFinal.me().getServletContext().getRealPath("/"));
        systemProp.setProperty("server.info", JFinal.me().getServletContext().getServerInfo());
        JFinal.me().getServletContext().setAttribute("system", systemProp);
        systemProperties.put("version", BlogBuildInfoUtil.getVersion());
        systemProperties.put("buildId", BlogBuildInfoUtil.getBuildId());
        systemProperties.put("buildTime", new SimpleDateFormat("yyyy-MM-dd").format(BlogBuildInfoUtil.getTime()));
        systemProperties.put("runMode", BlogBuildInfoUtil.getRunMode());
        JFinal.me().getServletContext().setAttribute("zrlog", systemProperties);
        JFinal.me().getServletContext().setAttribute("config", this);
    }

    /**
     * 重写了JFinal的stop方法，目的是为了，系统正常停止后（如使用sh catalina.sh stop，进行自动更新时）,正常关闭插件，防治内存泄漏。
     */
    @Override
    public void beforeJFinalStop() {
        PluginConfig.stopPluginCore();
    }

    /**
     * 配置JFinal的路由，其对应的ActionMapper，目前Zrlog拦截了所有，主要表现为将/的路由分配给了PostController使用，当然使用/post，也是
     * 一样的效果，如果需要扩展前台页面添加对应的路由即可，需要注意的，Zrlog统一了页面的渲染。
     * 即还需要在 VisitorInterception 根据request域中的key判断，然后渲染对应的页面
     *
     * @param routes （JFinal的Routes，类servlet的Mapper，但是JFinal是自己通过request URI path自己作的路由）
     */
    public void configRoute(Routes routes) {
        // 添加浏览者能访问Control 路由
        routes.add("/post", PostController.class);
        routes.add("/api", APIController.class);
        routes.add("/", PostController.class);
        routes.add("/install", InstallController.class);
        // 后台管理者
        routes.add(new AdminRoutes());
    }

    @Override
    public void configEngine(Engine engine) {

    }

    /**
     * 当安装流程正常执行完成时，调用了该方法，主要用于配置，启动JFinal插件功能，以及相应的Zrlog的插件服务。
     */
    public void installFinish() {
        configPlugin(plugins);
        List<IPlugin> pluginList = plugins.getPluginList();
        for (IPlugin plugin : pluginList) {
            plugin.start();
        }
    }
}
