package com.zrlog.business.util;

import com.hibegin.common.util.LoggerUtil;
import com.hibegin.common.util.StringUtils;
import com.hibegin.http.server.util.PathUtil;
import com.hibegin.http.server.util.ServerInfo;
import com.zrlog.business.service.InstallService;
import com.zrlog.util.ZrLogUtil;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class InstallUtils {

    public static final String INSTALL_ROUTER_PATH = "/install";

    public static final Logger LOGGER = LoggerUtil.getLogger(InstallUtils.class);

    /**
     * 通过检查conf目录下面是否有 install.lock 文件来判断是否已经安装过了，这里为静态工具方法，方便其他类调用。
     */
    public static boolean isInstalled() {
        return new InstallService(PathUtil.getConfPath()).checkInstall() || StringUtils.isNotEmpty(ZrLogUtil.getDbInfoByEnv());
    }

    public static File getLockFile() {
        return new InstallService(PathUtil.getConfPath()).getLockFile();
    }


    public static Properties getSystemProp(Connection connection) {
        Properties systemProp = System.getProperties();
        systemProp.setProperty("zrlog.runtime.path", PathUtil.getRootPath());
        systemProp.setProperty("server.info", "SimpleWebServer/" + ServerInfo.getVersion());
        if (StringUtils.isNotEmpty(systemProp.getProperty("os.name"))) {
            if (systemProp.get("os.name").toString().startsWith("Mac")) {
                systemProp.put("os.type", "apple");
            } else {
                systemProp.put("os.type", systemProp.getProperty("os.name").toLowerCase());
            }
            systemProp.put("docker", ZrLogUtil.isDockerMode() ? "docker" : "");
        }
        if (InstallUtils.isInstalled()) {
            systemProp.put("dbServer.version", getDatabaseServerVersion(connection));

        }
        return systemProp;
    }

    private static String getDatabaseServerVersion(Connection connection) {
        try {
            if (connection != null) {
                String queryVersionSQL = "select version()";
                try (PreparedStatement ps = connection.prepareStatement(queryVersionSQL)) {
                    try (ResultSet resultSet = ps.executeQuery()) {
                        if (resultSet.next()) {
                            return resultSet.getString(1);
                        }
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "DB connect error ", e);
        }
        return "Unknown";
    }
}
