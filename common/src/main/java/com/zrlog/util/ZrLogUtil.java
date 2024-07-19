package com.zrlog.util;

import com.hibegin.common.util.*;
import com.hibegin.http.server.api.HttpRequest;
import com.hibegin.http.server.util.PathUtil;
import com.zrlog.common.Constants;
import eu.bitwalker.useragentutils.BrowserType;
import eu.bitwalker.useragentutils.UserAgent;

import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * ZrLog特有的一些工具方法
 */
public class ZrLogUtil {

    private static final Logger LOGGER = LoggerUtil.getLogger(ZrLogUtil.class);

    public static String STATIC_USER_AGENT = "Static-Blog-Plugin/" + UUID.randomUUID().toString().replace("-", "");

    private ZrLogUtil() {
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

    public static boolean isStaticBlogPlugin(HttpRequest request) {
        if (Objects.isNull(request)) {
            return false;
        }
        String ua = request.getHeader("User-Agent");
        if (Objects.isNull(ua)) {
            return false;
        }
        return Objects.equals(ua, STATIC_USER_AGENT);
    }

    public static String getHomeUrlWithHost(HttpRequest request) {
        return "//" + getHomeUrlWithHostNotProtocol(request);
    }

    public static String getHomeUrlWithHostNotProtocol(HttpRequest request) {
        return getBlogHost(request) + "/";
    }

    public static String getBlogHost(HttpRequest request) {
        String websiteHost = getBlogHostByWebSite();
        if (Objects.nonNull(websiteHost) && !websiteHost.trim().isEmpty()) {
            return websiteHost;
        }
        if (Objects.isNull(request)) {
            return "";
        }
        return request.getHeader("Host");
    }

    public static String getBlogHostByWebSite() {
        String websiteHost = (String) Constants.zrLogConfig.getWebSite().get("host");
        if (Objects.nonNull(websiteHost) && !websiteHost.trim().isEmpty()) {
            return websiteHost;
        }
        return "";
    }

    public static String getFullUrl(HttpRequest request) {
        return "//" + getBlogHost(request) + request.getUri().substring(1);
    }

    public static String getDatabaseServerVersion(Properties dbConfig) {
        try (Connection connect = DbConnectUtils.getConnection(dbConfig)) {
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
            LOGGER.log(Level.SEVERE, "DB connect error ", e);
        }
        return "Unknown";
    }

    public static Long getCurrentSqlVersion(Properties dbConfig) {
        try (Connection connection = DbConnectUtils.getConnection(dbConfig)) {
            if (connection != null) {
                String queryVersionSQL = "select value from website where name = ?";
                try (PreparedStatement ps = connection.prepareStatement(queryVersionSQL)) {
                    ps.setString(1, Constants.ZRLOG_SQL_VERSION_KEY);
                    try (ResultSet resultSet = ps.executeQuery()) {
                        if (resultSet.next()) {
                            return Long.parseLong(resultSet.getString(1));
                        }
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "DB connect error ", e);
        }
        return -1L;
    }


    private static Map<Integer, String> getSqlFileList() {
        Map<Integer, String> fileList = new LinkedHashMap<>();
        for (int i = 1; i <= Constants.SQL_VERSION; i++) {
            InputStream sqlStream = PathUtil.getConfInputStream("/update-sql/" + i + ".sql");
            if (Objects.nonNull(sqlStream)) {
                fileList.put(i, IOUtil.getStringInputStream(sqlStream));
            }

        }
        return fileList;
    }

    public static List<Map.Entry<Integer, List<String>>> getExecSqlList(Long dbVersion) {
        List<Map.Entry<Integer, List<String>>> sqlList = new ArrayList<>();

        for (Map.Entry<Integer, String> f : getSqlFileList().entrySet()) {
            int fileVersion = f.getKey();
            if (fileVersion > dbVersion) {
                Map.Entry<Integer, List<String>> entry = new AbstractMap.SimpleEntry<>(fileVersion, extractExecutableSql(f.getValue()));
                LOGGER.info("Need update sql "+ fileVersion+".sql \n" + String.join(";\n",entry.getValue()) + ";");
                sqlList.add(entry);
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

    public static Integer getPort(String[] args) {
        if (Objects.nonNull(args)) {
            for (String arg : args) {
                if (arg.startsWith("--port=")) {
                    return Integer.parseInt(arg.split("=")[1]);
                }
            }
        }
        String webPort = System.getenv("PORT");
        if (Objects.nonNull(webPort)) {
            return Integer.parseInt(webPort);
        }
        return 8080;
    }

    public static List<String> extractExecutableSql(String sql){
        String[] sqlArr = sql.split("\n");
        StringBuilder tempSqlStr = new StringBuilder();
        List<String> sqlList = new ArrayList<>();
        for (String sqlSt : sqlArr) {
            if (sqlSt.startsWith("#") || sqlSt.startsWith("/*")) {
                continue;
            }
            tempSqlStr.append(sqlSt);
        }
        String[] cleanSql = tempSqlStr.toString().split(";");
        for (String sqlSt : cleanSql) {
            if (StringUtils.isEmpty(sqlSt) || sqlSt.trim().isEmpty()) {
                continue;
            }
            sqlList.add(sqlSt);
        }
        return sqlList;
    }

}
