package com.zrlog.admin.business.service;

import com.hibegin.common.util.BeanUtil;
import com.hibegin.common.util.UrlEncodeUtils;
import com.zrlog.admin.business.rest.response.ArticleTypeResponseEntry;
import com.zrlog.common.rest.request.PageRequest;
import com.zrlog.data.dto.PageData;
import com.zrlog.model.Type;

import java.sql.SQLException;
import java.util.Map;
import java.util.stream.Collectors;

public class ArticleTypeService {


    public PageData<ArticleTypeResponseEntry> find(String homeUrl, PageRequest page, boolean staticHtml) throws SQLException {
        PageData<Map<String, Object>> mapPageData = new Type().find(page);
        return new PageData<>(mapPageData.getTotalElements(), mapPageData.getRows().stream().map(e -> {
            ArticleTypeResponseEntry response = BeanUtil.convert(e, ArticleTypeResponseEntry.class);
            response.setAmount(response.getTypeamount());
            response.setUrl(homeUrl + UrlEncodeUtils.encodeUrl("sort/" + response.getAlias()) + (staticHtml ? ".html" : ""));
            return response;
        }).collect(Collectors.toList()), mapPageData.getPage(), mapPageData.getSize());
    }
}
