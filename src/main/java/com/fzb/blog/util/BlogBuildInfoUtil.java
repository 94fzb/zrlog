package com.fzb.blog.util;


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

    /*目前以git的commitId的前7位标记构建的Id*/
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
                    time = new SimpleDateFormat("yyyy-MM-dd hh:mm").parse(properties.get("buildTime").toString());
                }
                if (properties.get("runMode") != null && !"".equals(properties.get("runMode"))) {
                    runMode = properties.get("runMode").toString();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
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
            version = "1.x-dev";
        }
        if (time == null) {
            time = new Date();
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
        System.out.println("isRelease = " + isRelease());
        System.out.println("version = " + version);
        System.out.println("buildId = " + buildId);
        System.out.println("time = " + time);
    }
}
