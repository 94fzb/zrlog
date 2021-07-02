package com.zrlog.util;

import com.google.gson.Gson;
import com.hibegin.common.util.BeanUtil;
import com.hibegin.common.util.IOUtil;
import com.hibegin.common.util.StringUtils;
import com.hibegin.common.util.VersionComparator;
import com.zrlog.common.Constants;
import eu.bitwalker.useragentutils.BrowserType;
import eu.bitwalker.useragentutils.UserAgent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.*;
import java.util.Date;
import java.util.*;

/**
 * Zrlog特有的一些工具方法
 */
public class ZrLogUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZrLogUtil.class);

    private ZrLogUtil() {
    }

    public static <T> T convertRequestBody(ServletRequest request, Class<T> clazz) {
        try {
            String jsonStr = IOUtil.getStringInputStream(request.getInputStream());
            return new Gson().fromJson(jsonStr, clazz);
        } catch (Exception e) {
            LOGGER.info("", e);
            throw new RuntimeException(e);
        }
    }


    public static <T> T convertRequestParam(Map<String, String[]> requestParam, Class<T> clazz) {
        Map<String, Object> tempMap = new HashMap<>();
        for (Map.Entry<String, String[]> entry : requestParam.entrySet()) {
            if (entry.getValue() != null && entry.getValue().length > 0) {
                if (entry.getValue().length > 1) {
                    tempMap.put(entry.getKey(), Arrays.asList(entry.getValue()));
                } else {
                    tempMap.put(entry.getKey(), entry.getValue()[0]);
                }
            }
        }
        return BeanUtil.convert(tempMap, clazz);
    }

    public static boolean isStaticBlogPlugin(HttpServletRequest httpServletRequest) {
        return httpServletRequest.getHeader("User-Agent") != null && httpServletRequest.getHeader("User-Agent").startsWith("Static-Blog-Plugin");
    }

    public static String getFullUrl(HttpServletRequest request) {
        return "//" + request.getHeader("Host") + request.getRequestURI();
    }

    public static String getDatabaseServerVersion(String jdbcUrl, String userName, String password, String deriveClass) {
        Connection connect = null;
        try {
            connect = getConnection(jdbcUrl, userName, password, deriveClass);
            if (connect != null) {
                String queryVersionSQL = "select version()";
                try (PreparedStatement ps = connect.prepareStatement(queryVersionSQL)) {
                    try (ResultSet resultSet = ps.executeQuery()) {
                        if (resultSet.next()) {
                            return resultSet.getString(1);
                        }
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("Not can same deriveClass " + deriveClass, e);
        } finally {
            if (connect != null) {
                try {
                    connect.close();
                } catch (SQLException e) {
                    LOGGER.error("", e);
                }
            }
        }
        return "Unknown";
    }

    public static String getCurrentSqlVersion(String jdbcUrl, String userName, String password, String deriveClass) {
        Connection connect = null;
        try {
            connect = getConnection(jdbcUrl, userName, password, deriveClass);
            if (connect != null) {
                String queryVersionSQL = "select value from website where name = ?";
                try (PreparedStatement ps = connect.prepareStatement(queryVersionSQL)) {
                    ps.setString(1, Constants.ZRLOG_SQL_VERSION_KEY);
                    try (ResultSet resultSet = ps.executeQuery()) {
                        if (resultSet.next()) {
                            return resultSet.getString(1);
                        }
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("Not can same deriveClass " + deriveClass, e);
        } finally {
            if (connect != null) {
                try {
                    connect.close();
                } catch (SQLException e) {
                    LOGGER.error("", e);
                }
            }
        }
        return "-1";
    }

    public static Connection getConnection(String jdbcUrl, String user, String password, String driverClass) {
        try {
            Class.forName(driverClass);
            return DriverManager.getConnection(jdbcUrl, user, password);
        } catch (ClassNotFoundException | SQLException e) {
            LOGGER.error("", e);
        }
        return null;
    }

    public static Integer getSqlVersion(String basePath) {
        List<File> sqlFileList = getSqlFileList(basePath);
        if (!sqlFileList.isEmpty()) {
            return Integer.valueOf(sqlFileList.get(sqlFileList.size() - 1).getName().replace(".sql", ""));
        }
        return -1;
    }

    private static List<File> getSqlFileList(String basePath) {
        File file = new File(basePath);
        List<File> fileList = new ArrayList<>();
        if (file.exists() && file.isDirectory()) {
            File[] fs = file.listFiles();
            if (fs != null && fs.length > 0) {
                fileList = Arrays.asList(fs);
                fileList.sort(Comparator.comparingInt(e -> Integer.parseInt(e.getName().replace(".sql", ""))));
            }
        }
        return fileList;
    }

    public static List<Map.Entry<Integer, List<String>>> getExecSqlList(String sqlVersion, String basePath) {
        List<Map.Entry<Integer, List<String>>> sqlList = new ArrayList<>();
        int version = 0;
        try {
            version = Integer.parseInt(sqlVersion);
        } catch (Exception e) {
            LOGGER.error("", e);
        }
        for (File f : getSqlFileList(basePath)) {
            try {
                int fileVersion = Integer.parseInt(f.getName().replace(".sql", ""));
                if (fileVersion > version) {
                    LOGGER.info("need update sql " + f);
                    Map.Entry<Integer, List<String>> entry = new AbstractMap.SimpleEntry<>(fileVersion, Arrays.asList(IOUtil.getStringInputStream(new FileInputStream(f)).split("\n")));
                    sqlList.add(entry);
                }
            } catch (FileNotFoundException e) {
                LOGGER.error("", e);
            }
        }
        return sqlList;
    }


    public static boolean greatThenCurrentVersion(String buildId, Date releaseDate, String fetchedVersion) {
        if (buildId.equals(BlogBuildInfoUtil.getBuildId()) || releaseDate.before(BlogBuildInfoUtil.getTime())) {
            return false;
        }
        int result = new VersionComparator().compare(fetchedVersion, BlogBuildInfoUtil.getVersion());
        if (result == 0) {
            return releaseDate.after(BlogBuildInfoUtil.getTime());
        } else {
            return result > 0;
        }
    }

    public static boolean isBae() {
        String value = System.getenv("SERVER_SOFTWARE");
        return value != null && value.startsWith("bae");
    }

    public static boolean isPreviewMode() {
        String value = System.getenv("PREVIEW_MODE");
        return "true".equalsIgnoreCase(value);
    }

    public static boolean isDockerMode() {
        String value = System.getenv("DOCKER_MODE");
        return "true".equalsIgnoreCase(value);
    }

    public static String getDbInfoByEnv() {
        return System.getenv("DB_PROPERTIES");
    }

    public static boolean isInternalHostName(String name) {
        InetAddress address;
        try {
            address = InetAddress.getByName(name);
            return address.isSiteLocalAddress() || address.isLoopbackAddress();
        } catch (UnknownHostException e) {
            //ignore 可能没有网络. 认为不是内网地址
            return false;
        }
    }

    public static String getViewExt(String type) {
        if ("freemarker".equals(type)) {
            return ".ftl";
        } else {
            return ".jsp";
        }
    }

    public static boolean isNormalBrowser(String userAgent) {
        if (StringUtils.isEmpty(userAgent)) {
            return false;
        }
        UserAgent ua = UserAgent.parseUserAgentString(userAgent);
        BrowserType browserType = ua.getBrowser().getBrowserType();
        return browserType == BrowserType.MOBILE_BROWSER || browserType == BrowserType.WEB_BROWSER;
    }
}
