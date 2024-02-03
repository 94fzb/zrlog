package com.zrlog.blog.web.controller.api;

import com.hibegin.common.util.StringUtils;
import com.hibegin.http.annotation.ResponseBody;
import com.hibegin.http.server.util.PathUtil;
import com.hibegin.http.server.web.Controller;
import com.zrlog.business.exception.*;
import com.zrlog.business.service.InstallService;
import com.zrlog.business.type.TestConnectDbResult;
import com.zrlog.common.Constants;
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
    @ResponseBody
    public StandardResponse testDbConn() {
        TestConnectDbResult testConnectDbResult = new InstallService(PathUtil.getConfPath(), getDbConn()).testDbConn();
        if (testConnectDbResult.getError() != 0) {
            throw new InstallException(testConnectDbResult);
        }
        return new StandardResponse();
    }

    private Map<String, String> getDbConn() {
        if (StringUtils.isEmpty(getRequest().getParaToStr("dbHost"))) {
            throw new MissingDbHostException();
        }
        if (StringUtils.isEmpty(getRequest().getParaToStr("dbPort"))) {
            throw new MissingDbPortException();
        }
        if (StringUtils.isEmpty(getRequest().getParaToStr("dbUserName"))) {
            throw new MissingDbUserNameException();
        }
        if (StringUtils.isEmpty(getRequest().getParaToStr("dbName"))) {
            throw new MissingDbNameException();
        }
        Map<String, String> dbConn = new HashMap<>();
        dbConn.put("jdbcUrl", "jdbc:mysql://" + getRequest().getParaToStr("dbHost") + ":" + getRequest().getParaToStr("dbPort") + "/" + getRequest().getParaToStr("dbName")
                + "?" + JDBC_URL_BASE_QUERY_PARAM);
        dbConn.put("user", getRequest().getParaToStr("dbUserName"));
        dbConn.put("password", getRequest().getParaToStr("dbPassword"));
        dbConn.put("driverClass", "com.mysql.cj.jdbc.Driver");

        return dbConn;
    }

    /**
     * 数据库检查通过后，根据填写信息，执行数据表，表数据的初始化
     */
    @ResponseBody
    public StandardResponse startInstall() {
        Map<String, String> configMsg = new HashMap<>();
        configMsg.put("title", getRequest().getParaToStr("title"));
        configMsg.put("second_title", getRequest().getParaToStr("second_title"));
        configMsg.put("username", getRequest().getParaToStr("username"));
        configMsg.put("password", getRequest().getParaToStr("password"));
        configMsg.put("email", getRequest().getParaToStr("email"));
        if (!new InstallService(PathUtil.getConfPath(), getDbConn(), configMsg).install()) {
            throw new InstallException(TestConnectDbResult.UNKNOWN);
        }
        //通知启动插件，配置库连接等操作
        Constants.installAction.installFinish();
        return new StandardResponse();
    }
}
