package com.zrlog.admin.business.rest.response;

public class ArticleActivityData {

    private final String date;
    private final Long count;

    public ArticleActivityData(String date, Long count) {
        this.date = date;
        this.count = count;
    }

    public String getDate() {
        return date;
    }



    public Long getCount() {
        return count;
    }
}
