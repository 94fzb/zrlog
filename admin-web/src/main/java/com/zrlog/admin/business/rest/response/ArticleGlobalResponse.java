package com.zrlog.admin.business.rest.response;

import java.util.List;
import java.util.Map;

public class ArticleGlobalResponse {

    private List<Map<String, Object>> tags;
    private List<Map<String, Object>> types;
    private LoadEditArticleResponse article;

    public LoadEditArticleResponse getArticle() {
        return article;
    }

    public void setArticle(LoadEditArticleResponse article) {
        this.article = article;
    }

    public List<Map<String, Object>> getTags() {
        return tags;
    }

    public void setTags(List<Map<String, Object>> tags) {
        this.tags = tags;
    }

    public List<Map<String, Object>> getTypes() {
        return types;
    }

    public void setTypes(List<Map<String, Object>> types) {
        this.types = types;
    }
}
