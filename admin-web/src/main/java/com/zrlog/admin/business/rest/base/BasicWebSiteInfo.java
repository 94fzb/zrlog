package com.zrlog.admin.business.rest.base;

import com.zrlog.admin.business.exception.ArgsException;
import com.zrlog.common.Validator;

import java.util.Objects;

public class BasicWebSiteInfo implements Validator {

    private String second_title;
    private String title;
    private String keywords;
    private String description;

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
}
