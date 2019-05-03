package com.zrlog.web.controller.blog;

import com.jfinal.core.Controller;
import com.jfinal.core.JFinal;
import com.jfinal.kit.PathKit;
import com.zrlog.common.type.TestConnectDbResult;
import com.zrlog.service.InstallService;
import com.zrlog.util.I18nUtil;
import com.zrlog.web.config.ZrLogConfig;

import java.util.HashMap;
import java.util.Map;

/**
 * 与安装向导相关的路由进行控制
 * 注意 install.lock 文件相当重要，如果不是重新安装请不要删除这个自动生成的文件
 */
public class InstallController extends Controller {


    /**
     * 检查数据库是否可以正常连接使用，无法连接时给出相应的提示
     */
    public String testDbConn() {
        Map<String, String> dbConn = new HashMap<>();
        dbConn.put("jdbcUrl", "jdbc:mysql://" + getPara("dbhost") + ":" + getPara("port") + "/" + getPara("dbname")
                + "?" + ZrLogConfig.JDBC_URL_BASE_QUERY_PARAM);
        dbConn.put("user", getPara("dbuser"));
        dbConn.put("password", getPara("dbpwd"));
        dbConn.put("driverClass", "com.mysql.cj.jdbc.Driver");
        TestConnectDbResult testConnectDbResult = new InstallService(PathKit.getWebRootPath() + "/WEB-INF", dbConn).testDbConn();
        if (testConnectDbResult.getError() != 0) {
            setAttr("errorMsg", "[Error-" + testConnectDbResult.getError() + "] - " + I18nUtil.getStringFromRes("connectDbError_" + testConnectDbResult.getError()));
            return index();
        } else {
            JFinal.me().getServletContext().setAttribute("dbConn", dbConn);
            return "/install/message";
        }
    }

    /**
     * 数据库检查通过后，根据填写信息，执行数据表，表数据的初始化
     */
    public String install() {
        Map<String, String> dbConn = (Map<String, String>) JFinal.me().getServletContext().getAttribute("dbConn");
        Map<String, String> configMsg = new HashMap<>();
        configMsg.put("title", getPara("title"));
        configMsg.put("second_title", getPara("second_title"));
        configMsg.put("username", getPara("username"));
        configMsg.put("password", getPara("password"));
        configMsg.put("email", getPara("email"));
        if (new InstallService(PathKit.getWebRootPath() + "/WEB-INF", dbConn, configMsg).install()) {
            final ZrLogConfig config = (ZrLogConfig) JFinal.me().getServletContext().getAttribute("config");
            //通知启动插件，配置库连接等操作
            config.installFinish();
            return "/install/success";
        } else {
            setAttr("errorMsg", "[Error-" + TestConnectDbResult.UNKNOWN.getError() + "] - " + I18nUtil.getStringFromRes("connectDbError_" + TestConnectDbResult.UNKNOWN.getError()));
            return "/install/message";
        }
    }

    /**
     * 加载安装向导第一个页面数据
     */
    public String index() {
        setAttr("isUTF", ZrLogConfig.SYSTEM_PROP.getProperty("file.encoding").toLowerCase().startsWith("utf"));
        return "/install/index";
    }
}
