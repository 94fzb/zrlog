package com.zrlog.util;

import com.google.gson.Gson;
import com.hibegin.common.util.*;
import com.hibegin.common.util.IOUtil;
import com.hibegin.common.util.LoggerUtil;
import com.hibegin.http.server.api.HttpRequest;
import com.hibegin.http.server.api.HttpResponse;
import com.hibegin.http.server.util.PathUtil;
import com.hibegin.http.server.web.Controller;
import com.zrlog.common.Constants;
import eu.bitwalker.useragentutils.BrowserType;
import eu.bitwalker.useragentutils.UserAgent;

import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.*;
import java.util.Date;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * ZrLog特有的一些工具方法
 */
public class ZrLogUtil {

    private static final Logger LOGGER = LoggerUtil.getLogger(ZrLogUtil.class);

    private ZrLogUtil() {
    }

    public static <T> T convertRequestBody(HttpRequest request, Class<T> clazz) {
        try {
            String jsonStr = IOUtil.getStringInputStream(request.getInputStream());
            return new Gson().fromJson(jsonStr, clazz);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "", e);
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

    public static boolean isStaticBlogPlugin(HttpRequest HttpRequest) {
        return HttpRequest.getHeader("User-Agent") != null && HttpRequest.getHeader("User-Agent").startsWith("Static-Blog-Plugin");
    }

    public static String getFullUrl(HttpRequest request) {
        return "//" + request.getHeader("Host") + request.getUri();
    }

    public static String getDatabaseServerVersion(Properties dbConfig) {
        try (Connection  connect = DbConnectUtils.getConnection(dbConfig)){
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

    public static String getCurrentSqlVersion(Properties dbConfig) {
        try (Connection connection = DbConnectUtils.getConnection(dbConfig)){
            if (connection != null) {
                String queryVersionSQL = "select value from website where name = ?";
                try (PreparedStatement ps = connection.prepareStatement(queryVersionSQL)) {
                    ps.setString(1, Constants.ZRLOG_SQL_VERSION_KEY);
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
        return "-1";
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

    public static List<Map.Entry<Integer, List<String>>> getExecSqlList(String sqlVersion) {
        List<Map.Entry<Integer, List<String>>> sqlList = new ArrayList<>();
        int version = 0;
        try {
            version = Integer.parseInt(sqlVersion);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "", e);
        }
        for (Map.Entry<Integer, String> f : getSqlFileList().entrySet()) {
            int fileVersion = f.getKey();
            if (fileVersion > version) {
                LOGGER.info("need update sql " + f);
                Map.Entry<Integer, List<String>> entry = new AbstractMap.SimpleEntry<>(fileVersion, Arrays.asList(f.getValue().split("\n")));
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

    public static Controller buildController(Method method, HttpRequest request, HttpResponse response) throws InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchMethodException {
        Constructor[] constructors = method.getDeclaringClass().getConstructors();
        Controller controller = null;
        for (Constructor constructor : constructors) {
            if (constructor.getParameterTypes().length == 2) {
                if (constructor.getParameterTypes()[0].getName().equals(HttpRequest.class.getName()) && constructor.getParameterTypes()[1].getName().equals(HttpResponse.class.getName())) {
                    controller = (Controller) constructor.newInstance(request, response);
                }
            }
        }
        if (controller == null) {
            throw new RuntimeException(method.getDeclaringClass().getSimpleName() + " not find 2 args " + "constructor");
        }
        return controller;
    }
}
