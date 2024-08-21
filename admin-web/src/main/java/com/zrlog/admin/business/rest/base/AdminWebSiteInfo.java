package com.zrlog.admin.business.rest.base;

import com.hibegin.common.util.StringUtils;
import com.zrlog.common.Constants;
import com.zrlog.common.Validator;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

import java.util.Objects;

public class AdminWebSiteInfo implements Validator {

    private Long session_timeout;
    private String language;
    private String admin_color_primary;
    private Integer article_auto_digest_length;
    private Boolean admin_darkMode;
    private Integer admin_article_page_size;
    private String favicon_png_pwa_192_base64;
    private String favicon_png_pwa_512_base64;

    public Long getSession_timeout() {
        return session_timeout;
    }

    public void setSession_timeout(Long session_timeout) {
        this.session_timeout = session_timeout;
    }


    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getAdmin_color_primary() {
        return admin_color_primary;
    }

    public void setAdmin_color_primary(String admin_color_primary) {
        this.admin_color_primary = admin_color_primary;
    }

    public Integer getArticle_auto_digest_length() {
        return article_auto_digest_length;
    }

    public void setArticle_auto_digest_length(Integer article_auto_digest_length) {
        this.article_auto_digest_length = article_auto_digest_length;
    }

    public Boolean getAdmin_darkMode() {
        return admin_darkMode;
    }

    public void setAdmin_darkMode(Boolean admin_darkMode) {
        this.admin_darkMode = admin_darkMode;
    }

    @Override
    public void doValid() {
        if (Objects.isNull(article_auto_digest_length)) {
            article_auto_digest_length = Constants.DEFAULT_ARTICLE_DIGEST_LENGTH;
        }
    }

    @Override
    public void doClean() {
        if (StringUtils.isNotEmpty(favicon_png_pwa_512_base64)) {
            this.favicon_png_pwa_512_base64 = Jsoup.clean(favicon_png_pwa_512_base64, Safelist.none());
        }
        if (StringUtils.isNotEmpty(favicon_png_pwa_192_base64)) {
            this.favicon_png_pwa_192_base64 = Jsoup.clean(favicon_png_pwa_192_base64, Safelist.none());
        }
        if (StringUtils.isNotEmpty(language)) {
            this.language = Jsoup.clean(language, Safelist.none());
        }
        if (StringUtils.isNotEmpty(admin_color_primary)) {
            this.admin_color_primary = Jsoup.clean(admin_color_primary, Safelist.none());
        }
    }

    public String getFavicon_png_pwa_192_base64() {
        return favicon_png_pwa_192_base64;
    }

    public void setFavicon_png_pwa_192_base64(String favicon_png_pwa_192_base64) {
        this.favicon_png_pwa_192_base64 = favicon_png_pwa_192_base64;
    }

    public String getFavicon_png_pwa_512_base64() {
        return favicon_png_pwa_512_base64;
    }

    public void setFavicon_png_pwa_512_base64(String favicon_png_pwa_512_base64) {
        this.favicon_png_pwa_512_base64 = favicon_png_pwa_512_base64;
    }

    public Integer getAdmin_article_page_size() {
        return admin_article_page_size;
    }

    public void setAdmin_article_page_size(Integer admin_article_page_size) {
        this.admin_article_page_size = admin_article_page_size;
    }
}
