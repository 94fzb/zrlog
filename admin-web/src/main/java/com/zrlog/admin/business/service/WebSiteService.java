package com.zrlog.admin.business.service;

import com.zrlog.admin.business.rest.base.*;
import com.zrlog.admin.business.rest.response.WebSiteSettingsResponse;
import com.zrlog.common.Constants;

import java.util.Objects;

public class WebSiteService {

    public WebSiteSettingsResponse loadSettings() {
        WebSiteSettingsResponse webSiteSettingsResponse = new WebSiteSettingsResponse();
        BasicWebSiteInfo basic = new BasicWebSiteInfo();
        basic.setTitle((String) Constants.WEB_SITE.get("title"));
        basic.setSecond_title((String) Constants.WEB_SITE.get("second_title"));
        basic.setDescription((String) Constants.WEB_SITE.get("description"));
        basic.setKeywords((String) Constants.WEB_SITE.get("keywords"));
        webSiteSettingsResponse.setBasic(basic);
        OtherWebSiteInfo other = new OtherWebSiteInfo();
        other.setIcp((String) Constants.WEB_SITE.get("icp"));
        other.setWebCm((String) Constants.WEB_SITE.get("webCm"));
        webSiteSettingsResponse.setOther(other);
        UpgradeWebSiteInfo upgrade = new UpgradeWebSiteInfo();
        upgrade.setUpgradePreview(Constants.getBooleanByFromWebSite("upgradePreview"));
        upgrade.setAutoUpgradeVersion((long) Double.parseDouble((String) Constants.WEB_SITE.get("autoUpgradeVersion")));
        webSiteSettingsResponse.setUpgrade(upgrade);
        BlogWebSiteInfo blog = new BlogWebSiteInfo();
        blog.setGenerator_html_status(Constants.getBooleanByFromWebSite("generator_html_status"));
        blog.setHost(Objects.requireNonNullElse((String) Constants.WEB_SITE.get("host"), ""));
        blog.setDisable_comment_status(Constants.getBooleanByFromWebSite("disable_comment_status"));
        blog.setArticle_thumbnail_status(Constants.getBooleanByFromWebSite("article_thumbnail_status"));
        webSiteSettingsResponse.setBlog(blog);
        AdminWebSiteInfo admin = new AdminWebSiteInfo();
        admin.setAdmin_darkMode(Constants.getBooleanByFromWebSite("admin_darkMode"));
        admin.setLanguage((String) Constants.WEB_SITE.get("language"));
        admin.setAdmin_color_primary(Objects.toString(Constants.WEB_SITE.get("admin_color_primary"), "#1677ff"));
        admin.setSession_timeout(Constants.getSessionTimeout() / 60 / 1000);
        admin.setArticle_auto_digest_length(Constants.getAutoDigestLength());
        webSiteSettingsResponse.setAdmin(admin);
        return webSiteSettingsResponse;
    }
}
