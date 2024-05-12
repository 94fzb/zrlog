package com.zrlog.admin.business.rest.base;

import com.zrlog.common.Constants;
import com.zrlog.common.Validator;

import java.util.Objects;

public class AdminWebSiteInfo implements Validator {

    private Long session_timeout;
    private String language;
    private String admin_color_primary;
    private Integer article_auto_digest_length;
    private Boolean admin_darkMode;

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
}
