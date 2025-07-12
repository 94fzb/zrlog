package com.zrlog.admin.business.service;

import com.hibegin.http.server.api.HttpRequest;
import com.zrlog.admin.business.AdminConstants;
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
        if (Objects.nonNull(Constants.getStringByFromWebSite("autoUpgradeVersion"))) {
            upgrade.setAutoUpgradeVersion((long) Double.parseDouble((String) Constants.getStringByFromWebSite("autoUpgradeVersion")));
        }
        return upgrade;

    }

    public BlogWebSiteInfo blogWebSiteInfo() {
        BlogWebSiteInfo blog = new BlogWebSiteInfo();
        blog.setGenerator_html_status(Constants.getBooleanByFromWebSite("generator_html_status"));
        blog.setHost(Constants.getStringByFromWebSite("host", ""));
        blog.setDisable_comment_status(!Constants.isAllowComment());
        blog.setArticle_thumbnail_status(Constants.getBooleanByFromWebSite("article_thumbnail_status"));
        return blog;
    }

    public BasicWebSiteInfo basicWebSiteInfo() {
        FaviconBase64DTO faviconBase64DTO = new WebSite().faviconBase64DTO();
        BasicWebSiteInfo basic = new BasicWebSiteInfo();
        basic.setTitle(Constants.getStringByFromWebSite("title"));
        basic.setSecond_title(Constants.getStringByFromWebSite("second_title"));
        basic.setDescription(Constants.getStringByFromWebSite("description"));
        basic.setKeywords(Constants.getStringByFromWebSite("keywords"));
        basic.setFavicon_ico_base64(faviconBase64DTO.getFavicon_ico_base64());
        return basic;
    }

    public AdminWebSiteInfo adminWebSiteInfo(HttpRequest request) {
        AdminWebSiteInfo admin = new AdminWebSiteInfo();
        FaviconBase64DTO faviconBase64DTO = new WebSite().faviconBase64DTO();
        admin.setAdmin_darkMode(Constants.getBooleanByFromWebSite("admin_darkMode"));
        admin.setLanguage((String) Constants.getStringByFromWebSite("language"));
        admin.setAdmin_static_resource_base_url(ZrLogUtil.getAdminStaticResourceBaseUrlByWebSite(request));
        admin.setAdmin_color_primary(Objects.toString(Constants.getStringByFromWebSite("admin_color_primary"), "#1677ff"));
        admin.setSession_timeout(Constants.getSessionTimeout() / 60 / 1000);
        admin.setArticle_auto_digest_length(AdminConstants.getAutoDigestLength());
        admin.setFavicon_png_pwa_512_base64(faviconBase64DTO.getFavicon_png_pwa_512_base64());
        admin.setFavicon_png_pwa_192_base64(faviconBase64DTO.getFavicon_png_pwa_192_base64());
        admin.setAdmin_article_page_size(AdminConstants.getAdminArticlePageSize());
        return admin;
    }

    public OtherWebSiteInfo other() {
        OtherWebSiteInfo other = new OtherWebSiteInfo();
        other.setIcp(Constants.getStringByFromWebSite("icp"));
        other.setWebCm(Constants.getStringByFromWebSite("webCm"));
        other.setRobotRuleContent(Constants.getStringByFromWebSite("robotRuleContent"));
        return other;
    }
}
