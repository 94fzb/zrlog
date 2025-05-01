package com.zrlog.blog.web.controller.page;

public class ArticleUriInfoVO {

    private final String key;
    private final long page;

    public ArticleUriInfoVO(String key, long page) {
        this.key = key;
        this.page = page;
    }

    public long getPage() {
        return page;
    }

    public String getKey() {
        return key;
    }
}
