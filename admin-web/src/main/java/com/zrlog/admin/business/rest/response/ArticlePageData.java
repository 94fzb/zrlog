package com.zrlog.admin.business.rest.response;

import com.hibegin.common.dao.dto.PageData;
import com.zrlog.business.rest.response.ArticleResponseEntry;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ArticlePageData extends PageData<ArticleResponseEntry> implements Serializable {

    public ArticlePageData() {
    }

    private List<Map<String, Object>> types = new ArrayList<>();

    public List<Map<String, Object>> getTypes() {
        return types;
    }

    public void setTypes(List<Map<String, Object>> types) {
        this.types = types;
    }
}
