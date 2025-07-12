package com.zrlog.admin.business;

import com.hibegin.common.util.StringUtils;
import com.zrlog.common.Constants;
import com.zrlog.util.I18nUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;

import static com.zrlog.common.Constants.DEFAULT_ARTICLE_DIGEST_LENGTH;

public class AdminConstants {
    public static final String ADMIN_URI_BASE_PATH = "/admin";
    public static final String ADMIN_HTML_PAGE = ADMIN_URI_BASE_PATH + "/index.html";
    public static final String ADMIN_LOGIN_URI_PATH = ADMIN_URI_BASE_PATH + "/login";
    public static final String ADMIN_PWA_MANIFEST_API_URI_PATH = "/api" + ADMIN_URI_BASE_PATH + "/manifest";
    public static final String ADMIN_REFRESH_CACHE_API_URI_PATH = "/api" + ADMIN_URI_BASE_PATH + "/refreshCache";
    public static final String ADMIN_TITLE_CHAR = " - ";
    public static final String AUTO_UPGRADE_VERSION_KEY = "autoUpgradeVersion";
    public static final Map<String, String> TITLE_MAP = new HashMap<>();
    public static final String INDEX_URI_PATH = "/index";


    static {
        TITLE_MAP.put(ADMIN_LOGIN_URI_PATH, "login");
        TITLE_MAP.put(ADMIN_URI_BASE_PATH + "/article-edit", "admin.log.edit");
        TITLE_MAP.put(ADMIN_URI_BASE_PATH + "/article", "blogManage");
    }


    public static String getAdminDocumentTitleByUri(String uri) {
        String realUri = uri.replaceFirst("/api", "");
        String key = TITLE_MAP.get(realUri);
        if (Objects.isNull(key)) {
            return getAdminTitle("");
        }
        return getAdminTitle(I18nUtil.getAdminStringFromRes(key));
    }

    public static int getAdminArticlePageSize() {
        return (int) Double.parseDouble(Constants.getStringByFromWebSite("admin_article_page_size", "10"));
    }

    public static String getAdminTitle(String startTitle) {
        String title = Constants.getStringByFromWebSite("title","");
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

    public static int getAutoDigestLength() {
        Object dbSettingSize = Constants.getStringByFromWebSite("article_auto_digest_length");
        if (dbSettingSize != null) {
            try {
                return (int) Double.parseDouble(dbSettingSize.toString());
            } catch (Exception e) {
                //ignore
            }
        }
        return DEFAULT_ARTICLE_DIGEST_LENGTH;
    }
}
