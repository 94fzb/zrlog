package com.zrlog.common;

import com.hibegin.common.util.BooleanUtils;
import com.hibegin.common.util.EnvKit;
import com.hibegin.common.util.StringUtils;
import com.zrlog.common.type.AutoUpgradeVersionType;
import com.zrlog.common.type.RunMode;
import com.zrlog.util.I18nUtil;

import java.util.*;

/**
 * 存放全局的静态变量，有多个地方使用一个key时，存放在这里，方便代码的维护。
 */
public class Constants {

    public static final String ZRLOG_SQL_VERSION_KEY = "zrlogSqlVersion";
    public static final String INSTALL_HTML_PAGE = "/install/index.html";
    public static final String ADMIN_URI_BASE_PATH = "/admin";
    public static final String ADMIN_HTML_PAGE = ADMIN_URI_BASE_PATH + "/index.html";
    public static final String TEMPLATE_BASE_PATH = "/include/templates/";
    public static final String DEFAULT_TEMPLATE_PATH = TEMPLATE_BASE_PATH + "default";
    public static final String AUTO_UPGRADE_VERSION_KEY = "autoUpgradeVersion";
    public static final int DEFAULT_ARTICLE_DIGEST_LENGTH = 200;
    public static final String FAVICON_ICO_URI_PATH = "/favicon.ico";
    public static final String FAVICON_PNG_PWA_192_URI_PATH = "/pwa/icon/favicon-192.png";
    public static final String FAVICON_PNG_PWA_512_URI_PATH = "/pwa/icon/favicon-512.png";
    public static final String ADMIN_LOGIN_URI_PATH = ADMIN_URI_BASE_PATH + "/login";
    public static final String ADMIN_PWA_MANIFEST_JSON = ADMIN_URI_BASE_PATH + "/manifest.json";
    public static final String ADMIN_SERVICE_WORKER_JS = "/admin/service-worker.js";
    public static final String ADMIN_PWA_MANIFEST_API_URI_PATH = "/api" + ADMIN_URI_BASE_PATH + "/manifest";
    public static final String ADMIN_REFRESH_CACHE_API_URI_PATH = "/api" + ADMIN_URI_BASE_PATH + "/refreshCache";
    public static final String INDEX_URI_PATH = "/index";
    public static final String ATTACHED_FOLDER = "/attached/";
    public static final AutoUpgradeVersionType DEFAULT_AUTO_UPGRADE_VERSION_TYPE = AutoUpgradeVersionType.ONE_DAY;
    public static final String DATE_FORMAT_PATTERN = "yyyy-MM-dd HH:mm:ssXXX";
    public static final Integer SQL_VERSION = 19;
    public static final String ADMIN_TITLE_CHAR = " - ";
    /**
     * 1天
     */
    private static final long DEFAULT_SESSION_TIMEOUT = 1000 * 60 * 60 * 24L;
    private static final String SESSION_TIMEOUT_KEY = "session_timeout";
    public static RunMode runMode = RunMode.JAVA;
    public static ZrLogConfig zrLogConfig;
    public static String pluginServer;
    private static volatile long lastAccessTime = System.currentTimeMillis();

    static {
        init();
    }

    public static long getLastAccessTime() {
        return lastAccessTime;
    }

    public static void setLastAccessTime(long lastAccessTime) {
        Constants.lastAccessTime = lastAccessTime;
    }

    public static boolean debugLoggerPrintAble() {
        return EnvKit.isDevMode() || EnvKit.isDebugMode();
    }

    public static String getRealFileArch() {
        String os = System.getProperty("os.name");
        if (os.startsWith("Windows")) {
            return "Windows-" + System.getProperty("os.arch");
        } else if (os.startsWith("Mac")) {
            return os.replace("Mac OS X", "Darwin") + "-" + System.getProperty("os.arch").replace("aarch64", "arm64");
        }
        return os + "-" + System.getProperty("os.arch").replace("aarch64", "arm64");
    }

    public static String getZrLogHomeByEnv() {
        return System.getenv().get("ZRLOG_HOME");
    }

    public static String getZrLogHome() {
        if (Constants.getZrLogHomeByEnv() == null) {
            return System.getProperty("user.dir");
        } else {
            return Constants.getZrLogHomeByEnv();
        }
    }

