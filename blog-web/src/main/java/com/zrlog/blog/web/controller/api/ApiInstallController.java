package com.zrlog.blog.web.controller.api;

import com.jfinal.core.Controller;
import com.jfinal.core.JFinal;
import com.jfinal.kit.PathKit;
import com.zrlog.business.exception.InstallException;
import com.zrlog.business.service.InstallAction;
import com.zrlog.business.service.InstallService;
import com.zrlog.business.type.TestConnectDbResult;
import com.zrlog.common.rest.response.StandardResponse;

import java.util.HashMap;
import java.util.Map;

/**
 * 与安装向导相关的路由进行控制
 * 注意 install.lock 文件相当重要，如果不是重新安装请不要删除这个自动生成的文件
 */
public class ApiInstallController extends Controller {

    public static String JDBC_URL_BASE_QUERY_PARAM = "characterEncoding=UTF-8&allowPublicKeyRetrieval=true&useSSL=false&serverTimezone=GMT";

    /**
     * 检查数据库是否可以正常连接使用，无法连接时给出相应的提示
     */
    public StandardResponse testDbConn() {
        TestConnectDbResult testConnectDbResult = new InstallService(PathKit.getWebRootPath() + "/WEB-INF", getDbConn()).testDbConn();
        if (testConnectDbResult.getError() != 0) {
            throw new InstallException(testConnectDbResult);
        }
        return new StandardResponse();
    }

    private Map<String, String> getDbConn() {
        Map<String, String> dbConn = new HashMap<>();
        dbConn.put("jdbcUrl", "jdbc:mysql://" + getPara("dbHost") + ":" + getPara("dbPort") + "/" + getPara("dbName")
                + "?" + JDBC_URL_BASE_QUERY_PARAM);
        dbConn.put("user", getPara("dbUserName"));
        dbConn.put("password", getPara("dbPassword"));
        dbConn.put("driverClass", "com.mysql.cj.jdbc.Driver");
        return dbConn;
    }

    /**
     * 数据库检查通过后，根据填写信息，执行数据表，表数据的初始化
     */
    public StandardResponse startInstall() {
        Map<String, String> configMsg = new HashMap<>();
        configMsg.put("title", getPara("title"));
        configMsg.put("second_title", getPara("second_title"));
        configMsg.put("username", getPara("username"));
        configMsg.put("password", getPara("password"));
        configMsg.put("email", getPara("email"));
        if (!new InstallService(PathKit.getWebRootPath() + "/WEB-INF", getDbConn(), configMsg).install()) {
            throw new InstallException(TestConnectDbResult.UNKNOWN);
        }
        //通知启动插件，配置库连接等操作
        ((InstallAction) JFinal.me().getServletContext().getAttribute("config")).installFinish();
        return new StandardResponse();
    }
}
