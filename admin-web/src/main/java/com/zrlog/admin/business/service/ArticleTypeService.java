package com.zrlog.admin.business.service;

import com.hibegin.common.util.BeanUtil;
import com.zrlog.admin.business.rest.response.ArticleTypeResponseEntry;
import com.zrlog.common.rest.request.PageRequest;
import com.zrlog.data.dto.PageData;
import com.zrlog.model.Type;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Map;

public class ArticleTypeService {


    public PageData<ArticleTypeResponseEntry> find(String homeUrl, PageRequest page, boolean staticHtml) throws SQLException {
        PageData<Map<String, Object>> mapPageData = new Type().find(page);
        return new PageData<>(mapPageData.getTotalElements(), mapPageData.getRows().stream().map(e -> {
            ArticleTypeResponseEntry response = BeanUtil.convert(e, ArticleTypeResponseEntry.class);
            response.setAmount(response.getTypeamount());
            response.setUrl(homeUrl + "sort/" + URLEncoder.encode(response.getAlias(), StandardCharsets.UTF_8) + (staticHtml ? ".html" : ""));
            return response;
        }).toList());
    }
}
