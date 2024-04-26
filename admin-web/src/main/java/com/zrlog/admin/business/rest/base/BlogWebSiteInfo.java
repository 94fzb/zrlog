package com.zrlog.admin.business.rest.base;

import com.hibegin.common.util.StringUtils;
import com.zrlog.admin.business.exception.StaticHtmlConfigException;
import com.zrlog.common.Constants;
import com.zrlog.common.Validator;

import java.util.Objects;

public class BlogWebSiteInfo implements Validator {

    private Long session_timeout;
    private Boolean generator_html_status;
    private Boolean disable_comment_status;
    private Boolean article_thumbnail_status;
    private String language;
    private String host;
    private String article_route;
    private Boolean admin_darkMode;
    private String admin_color_primary;
    private Integer article_auto_digest_length;

    public Long getSession_timeout() {
        return session_timeout;
    }

    public void setSession_timeout(Long session_timeout) {
        this.session_timeout = session_timeout;
    }

    public Boolean getGenerator_html_status() {
        return generator_html_status;
    }

    public void setGenerator_html_status(Boolean generator_html_status) {
        this.generator_html_status = generator_html_status;
    }

    public Boolean getDisable_comment_status() {
        return disable_comment_status;
    }

    public void setDisable_comment_status(Boolean disable_comment_status) {
        this.disable_comment_status = disable_comment_status;
    }

    public Boolean getArticle_thumbnail_status() {
        return article_thumbnail_status;
    }

    public void setArticle_thumbnail_status(Boolean article_thumbnail_status) {
        this.article_thumbnail_status = article_thumbnail_status;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getArticle_route() {
        return article_route;
    }

    public void setArticle_route(String article_route) {
        this.article_route = article_route;
    }

    public Boolean getAdmin_darkMode() {
        return admin_darkMode;
    }

    public void setAdmin_darkMode(Boolean admin_darkMode) {
        this.admin_darkMode = admin_darkMode;
    }

    public String getAdmin_color_primary() {
        return admin_color_primary;
    }

    public void setAdmin_color_primary(String admin_color_primary) {
        this.admin_color_primary = admin_color_primary;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getArticle_auto_digest_length() {
        return article_auto_digest_length;
    }

    public void setArticle_auto_digest_length(Integer article_auto_digest_length) {
        this.article_auto_digest_length = article_auto_digest_length;
    }

    @Override
    public void doValid() {
        if (Objects.equals(generator_html_status, true) && StringUtils.isEmpty(host)) {
            throw new StaticHtmlConfigException();
        }
        if (Objects.isNull(article_auto_digest_length)) {
            article_auto_digest_length = Constants.DEFAULT_ARTICLE_DIGEST_LENGTH;
        }
    }
}
