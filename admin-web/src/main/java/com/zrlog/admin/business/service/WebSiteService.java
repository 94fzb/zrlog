package com.zrlog.admin.business.service;

import com.zrlog.admin.business.rest.base.BasicWebSiteRequest;
import com.zrlog.admin.business.rest.base.BlogWebSiteRequest;
import com.zrlog.admin.business.rest.base.OtherWebSiteRequest;
import com.zrlog.admin.business.rest.base.UpgradeWebSiteRequest;
import com.zrlog.admin.business.rest.response.WebSiteSettingsResponse;
import com.zrlog.common.Constants;

public class WebSiteService {

    public WebSiteSettingsResponse loadSettings() {
        WebSiteSettingsResponse webSiteSettingsResponse = new WebSiteSettingsResponse();
        BasicWebSiteRequest basic = new BasicWebSiteRequest();
        basic.setTitle((String) Constants.WEB_SITE.get("title"));
        basic.setSecond_title((String) Constants.WEB_SITE.get("second_title"));
        basic.setDescription((String) Constants.WEB_SITE.get("description"));
        basic.setKeywords((String) Constants.WEB_SITE.get("keywords"));
        webSiteSettingsResponse.setBasic(basic);
        OtherWebSiteRequest other = new OtherWebSiteRequest();
        other.setIcp((String) Constants.WEB_SITE.get("icp"));
        other.setWebCm((String) Constants.WEB_SITE.get("webCm"));
        webSiteSettingsResponse.setOther(other);
        UpgradeWebSiteRequest upgrade = new UpgradeWebSiteRequest();
        upgrade.setUpgradePreview(Constants.getBooleanByFromWebSite("upgradePreview"));
        upgrade.setAutoUpgradeVersion(Long.parseLong((String) Constants.WEB_SITE.get("autoUpgradeVersion")));
        webSiteSettingsResponse.setUpgrade(upgrade);
        BlogWebSiteRequest blog = new BlogWebSiteRequest();
        blog.setArticle_route((String) Constants.WEB_SITE.get("article_route"));
        blog.setAdmin_darkMode(Constants.getBooleanByFromWebSite("admin_darkMode"));
        blog.setLanguage((String) Constants.WEB_SITE.get("language"));
        blog.setGenerator_html_status(Constants.getBooleanByFromWebSite("generator_html_status"));
        blog.setDisable_comment_status(Constants.getBooleanByFromWebSite("disable_comment_status"));
        blog.setSession_timeout(Constants.getSessionTimeout() / 60 / 1000);
        blog.setArticle_thumbnail_status(Constants.getBooleanByFromWebSite("article_thumbnail_status"));
        webSiteSettingsResponse.setBlog(blog);
        return webSiteSettingsResponse;
    }
}
