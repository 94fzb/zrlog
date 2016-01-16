package com.fzb.blog.config;

import com.fzb.blog.controller.APIController;
import com.fzb.blog.controller.InstallController;
import com.fzb.blog.controller.QueryLogController;
import com.fzb.blog.incp.BlackListInterceptor;
import com.fzb.blog.incp.InitDataInterceptor;
import com.fzb.blog.incp.LoginInterceptor;
import com.fzb.blog.model.*;
import com.fzb.blog.util.InstallUtil;
import com.fzb.blog.util.plugin.PluginsUtil;
import com.fzb.blog.util.plugin.api.IZrlogPlugin;
import com.jfinal.config.*;
import com.jfinal.core.JFinal;
import com.jfinal.ext.interceptor.SessionInViewInterceptor;
import com.jfinal.kit.PathKit;
import com.jfinal.plugin.activerecord.ActiveRecordPlugin;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.c3p0.C3p0Plugin;
import com.jfinal.plugin.ehcache.EhCachePlugin;
import com.jfinal.render.ViewType;
import flexjson.JSONDeserializer;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;

/**
 * @author zhengchangchun JFinal 一些参数的配置
 */
public class ZrlogConfig extends JFinalConfig {

    private static Logger LOGGER = Logger.getLogger(ZrlogConfig.class);

    public void configConstant(Constants con) {
        //con.setDevMode(true);
        con.setViewType(ViewType.JSP);
        con.setEncoding("utf-8");
        /*con.setI18n("i18n");
        I18N.init("i18n", null, null);*/
        con.setError404View("/error/404.html");
        con.setError500View("/error/500.html");
        con.setError403View("/error/403.html");
        con.setBaseUploadPath(PathKit.getWebRootPath() + "/attached");
    }

    public void configHandler(Handlers handlers) {
        handlers.add(new JspSkipHandler());
    }

    public void configInterceptor(Interceptors incp) {
        incp.add(new SessionInViewInterceptor());
        incp.add(new InitDataInterceptor());
        incp.add(new BlackListInterceptor());
        incp.add(new LoginInterceptor());
    }

    public void configPlugin(Plugins plugins) {
        try {
            JFinal.me().getServletContext().setAttribute("plugins", plugins);
            //config ehcache
            if (new File(PathKit.getRootClassPath() + "/ehcache.xml").exists()) {
                plugins.add(new EhCachePlugin(PathKit.getRootClassPath() + "/ehcache.xml"));
            } else {
                plugins.add(new EhCachePlugin());
            }
            // 如果不存在 install.lock 文件的情况下不初始化数据
            if (!new InstallUtil(PathKit.getWebRootPath() + "/WEB-INF")
                    .checkInstall()) {
                LOGGER.warn("Not found lock file(/WEB-INF/install.lock), Please visit the http://ip:port" + JFinal.me().getContextPath() + "/install installation");
                return;
            }
            Properties properties = new Properties();
            try {
                properties.load(new FileInputStream(PathKit.getWebRootPath() + "/WEB-INF/db.properties"));
            } catch (IOException e) {
                LOGGER.error("load db.properties error", e);
            }
            // 启动时候进行数据库连接
            C3p0Plugin c3p0Plugin = new C3p0Plugin(properties.getProperty("jdbcUrl"),
                    properties.getProperty("user"), properties.getProperty("password"));
            plugins.add(c3p0Plugin);
            // 添加表与实体的映射关系
            plugins.add(getActiveRecordPlugin(c3p0Plugin));

        } catch (Exception e) {
            LOGGER.warn("configPlugin exception ", e);
        }

    }

    @Override
    public void afterJFinalStart() {
        super.afterJFinalStart();
        //加载 zrlog 提供的插件
        try {
            List<Object[]> zPlugins = Db.query("select content,pluginName from plugin where level=?", -1);
            if (zPlugins != null && zPlugins.size() > 0) {
                for (Object[] pluginStr : zPlugins) {
                    Map<String, Object> map = new JSONDeserializer<Map<String, Object>>().deserialize(pluginStr[0].toString());
                    try {
                        Object tPlugin = Class.forName(map.get("classLoader").toString()).newInstance();
                        if (tPlugin instanceof IZrlogPlugin) {
                            if (Integer.parseInt(map.get("status").toString()) == 2) {
                                PluginsUtil.addPlugin(pluginStr[1].toString(), (IZrlogPlugin) tPlugin);
                            }
                        }
                    } catch (Exception e) {
                        LOGGER.warn("load Plugin " + pluginStr[1] + " failed " + e.getMessage());
                    }

                }
            }
        } catch (Exception e) {
            LOGGER.warn("start plugin exception ", e);
        }
        // 读取系统参数
        Properties systemProp = System.getProperties();
        systemProp.setProperty("zrlog.runtime.path", JFinal.me().getServletContext().getRealPath("/"));
        JFinal.me().getServletContext().setAttribute("system", systemProp);
        Properties properties = new Properties();
        try {
            properties.load(ZrlogConfig.class.getResourceAsStream("/zrlog.properties"));
        } catch (IOException e) {
            LOGGER.warn("load zrlog.properties error");
        }
        JFinal.me().getServletContext().setAttribute("zrlog", properties);

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

    public static ActiveRecordPlugin getActiveRecordPlugin(C3p0Plugin c3p0Plugin) {
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
}