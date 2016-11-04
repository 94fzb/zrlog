package com.fzb.blog.config;

import com.fzb.blog.controller.APIController;
import com.fzb.blog.controller.InstallController;
import com.fzb.blog.controller.QueryLogController;
import com.fzb.blog.incp.BlackListInterceptor;
import com.fzb.blog.incp.InitDataInterceptor;
import com.fzb.blog.incp.LoginInterceptor;
import com.fzb.blog.incp.MyI18NInterceptor;
import com.fzb.blog.model.*;
import com.fzb.blog.plugin.UpdateVersionPlugin;
import com.fzb.blog.util.BlogBuildInfoUtil;
import com.fzb.blog.util.InstallUtil;
import com.fzb.blog.util.PluginConfig;
import com.fzb.blog.util.ZrlogUtil;
import com.fzb.common.util.http.HttpUtil;
import com.fzb.common.util.http.handle.HttpFileHandle;
import com.jfinal.config.*;
import com.jfinal.core.JFinal;
import com.jfinal.ext.interceptor.SessionInViewInterceptor;
import com.jfinal.i18n.I18nInterceptor;
import com.jfinal.kit.PathKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.c3p0.C3p0Plugin;
import com.jfinal.render.ViewType;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Properties;
import java.util.Random;

/**
 * @author zhengchangchun JFinal 一些参数的配置
 */
public class ZrlogConfig extends JFinalConfig {

    private static final Logger LOGGER = Logger.getLogger(ZrlogConfig.class);
    private static final String PLUGIN_CORE_DOWNLOAD_URL = "http://dl.zrlog.com/release/plugin/plugin-core.jar";

    private static Properties systemProperties = new Properties();
    private Properties properties = new Properties();

    public static final String I18N = "i18n";

    static {
        try {
            systemProperties.load(ZrlogConfig.class.getResourceAsStream("/zrlog.properties"));
        } catch (IOException e) {
            LOGGER.error("load systemProperties error", e);
        }
    }

    private static void runBlogPlugin(final String dbPropertiesPath, final String pluginJvmArgs) {
        new Thread() {
            @Override
            public void run() {
                //加载 zrlog 提供的插件
                File pluginCoreFile = new File(PathKit.getWebRootPath() + "/WEB-INF/plugins/plugin-core.jar");
                if (!pluginCoreFile.exists()) {
                    pluginCoreFile.getParentFile().mkdirs();
                    String filePath = pluginCoreFile.getParentFile().toString();
                    try {
                        LOGGER.info("plugin-core.jar not exists will download from " + PLUGIN_CORE_DOWNLOAD_URL);
                        HttpUtil.sendGetRequest(PLUGIN_CORE_DOWNLOAD_URL, new HashMap<String, String[]>(), new HttpFileHandle(filePath), new HashMap<String, String>());
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

    public static ActiveRecordPlugin getActiveRecordPlugin(C3p0Plugin c3p0Plugin, String dbPropertiesPath) {
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

        Object pluginJvmArgsObj = systemProperties.get("pluginJvmArgs");
        if (pluginJvmArgsObj == null) {
            pluginJvmArgsObj = "";
        }
        runBlogPlugin(dbPropertiesPath, pluginJvmArgsObj.toString());
        return arp;
    }

    public void configConstant(Constants con) {
        con.setDevMode(!BlogBuildInfoUtil.isRelease());
        con.setViewType(ViewType.JSP);
        con.setEncoding("utf-8");
        con.setI18nDefaultBaseName(I18N);
        con.setI18nDefaultLocale("zh_CN");
        con.setError404View("/error/404.html");
        con.setError500View("/error/500.html");
        con.setError403View("/error/403.html");
        con.setBaseUploadPath(PathKit.getWebRootPath() + "/attached");
    }

    public void configHandler(Handlers handlers) {
        handlers.add(new PluginHandler());
        handlers.add(new JspSkipHandler());
    }

    public void configInterceptor(Interceptors incp) {
        incp.add(new SessionInViewInterceptor());
        incp.add(new InitDataInterceptor());
        incp.add(new I18nInterceptor());
        incp.add(new MyI18NInterceptor());
        incp.add(new BlackListInterceptor());
        incp.add(new LoginInterceptor());
    }

    public void configPlugin(Plugins plugins) {
        try {
            JFinal.me().getServletContext().setAttribute("plugins", plugins);
            // 如果不存在 install.lock 文件的情况下不初始化数据
            if (!new InstallUtil(PathKit.getWebRootPath() + "/WEB-INF").checkInstall()) {
                LOGGER.warn("Not found lock file(/WEB-INF/install.lock), Please visit the http://ip:port" + JFinal.me().getContextPath() + "/install installation");
                return;
            }
            String dbPropertiesPath = PathKit.getWebRootPath() + "/WEB-INF/db.properties";
            try {
                properties.load(new FileInputStream(dbPropertiesPath));
            } catch (IOException e) {
                LOGGER.error("load db.systemProperties error", e);
            }
            // 启动时候进行数据库连接
            C3p0Plugin c3p0Plugin = new C3p0Plugin(properties.getProperty("jdbcUrl"),
                    properties.getProperty("user"), properties.getProperty("password"));
            plugins.add(c3p0Plugin);
            // 添加表与实体的映射关系
            plugins.add(getActiveRecordPlugin(c3p0Plugin, dbPropertiesPath));
            plugins.add(new UpdateVersionPlugin());
        } catch (Exception e) {
            LOGGER.warn("configPlugin exception ", e);
        }

    }

    @Override
    public void afterJFinalStart() {
        super.afterJFinalStart();
        // 读取系统参数
        Properties systemProp = System.getProperties();
        systemProp.setProperty("zrlog.runtime.path", JFinal.me().getServletContext().getRealPath("/"));
        systemProp.setProperty("server.info", JFinal.me().getServletContext().getServerInfo());
        systemProp.put("dbServer.version", ZrlogUtil.getDatabaseServerVersion(properties.getProperty("jdbcUrl"), properties.getProperty("user"),
                properties.getProperty("password"), properties.getProperty("driverClass")));
        JFinal.me().getServletContext().setAttribute("system", systemProp);
        systemProperties.put("version", BlogBuildInfoUtil.getVersion());
        systemProperties.put("buildId", BlogBuildInfoUtil.getBuildId());
        systemProperties.put("buildTime", new SimpleDateFormat("yyyy-MM-dd").format(BlogBuildInfoUtil.getTime()));
        systemProperties.put("runMode", BlogBuildInfoUtil.getRunMode());
        JFinal.me().getServletContext().setAttribute("zrlog", systemProperties);
    }

    @Override
    public void beforeJFinalStop() {
        PluginConfig.stopPluginCore();
    }

    public void configRoute(Routes routes) {
        // 添加浏览者能访问Control 路由
        routes.add("/post", QueryLogController.class);
        routes.add("/api", APIController.class);
        routes.add("/", QueryLogController.class);
        routes.add("/install", InstallController.class);
        // 后台管理者
        routes.add(new UserRoutes());
    }
}
