package com.zrlog.util;


import com.zrlog.common.Constants;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

/**
 * 读取Zrlog构建信息，及build.properties。
 * 注 build.properties 为使用CI工具自动加入的，git代码仓库并没有该文件。
 */
public class BlogBuildInfoUtil {

    private static final Logger LOGGER = Logger.getLogger(BlogBuildInfoUtil.class);

    /**
     * 目前以git的commitId的前7位标记构建的Id
     */
    private static String buildId;
    private static String version;
    private static Date time;
    private static String runMode = "DEV";

    static {
        Properties properties = new Properties();
        InputStream inputStream = BlogBuildInfoUtil.class.getResourceAsStream("/build.properties");
        if (inputStream != null) {
            try {
                properties.load(inputStream);
                if (properties.get("buildId") != null) {
                    buildId = properties.get("buildId").toString();
                }
                if (properties.get("version") != null && !"".equals(properties.get("version"))) {
                    version = properties.get("version").toString();
                }
                if (properties.get("buildTime") != null && !"".equals(properties.get("buildTime"))) {
                    time = new SimpleDateFormat(Constants.DATE_FORMAT_PATTERN).parse(properties.get("buildTime").toString());
                }
                if (properties.get("runMode") != null && !"".equals(properties.get("runMode"))) {
                    runMode = properties.get("runMode").toString();
                }
            } catch (IOException | ParseException e) {
                LOGGER.error("doRead stream error", e);
            } finally {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    LOGGER.error("close stream error", e);
                }
            }
        }
        if (buildId == null) {
            buildId = "0000000";
        }
        if (version == null) {
            version = "1.0.0-SNAPSHOT";
        }
    }

    public static String getBuildId() {
        return buildId;
    }

    public static String getVersion() {
        return version;
    }

    public static Date getTime() {
        //仅存在开发环境为空
        if (time == null) {
            return new Date();
        }
        return time;
    }

    public static String getRunMode() {
        return runMode;
    }

    public static boolean isRelease() {
        return "RELEASE".equalsIgnoreCase(runMode);
    }

    public static boolean isPreview() {
        return "PREVIEW".equalsIgnoreCase(runMode);
    }

    public static boolean isDev() {
        return "DEV".equalsIgnoreCase(runMode);
    }

    public static void main(String[] args) {
        LOGGER.info("isRelease = " + isRelease());
        LOGGER.info("version = " + getVersion());
        LOGGER.info("buildId = " + getBuildId());
        LOGGER.info("time = " + getTime());
    }
}
