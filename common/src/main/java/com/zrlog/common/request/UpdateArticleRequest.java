package com.zrlog.common.request;

public class UpdateArticleRequest extends CreateArticleRequest {

    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
