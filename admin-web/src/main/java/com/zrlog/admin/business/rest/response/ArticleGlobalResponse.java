package com.zrlog.admin.business.rest.response;

import com.zrlog.model.Tag;
import com.zrlog.model.Type;

import java.util.List;

public class ArticleGlobalResponse {

    private List<Tag> tags;
    private List<Type> types;

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public List<Type> getTypes() {
        return types;
    }

    public void setTypes(List<Type> types) {
        this.types = types;
    }
}
