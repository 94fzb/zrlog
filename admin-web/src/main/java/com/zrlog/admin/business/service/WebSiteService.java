package com.zrlog.admin.business.service;

import com.zrlog.admin.business.rest.base.*;
import com.zrlog.common.Constants;
import com.zrlog.data.dto.FaviconBase64DTO;
import com.zrlog.model.WebSite;
import com.zrlog.util.ZrLogUtil;

import java.util.Objects;

public class WebSiteService {


    public UpgradeWebSiteInfo upgradeWebSiteInfo() {
        UpgradeWebSiteInfo upgrade = new UpgradeWebSiteInfo();
        upgrade.setUpgradePreview(Constants.getBooleanByFromWebSite("upgradePreview"));
        if (Objects.nonNull(Constants.zrLogConfig.getPublicWebSite().get("autoUpgradeVersion"))) {
            upgrade.setAutoUpgradeVersion((long) Double.parseDouble((String) Constants.zrLogConfig.getPublicWebSite().get("autoUpgradeVersion")));
        }
        return upgrade;

    }

    public BlogWebSiteInfo blogWebSiteInfo() {
        BlogWebSiteInfo blog = new BlogWebSiteInfo();
        blog.setGenerator_html_status(Constants.getBooleanByFromWebSite("generator_html_status"));
        blog.setHost(Objects.requireNonNullElse((String) Constants.zrLogConfig.getPublicWebSite().get("host"), ""));
        blog.setDisable_comment_status(Constants.getBooleanByFromWebSite("disable_comment_status"));
        blog.setArticle_thumbnail_status(Constants.getBooleanByFromWebSite("article_thumbnail_status"));
        return blog;
    }

    public BasicWebSiteInfo basicWebSiteInfo() {
        FaviconBase64DTO faviconBase64DTO = new WebSite().faviconBase64DTO();
        BasicWebSiteInfo basic = new BasicWebSiteInfo();
        basic.setTitle((String) Constants.zrLogConfig.getPublicWebSite().get("title"));
        basic.setSecond_title((String) Constants.zrLogConfig.getPublicWebSite().get("second_title"));
        basic.setDescription((String) Constants.zrLogConfig.getPublicWebSite().get("description"));
        basic.setKeywords((String) Constants.zrLogConfig.getPublicWebSite().get("keywords"));
        basic.setFavicon_ico_base64(faviconBase64DTO.getFavicon_ico_base64());
        return basic;
    }

    public AdminWebSiteInfo adminWebSiteInfo() {
        AdminWebSiteInfo admin = new AdminWebSiteInfo();
        FaviconBase64DTO faviconBase64DTO = new WebSite().faviconBase64DTO();
        admin.setAdmin_darkMode(Constants.getBooleanByFromWebSite("admin_darkMode"));
        admin.setLanguage((String) Constants.zrLogConfig.getPublicWebSite().get("language"));
        admin.setAdmin_static_resource_base_url(ZrLogUtil.getAdminStaticResourceBaseUrlByWebSite());
        admin.setAdmin_color_primary(Objects.toString(Constants.zrLogConfig.getPublicWebSite().get("admin_color_primary"), "#1677ff"));
        admin.setSession_timeout(Constants.getSessionTimeout() / 60 / 1000);
        admin.setArticle_auto_digest_length(Constants.getAutoDigestLength());
        admin.setFavicon_png_pwa_512_base64(faviconBase64DTO.getFavicon_png_pwa_512_base64());
        admin.setFavicon_png_pwa_192_base64(faviconBase64DTO.getFavicon_png_pwa_192_base64());
        admin.setAdmin_article_page_size(Constants.getAdminArticlePageSize());
        return admin;
    }

    public OtherWebSiteInfo other() {
        OtherWebSiteInfo other = new OtherWebSiteInfo();
        other.setIcp((String) Constants.zrLogConfig.getPublicWebSite().get("icp"));
        other.setWebCm((String) Constants.zrLogConfig.getPublicWebSite().get("webCm"));
        other.setRobotRuleContent((String) Constants.zrLogConfig.getPublicWebSite().get("robotRuleContent"));
        return other;
    }
}
