package com.zrlog.util;


import com.hibegin.common.util.LoggerUtil;
import com.zrlog.common.Constants;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Objects;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 读取ZrLog构建信息，及build.properties。
 * 注 build.properties 为使用CI工具自动加入的，git代码仓库并没有该文件。
 */
public class BlogBuildInfoUtil {

    private static final Logger LOGGER = LoggerUtil.getLogger(BlogBuildInfoUtil.class);

    /**
     * 目前以git的commitId的前7位标记构建的Id
     */
    private static String buildId = "0000000";
    private static String version = "1.0.0-SNAPSHOT";
    private static Date time = Date.from(LocalDateTime.of(2015, 3, 29, 0, 0, 0)
            .atZone(ZoneOffset.systemDefault()).toInstant());
    private static String runMode = "RELEASE";
    private static String resourceDownloadUrl = "https://dl.zrlog.com/";

    static {
        try (InputStream inputStream = BlogBuildInfoUtil.class.getResourceAsStream("/build.properties")) {
            Properties properties = new Properties();
            if (Objects.nonNull(inputStream)) {
                properties.load(inputStream);
            }
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
            if (properties.get("mirrorWebSite") != null && !"".equals(properties.get("mirrorWebSite"))) {
                resourceDownloadUrl = properties.get("mirrorWebSite").toString();
            }
        } catch (IOException | ParseException e) {
            LOGGER.log(Level.SEVERE, "doRead stream error", e);
        }
        if (Objects.nonNull(resourceDownloadUrl) && resourceDownloadUrl.endsWith("/")) {
            resourceDownloadUrl = resourceDownloadUrl.substring(0, resourceDownloadUrl.length() - 1);
        }
    }

    public static String getBuildId() {
        return buildId;
    }

    public static String getVersion() {
        return version;
    }

    public static Date getTime() {
        return time;
    }

    public static String getRunMode() {
        return runMode;
    }

    public static String getResourceDownloadUrl() {
        return resourceDownloadUrl;
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
        LOGGER.info("resourceDownloadUrl = " + getResourceDownloadUrl());
    }

    public static Properties getBlogProp() {
        Properties blogProperties = new Properties();
        try {
            blogProperties.load(BlogBuildInfoUtil.class.getResourceAsStream("/zrlog.properties"));
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "load blogProperties error", e);
        }
        blogProperties.put("version", BlogBuildInfoUtil.getVersion());
        blogProperties.put("buildId", BlogBuildInfoUtil.getBuildId());
        blogProperties.put("buildTime", new SimpleDateFormat("yyyy-MM-dd").format(BlogBuildInfoUtil.getTime()));
        blogProperties.put("runMode", BlogBuildInfoUtil.getRunMode());
        return blogProperties;
    }

    public static String getVersionShortInfo() {
        return BlogBuildInfoUtil.getVersion() + " - " + BlogBuildInfoUtil.getBuildId();
    }

    public static String getVersionInfo() {
        return getVersionShortInfo() + " (" + new SimpleDateFormat("yyyy-MM-dd").format(BlogBuildInfoUtil.getTime()) + ")";
    }

    public static String getVersionInfoFull() {
        return BlogBuildInfoUtil.getVersion() + " - " + BlogBuildInfoUtil.getBuildId() + " (" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(BlogBuildInfoUtil.getTime()) + ")";
    }
}
