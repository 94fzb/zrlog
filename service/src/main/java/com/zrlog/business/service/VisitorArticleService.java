package com.zrlog.business.service;

import com.hibegin.common.util.BeanUtil;
import com.hibegin.common.util.StringUtils;
import com.hibegin.http.server.api.HttpRequest;
import com.zrlog.business.rest.response.ArticleResponseEntry;
import com.zrlog.common.Constants;
import com.zrlog.common.rest.request.PageRequest;
import com.zrlog.data.dto.PageData;
import com.zrlog.model.Log;
import com.zrlog.util.ParseUtil;
import com.zrlog.util.ZrLogUtil;
import org.jsoup.Jsoup;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 与文章相关的业务代码
 */
public class VisitorArticleService {

    /**
     * 高亮用户检索的关键字
     */
    public static void wrapperSearchKeyword(PageData<Map<String, Object>> data, String keywords) {
        if (StringUtils.isEmpty(keywords)) {
            return;
        }
        List<Map<String, Object>> logs = data.getRows();
        if (logs == null || logs.isEmpty()) {
            return;
        }
        for (Map<String, Object> log : logs) {
            String title = (String) log.get("title");
            String content = (String) log.get("content");
            String digest = (String) log.get("digest");
            log.put("title", ParseUtil.wrapperKeyword(title, keywords));
            String tryWrapperDigest = ParseUtil.wrapperKeyword(digest, keywords);
            if (tryWrapperDigest != null && tryWrapperDigest.length() != digest.length()) {
                log.put("digest", tryWrapperDigest);
            } else {
                log.put("digest", ParseUtil.wrapperKeyword(ParseUtil.removeHtmlElement(content), keywords));
            }
        }

    }

    public static String getAccessUrl(ArticleResponseEntry articleResponseEntry, HttpRequest request) {
        if (articleResponseEntry.isPrivacy() || articleResponseEntry.isRubbish()) {
            return "/article-edit?previewMode=true&id=" + articleResponseEntry.getId();
        }
        String key = articleResponseEntry.getId() + "";
        if (StringUtils.isNotEmpty(articleResponseEntry.getAlias())) {
            key = articleResponseEntry.getAlias();
        }
        return ZrLogUtil.getHomeUrlWithHost(request) + Constants.getArticleUri() + key + TemplateHelper.getSuffix(request);
    }

    /**
     * 将输入的分页过后的对象，转化PageableResponse的对象
     */
    public static PageData<ArticleResponseEntry> convertPageable(PageData<Map<String, Object>> object, HttpRequest request) {
        List<ArticleResponseEntry> dataList = new ArrayList<>();
        for (Map<String, Object> obj : object.getRows()) {
            if (Objects.nonNull(obj.get("releaseTime"))) {
                obj.put("releaseTime", ((LocalDateTime) obj.get("releaseTime")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            }
            if (Objects.nonNull(obj.get("logId"))) {
                obj.put("id", obj.get("logId"));
            }
            if (Objects.nonNull(obj.get("lastUpdateDate"))) {
                obj.put("lastUpdateDate", ((LocalDateTime) obj.get("lastUpdateDate")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            }
            obj.remove("last_update_date");
            ArticleResponseEntry convert = BeanUtil.convert(obj, ArticleResponseEntry.class);
            convert.setUrl(getAccessUrl(convert, request));
            dataList.add(convert);
        }
        PageData<ArticleResponseEntry> pageData = new PageData<>();
        pageData.setTotalElements(object.getTotalElements());
        pageData.setRows(dataList);
        pageData.setPage(object.getPage());
        pageData.setSize(object.getSize());
        pageData.setSort(object.getSort());
        return pageData;
    }

    public static String getPlainSearchText(String content) {
        if (StringUtils.isEmpty(content)) {
            return "";
        }
        return Jsoup.parse(content).body().text();
    }

    public PageData<Map<String, Object>> pageByKeywords(PageRequest pageRequest, String keywords) {
        PageData<Map<String, Object>> data = new Log().visitorFind(pageRequest, keywords);
        wrapperSearchKeyword(data, keywords);
        return data;
    }
}
