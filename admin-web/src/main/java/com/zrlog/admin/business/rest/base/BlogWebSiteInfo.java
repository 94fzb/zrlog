package com.zrlog.admin.business.rest.base;

import com.hibegin.common.util.StringUtils;
import com.zrlog.admin.business.exception.StaticHtmlConfigException;
import com.zrlog.common.Validator;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

import java.util.Objects;

public class BlogWebSiteInfo implements Validator {

    private Boolean generator_html_status;

    private String host;

    private Boolean disable_comment_status;


    private Boolean article_thumbnail_status;

    public Boolean getArticle_thumbnail_status() {
        return article_thumbnail_status;
    }

    public void setArticle_thumbnail_status(Boolean article_thumbnail_status) {
        this.article_thumbnail_status = article_thumbnail_status;
    }

    public Boolean getGenerator_html_status() {
        return generator_html_status;
    }

    public Boolean getDisable_comment_status() {
        return disable_comment_status;
    }

    public void setDisable_comment_status(Boolean disable_comment_status) {
        this.disable_comment_status = disable_comment_status;
    }

    public void setGenerator_html_status(Boolean generator_html_status) {
        this.generator_html_status = generator_html_status;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    @Override
    public void doValid() {
        if (Objects.equals(generator_html_status, true) && StringUtils.isEmpty(host)) {
            throw new StaticHtmlConfigException();
        }
    }

    @Override
    public void doClean() {
        if (StringUtils.isNotEmpty(host)) {
            this.host = Jsoup.clean(host, Safelist.none());
        }
    }
}