    public static boolean isStaticHtmlStatus() {
        return getBooleanByFromWebSite("generator_html_status");
    }

    public static int getInitDataMaxCacheTimeout() {
        Object dbSettingSize = zrLogConfig.getPublicWebSite().get("cache_timeout_minutes");
        if (dbSettingSize != null) {
            try {
                return (int) (Double.parseDouble(dbSettingSize.toString()) * 60 * 1000);
            } catch (Exception e) {
                //ignore
            }
        }
        return 5 * 60 * 1000;
    }

    public static String getArticleUri() {
        return "";
    }

    public static int getAutoDigestLength() {
        Object dbSettingSize = zrLogConfig.getPublicWebSite().get("article_auto_digest_length");
        if (dbSettingSize != null) {
            try {
                return (int) Double.parseDouble(dbSettingSize.toString());
            } catch (Exception e) {
                //ignore
            }
        }
        return DEFAULT_ARTICLE_DIGEST_LENGTH;
    }

    public static boolean getBooleanByFromWebSite(String key) {
        Object dbSetting = zrLogConfig.getPublicWebSite().get(key);
        return websiteValueIsTrue(dbSetting);
    }

    public static boolean websiteValueIsTrue(Object dbSetting) {
        if (Objects.isNull(dbSetting)) {
            return false;
        }
        if (dbSetting instanceof Boolean) {
            return (boolean) dbSetting;
        }
        return dbSetting instanceof String && ("1".equals(dbSetting) || "on".equals(dbSetting) || BooleanUtils.isTrue((String) dbSetting));
    }

    public static Long getSessionTimeout() {
        String sessionTimeoutString = (String) Constants.zrLogConfig.getPublicWebSite().get(SESSION_TIMEOUT_KEY);
        if (StringUtils.isEmpty(sessionTimeoutString)) {
            return DEFAULT_SESSION_TIMEOUT;
        }
        //*60， Cookie过期时间单位为分钟
        long sessionTimeout = (long) (Double.parseDouble(sessionTimeoutString) * 60 * 1000);
        if (sessionTimeout <= 0) {
            return DEFAULT_SESSION_TIMEOUT;
        }
        return sessionTimeout;
    }

    public static List<String> articleRouterList() {
        return Collections.singletonList("/");
    }

    public static boolean isAllowComment() {
        return !Constants.getBooleanByFromWebSite("disable_comment_status");
    }

    public static long getDefaultRows() {
        return (long) Double.parseDouble((String) Objects.requireNonNullElse(Constants.zrLogConfig.getPublicWebSite().get("rows"), "10"));
    }

    public static int getAdminArticlePageSize() {
        return (int) Double.parseDouble((String) Objects.requireNonNullElse(Constants.zrLogConfig.getPublicWebSite().get("admin_article_page_size"), "10"));
    }

    public static String getAdminTitle(String startTitle) {
        String title = (String) Constants.zrLogConfig.getPublicWebSite().get("title");
        StringJoiner sj = new StringJoiner(ADMIN_TITLE_CHAR);
        if (StringUtils.isNotEmpty(startTitle) && !startTitle.trim().isEmpty()) {
            sj.add(startTitle);
        }
        if (StringUtils.isNotEmpty(title)) {
            sj.add(title);
        }
        sj.add(I18nUtil.getAdminStringFromRes("admin.management"));
        return sj.toString();
    }

    public static Map<String, String> titleMap = new HashMap<>();

    static {
        titleMap.put(Constants.ADMIN_LOGIN_URI_PATH, "login");
        titleMap.put("/admin/article-edit", "admin.log.edit");
        titleMap.put("/admin/article", "blogManage");
    }

    public static String getAdminDocumentTitleByUri(String uri) {
        String realUri = uri.replaceFirst("/api", "");
        String key = titleMap.get(realUri);
        if (Objects.isNull(key)) {
            return getAdminTitle("");
        }
        return getAdminTitle(I18nUtil.getAdminStringFromRes(key));
    }

    public static String getAppId() {
        return String.valueOf(zrLogConfig.getPublicWebSite().get("appId"));
    }

    public static void init() {
        System.getProperties().put("java.util.logging.SimpleFormatter.format", "%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS %4$s %5$s%6$s%n");
    }
}