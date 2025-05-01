package com.zrlog.admin.util;

import com.hibegin.common.dao.DataSourceWrapperImpl;
import com.hibegin.common.util.LoggerUtil;
import com.hibegin.common.util.StringUtils;
import com.hibegin.http.server.api.HttpRequest;
import com.hibegin.http.server.util.PathUtil;
import com.zrlog.common.Constants;
import com.zrlog.util.BlogBuildInfoUtil;
import com.zrlog.util.ZrLogUtil;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SystemInfoUtils {

    public static final Logger LOGGER = LoggerUtil.getLogger(SystemInfoUtils.class);


    private static Properties getSystemProp() {
        Properties systemProp = System.getProperties();
        systemProp.setProperty("zrlog.runtime.path", PathUtil.getRootPath());
        systemProp.setProperty("server.info", "SimpleWebServer/" + com.hibegin.http.server.util.ServerInfo.getVersion());
        if (StringUtils.isNotEmpty(systemProp.getProperty("os.name"))) {
            if (systemProp.get("os.name").toString().startsWith("Mac")) {
                systemProp.put("os.type", "apple");
            } else {
                systemProp.put("os.type", systemProp.getProperty("os.name").toLowerCase());
            }
            systemProp.put("docker", ZrLogUtil.isDockerMode() ? "docker" : "");
        }
        if (Constants.zrLogConfig.isInstalled()) {
            systemProp.put("dbServer.version", getDatabaseServerVersion());

        }
        return systemProp;
    }


    private static String getDatabaseServerVersion() {
        DataSource dataSource = Constants.zrLogConfig.getDataSource();
        if (Objects.isNull(dataSource)) {
            return "Unknown";
        }
        try {
            if (dataSource instanceof DataSourceWrapperImpl) {
                return ((DataSourceWrapperImpl) dataSource).getDbInfo();
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "DB connect error ", e);
        }
        return "Unknown";
    }


    public static List<ServerInfo> serverInfo(HttpRequest httpRequest) throws SQLException {
        Map<String, Object> info = new HashMap<>();
        getSystemProp().forEach((key, value) -> info.put(key.toString(), value));
        BlogBuildInfoUtil.getBlogProp().forEach((key, value) -> info.put("zrlog." + key.toString(), value));
        String applicationName = httpRequest.getServerConfig().getApplicationName();
        if (applicationName.startsWith("zrlog")) {
            info.put("server.info", com.hibegin.http.server.util.ServerInfo.getName() + "/" + com.hibegin.http.server.util.ServerInfo.getVersion());
        } else {
            info.put("server.info", applicationName + "/" + httpRequest.getServerConfig().getApplicationVersion());
        }
        return ServerInfoUtils.convertToServerInfos(info);
    }

    public static List<ServerInfo> systemIOInfoVO() {
        return ServerInfoUtils.getServerInfos2();
    }
}
