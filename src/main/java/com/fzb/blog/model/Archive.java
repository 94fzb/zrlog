package com.fzb.blog.model;

/**
 * 文章存档的值对象，不在数据库中有对应的表记录，通过SQL语言查询完成
 */
public class Archive {
    private String url;
    private String text;
    private Long count;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
}
