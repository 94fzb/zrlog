package com.zrlog.admin.business.rest.response;

import com.zrlog.business.rest.response.ArticleResponseEntry;
import com.zrlog.data.dto.PageData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ArticlePageData extends PageData<ArticleResponseEntry> {

    private List<Map<String, Object>> types = new ArrayList<>();

    public List<Map<String, Object>> getTypes() {
        return types;
    }

    public void setTypes(List<Map<String, Object>> types) {
        this.types = types;
    }
}
