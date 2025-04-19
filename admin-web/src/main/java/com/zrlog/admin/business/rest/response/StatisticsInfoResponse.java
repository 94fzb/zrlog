package com.zrlog.admin.business.rest.response;

public class StatisticsInfoResponse {

    private Long commCount;
    private Long toDayCommCount;
    private Long clickCount;
    private Long articleCount;

    public Long getCommCount() {
        return commCount;
    }

    public void setCommCount(Long commCount) {
        this.commCount = commCount;
    }

    public Long getToDayCommCount() {
        return toDayCommCount;
    }

    public void setToDayCommCount(Long toDayCommCount) {
        this.toDayCommCount = toDayCommCount;
    }

    public Long getClickCount() {
        return clickCount;
    }

    public void setClickCount(Long clickCount) {
        this.clickCount = clickCount;
    }

    public Long getArticleCount() {
        return articleCount;
    }

    public void setArticleCount(Long articleCount) {
        this.articleCount = articleCount;
    }
}
