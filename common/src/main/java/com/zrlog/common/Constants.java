package com.zrlog.common;


import com.hibegin.common.util.BooleanUtils;
import com.hibegin.common.util.StringUtils;
import com.zrlog.common.type.AutoUpgradeVersionType;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * 存放全局的静态变量，有多个地方使用一个key时，存放在这里，方便代码的维护。
 */
public class Constants {
    public static final String CACHE_KEY = "initDataV2";
    public static final String ZRLOG_SQL_VERSION_KEY = "zrlogSqlVersion";
    public static final String I18N = "i18n";
    public static final String TEMPLATE_BASE_PATH = "/include/templates/";
    public static final String DEFAULT_TEMPLATE_PATH = TEMPLATE_BASE_PATH + "default";
    public static final String ADMIN_INDEX = "/admin/index#dashboard";
    public static final String ERROR_PAGE = "/error/500.html";
    public static final String FORBIDDEN_PAGE = "/error/403.html";
    public static final String NOT_FOUND_PAGE = "/error/404.html";
    public static final String ZRLOG_RESOURCE_DOWNLOAD_URL = "http://dl.zrlog.com";
    public static final String AUTO_UPGRADE_VERSION_KEY = "autoUpgradeVersion";
    public static final int DEFAULT_ARTICLE_DIGEST_LENGTH = 200;
    public static final String ADMIN_TOKEN = "admin-token";
    /**
     * 字符长度必须要大于16个字符
     */
    public static final String AES_PUBLIC_KEY = "_BLOG_BLOG_BLOG_";
    /**
     * 1天
     */
    private static final long DEFAULT_SESSION_TIMEOUT = 1000 * 60 * 60 * 24L;
    private static final String SESSION_TIMEOUT_KEY = "session_timeout";
    public static final String ATTACHED_FOLDER = "/attached/";
    public static String TEMPLATE_CONFIG_SUFFIX = "_setting";
    public static final AutoUpgradeVersionType DEFAULT_AUTO_UPGRADE_VERSION_TYPE = AutoUpgradeVersionType.ONE_DAY;
    public static String DATE_FORMAT_PATTERN = "yyyy-MM-dd HH:mm:ssXXX";
    public static final Map<String, Object> WEB_SITE = Collections.synchronizedMap(new HashMap<>());
    public static boolean IN_JAR = new File("webapp").exists();
    public static String FTL_VIEW_PATH = "/view";
    public static final String ARTICLE_ROUTER_KEY = "article_route";
    public static String DEFAULT_HEADER = "assets/images/default-portrait.gif";

    public static String pluginServer;

    public static boolean isStaticHtmlStatus() {
        return getBooleanByFromWebSite("generator_html_status");
    }

    public static int getMaxCacheHtmlSize() {
        Object dbSettingSize = WEB_SITE.get("cache_html_size");
        if (dbSettingSize != null) {
            try {
                return Integer.valueOf(dbSettingSize.toString());
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
                return Integer.valueOf(dbSettingSize.toString()) * 60 * 1000;
            } catch (Exception e) {
                //ignore
            }
        }
        return 5 * 60 * 1000;
    }

    public static String getArticleUri() {
        String router = getArticleRoute();
        if (StringUtils.isNotEmpty(router)) {
            return router + "/";
        }
        return "";
    }

    public static String getArticleRoute() {
        if (WEB_SITE.containsKey(ARTICLE_ROUTER_KEY)) {
            if (WEB_SITE.get(ARTICLE_ROUTER_KEY) == null) {
                return "";
            }
            return (String) WEB_SITE.get("article_route");
        }
        return "post";
    }

    public static Long getSessionTimeout() {
        String sessionTimeoutString = (String) Constants.WEB_SITE.get(Constants.SESSION_TIMEOUT_KEY);
        long sessionTimeout;
        if (!StringUtils.isEmpty(sessionTimeoutString)) {
            //*60， Cookie过期时间单位为分钟
            sessionTimeout = Long.valueOf(sessionTimeoutString) * 60 * 1000;
            if (sessionTimeout <= 0) {
                sessionTimeout = Constants.DEFAULT_SESSION_TIMEOUT;
            }
        } else {
            sessionTimeout = Constants.DEFAULT_SESSION_TIMEOUT;
        }
        return sessionTimeout;
    }

    public static int getAutoDigestLength() {
        Object dbSettingSize = WEB_SITE.get("article_auto_digest_length");
        if (dbSettingSize != null) {
            try {
                return Integer.valueOf(dbSettingSize.toString());
            } catch (Exception e) {
                //ignore
            }
        }
        return 100;
    }

    public static boolean getBooleanByFromWebSite(String key) {
        Object dbSettingSize = WEB_SITE.get(key);
        if (dbSettingSize != null) {
            return dbSettingSize instanceof String && ("1".equals(dbSettingSize) || BooleanUtils.isTrue((String) dbSettingSize));
        }
        return false;
    }
}