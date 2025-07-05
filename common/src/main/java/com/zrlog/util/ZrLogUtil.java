package com.zrlog.util;

import com.hibegin.common.util.*;
import com.hibegin.http.server.api.HttpRequest;
import com.hibegin.http.server.api.HttpResponse;
import com.hibegin.http.server.config.ConfigKit;
import com.zrlog.blog.web.util.WebTools;
import com.zrlog.common.Constants;
import com.zrlog.common.Updater;
import com.zrlog.common.UpdaterTypeEnum;
import eu.bitwalker.useragentutils.BrowserType;
import eu.bitwalker.useragentutils.UserAgent;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

/**
 * ZrLog特有的一些工具方法
 */
public class ZrLogUtil {

    //private static final Logger LOGGER = LoggerUtil.getLogger(ZrLogUtil.class);

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

    public static boolean isStaticPlugin(HttpRequest request) {
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
        return getBlogHost(request) + WebTools.getHomeUrl(request);
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
        String websiteHost = (String) Constants.zrLogConfig.getPublicWebSite().get("host");
        if (Objects.nonNull(websiteHost) && !websiteHost.trim().isEmpty()) {
            return websiteHost;
        }
        return "";
    }

    public static String getAdminStaticResourceBaseUrlByWebSite(HttpRequest request) {
        if (Objects.isNull(Constants.zrLogConfig)) {
            return "";
        }
        String websiteHost = (String) Constants.zrLogConfig.getPublicWebSite().get("admin_static_resource_base_url");
        if (Objects.nonNull(websiteHost) && !websiteHost.trim().isEmpty()) {
            return websiteHost + request.getContextPath();
        }
        return request.getContextPath();
    }

    public static String getFullUrl(HttpRequest request) {
        return "//" + getBlogHost(request) + request.getUri().substring(1);
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
        return ConfigKit.getInt("server.port", 8080);
    }

    public static String getContextPath(String[] args) {
        if (Objects.nonNull(args)) {
            for (String arg : args) {
                if (arg.startsWith("--contextPath=")) {
                    return arg.split("=")[1];
                }
            }
        }
        String contextPath = System.getenv("contextPath");
        if (Objects.nonNull(contextPath)) {
            return contextPath;
        }
        return ConfigKit.get("server.contextPath", "").toString();
    }

    public static List<String> extractExecutableSql(String sql) {
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

    public static boolean isSystemServiceMode() {
        String value = System.getenv("SYSTEM_SERVICE_MODE");
        return "true".equalsIgnoreCase(value);
    }

    public static boolean isWarMode() {
        if (EnvKit.isDevMode()) {
            return false;
        }
        Updater updater = Constants.zrLogConfig.getUpdater();
        if (Objects.isNull(updater)) {
            return false;
        }
        return updater.getType() == UpdaterTypeEnum.WAR;
    }

    public static boolean isWarMode(Updater updater) {
        if (EnvKit.isDevMode()) {
            return false;
        }
        if (Objects.isNull(updater)) {
            return false;
        }
        return updater.getType() == UpdaterTypeEnum.WAR;
    }

    public static void putLongTimeCache(HttpResponse response) {
        response.addHeader("Cache-Control", "max-age=31536000, immutable"); // 1 年的秒数
    }

    public static String getLambdaRoot() {
        return System.getenv("LAMBDA_TASK_ROOT");
    }

}
