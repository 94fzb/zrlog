package com.zrlog.admin.business.rest.base;

public class BlogWebSiteRequest {

    private Long session_timeout;
    private Boolean generator_html_status;
    private Boolean disable_comment_status;
    private Boolean article_thumbnail_status;
    private String language;
    private String article_route;
    private Boolean admin_darkMode;

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
}
