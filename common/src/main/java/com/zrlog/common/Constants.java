package com.zrlog.common;

import com.hibegin.common.util.BooleanUtils;
import com.hibegin.common.util.StringUtils;
import com.hibegin.http.server.util.PathUtil;
import com.zrlog.common.type.AutoUpgradeVersionType;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * 存放全局的静态变量，有多个地方使用一个key时，存放在这里，方便代码的维护。
 */
public class Constants {

    private static volatile long lastAccessTime = System.currentTimeMillis();

    public static long getLastAccessTime() {
        return lastAccessTime;
    }

    public static boolean DEV_MODE = Objects.equals(System.getenv("DEV_MODE"), "true");

    public static void setLastAccessTime(long lastAccessTime) {
        Constants.lastAccessTime = lastAccessTime;
    }

    public static ZrLogConfig zrLogConfig;

    public static final String ZRLOG_SQL_VERSION_KEY = "zrlogSqlVersion";

    public static final String INSTALL_HTML_PAGE = "/install/index.html";

    public static final String ADMIN_URI_BASE_PATH = "/admin";

    public static final String ADMIN_HTML_PAGE = ADMIN_URI_BASE_PATH + "/index.html";

    public static final String TEMPLATE_BASE_PATH = "/include/templates/";

    public static final String DEFAULT_TEMPLATE_PATH = TEMPLATE_BASE_PATH + "default";

    public static final String AUTO_UPGRADE_VERSION_KEY = "autoUpgradeVersion";

    public static final int DEFAULT_ARTICLE_DIGEST_LENGTH = 200;


    public static final String ADMIN_LOGIN_URI_PATH = ADMIN_URI_BASE_PATH + "/login";
    public static final String ADMIN_REFRESH_CACHE_API_URI_PATH = "/api" + ADMIN_URI_BASE_PATH + "/refreshCache";

    public static final String INDEX_URI_PATH = "/index";

    public static final String ATTACHED_FOLDER = "/attached/";


    public static final AutoUpgradeVersionType DEFAULT_AUTO_UPGRADE_VERSION_TYPE = AutoUpgradeVersionType.ONE_DAY;

    public static final String DATE_FORMAT_PATTERN = "yyyy-MM-dd HH:mm:ssXXX";

    public static final Map<String, Object> WEB_SITE = new ConcurrentSkipListMap<>();

    public static final Integer SQL_VERSION = 17;

    /**
     * 1天
     */
    private static final long DEFAULT_SESSION_TIMEOUT = 1000 * 60 * 60 * 24L;

    private static final String SESSION_TIMEOUT_KEY = "session_timeout";


    public static String pluginServer;

    public static boolean isStaticHtmlStatus() {
        return getBooleanByFromWebSite("generator_html_status");
    }

    public static int getMaxCacheHtmlSize() {
        Object dbSettingSize = WEB_SITE.get("cache_html_size");
        if (dbSettingSize != null) {
            try {
                return (int) Double.parseDouble(dbSettingSize.toString());
            } catch (Exception e) {
                //ignore
            }
        }
        return 20 * 1024 * 1024;
    }

    public static int getInitDataMaxCacheTimeout() {
        Object dbSettingSize = WEB_SITE.get("cache_timeout_minutes");
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
        Object dbSettingSize = WEB_SITE.get("article_auto_digest_length");
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
        Object dbSetting = WEB_SITE.get(key);
        if (Objects.isNull(dbSetting)) {
            return false;
        }
        if (dbSetting instanceof Boolean) {
            return (boolean) dbSetting;
        }
        return dbSetting instanceof String && ("1".equals(dbSetting) || "on".equals(dbSetting) || BooleanUtils.isTrue((String) dbSetting));
    }

    public static Long getSessionTimeout() {
        String sessionTimeoutString = (String) Constants.WEB_SITE.get(SESSION_TIMEOUT_KEY);
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
        return (long) Double.parseDouble((String) Objects.requireNonNullElse(Constants.WEB_SITE.get("rows"), "10"));
    }


    public static File getDbPropertiesFile() {
        File file = new File(PathUtil.getConfPath() + "/db.properties");
        new File(PathUtil.getConfPath()).mkdirs();
        return file;
    }
}