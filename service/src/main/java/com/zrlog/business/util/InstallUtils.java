package com.zrlog.business.util;

import com.hibegin.common.util.StringUtils;
import com.hibegin.http.server.util.PathUtil;
import com.hibegin.http.server.util.ServerInfo;
import com.zrlog.business.service.InstallService;
import com.zrlog.common.Constants;
import com.zrlog.util.ZrLogUtil;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Objects;
import java.util.Properties;

public class InstallUtils {

    public static final String INSTALL_ROUTER_PATH = "/install";

    /**
     * 通过检查conf目录下面是否有 install.lock 文件来判断是否已经安装过了，这里为静态工具方法，方便其他类调用。
     */
    public static boolean isInstalled() {
        return new InstallService(PathUtil.getConfPath()).checkInstall() || StringUtils.isNotEmpty(ZrLogUtil.getDbInfoByEnv());
    }

    public static Properties getDbProp() {
        if (InstallUtils.isInstalled()) {
            Properties dbProperties = new Properties();
            try (FileInputStream in = new FileInputStream(Constants.getDbPropertiesFile())) {
                dbProperties.load(in);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return dbProperties;
        }
        return null;
    }

    public static Properties getSystemProp() {
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
            Properties dbProperties = Objects.requireNonNull(getDbProp());
            systemProp.put("dbServer.version", ZrLogUtil.getDatabaseServerVersion(dbProperties));

        }
        return systemProp;
    }

}
