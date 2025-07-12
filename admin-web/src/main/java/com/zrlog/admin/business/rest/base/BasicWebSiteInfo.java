package com.zrlog.admin.business.rest.base;

import com.hibegin.common.util.StringUtils;
import com.zrlog.common.Validator;
import com.zrlog.common.exception.ArgsException;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

import java.util.Objects;

public class BasicWebSiteInfo implements Validator {

    private String second_title;
    private String title;
    private String keywords;
    private String description;
    private String favicon_ico_base64;

    public String getSecond_title() {
        return second_title;
    }

    public void setSecond_title(String second_title) {
        this.second_title = second_title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public void doValid() {
        if (Objects.isNull(title) || title.trim().isEmpty()) {
            throw new ArgsException("title");
        }
    }

    @Override
    public void doClean() {
        if (StringUtils.isNotEmpty(second_title)) {
            this.second_title = Jsoup.clean(second_title, Safelist.none());
        }
        if (StringUtils.isNotEmpty(keywords)) {
            this.keywords = Jsoup.clean(keywords, Safelist.none());
        }
        if (StringUtils.isNotEmpty(title)) {
            this.title = Jsoup.clean(title, Safelist.none());
        }
        if (StringUtils.isNotEmpty(description)) {
            this.description = Jsoup.clean(description, Safelist.none());
        }
        if (StringUtils.isNotEmpty(favicon_ico_base64)) {
            this.favicon_ico_base64 = Jsoup.clean(favicon_ico_base64, Safelist.none());
        }
    }

    public String getFavicon_ico_base64() {
        return favicon_ico_base64;
    }

    public void setFavicon_ico_base64(String favicon_ico_base64) {
        this.favicon_ico_base64 = favicon_ico_base64;
    }
}
