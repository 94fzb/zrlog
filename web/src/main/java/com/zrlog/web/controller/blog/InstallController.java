package com.zrlog.web.controller.blog;

import com.jfinal.core.Controller;
import com.jfinal.core.JFinal;
import com.jfinal.kit.PathKit;
import com.zrlog.common.type.TestConnectDbResult;
import com.zrlog.service.InstallService;
import com.zrlog.util.I18NUtil;
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
    public void testDbConn() {
        Map<String, String> dbConn = new HashMap<>();
        dbConn.put("jdbcUrl", "jdbc:mysql://" + getPara("dbhost") + ":"
                + getPara("port") + "/" + getPara("dbname")
                + "?&characterEncoding=UTF-8");
        dbConn.put("user", getPara("dbuser"));
        dbConn.put("password", getPara("dbpwd"));
        dbConn.put("driverClass", "com.mysql.jdbc.Driver");
        JFinal.me().getServletContext().setAttribute("dbConn", dbConn);
        TestConnectDbResult testConnectDbResult = new InstallService(PathKit.getWebRootPath() + "/WEB-INF", dbConn).testDbConn();
        if (testConnectDbResult.getError() != 0) {
            setAttr("errorMsg", "[Error-" + testConnectDbResult.getError() + "] - " + I18NUtil.getStringFromRes("connectDbError_" + testConnectDbResult.getError(), getRequest()));
            index();
        } else {
            render("/install/message.jsp");
        }
    }

    /**
     * 数据库检查通过后，根据填写信息，执行数据表，表数据的初始化
     */
    public void installZrlog() {
        String home = getRequest().getScheme() + "://" + getRequest().getHeader("host") + getRequest().getContextPath() + "/";
        Map<String, String> dbConn = (Map<String, String>) JFinal.me().getServletContext().getAttribute("dbConn");
        Map<String, String> configMsg = new HashMap<>();
        configMsg.put("title", getPara("title"));
        configMsg.put("second_title", getPara("second_title"));
        configMsg.put("username", getPara("username"));
        configMsg.put("password", getPara("password"));
        configMsg.put("email", getPara("email"));
        configMsg.put("home", home);
        if (new InstallService(PathKit.getWebRootPath() + "/WEB-INF", dbConn, configMsg).install()) {
            render("/install/success.jsp");
            ZrLogConfig config = (ZrLogConfig) JFinal.me().getServletContext().getAttribute("config");
            //通知启动插件，配置库连接等操作
            config.installFinish();
        }
    }

    /**
     * 加载安装向导第一个页面数据
     */
    public void index() {
        render("/install/index.jsp");
    }
}
