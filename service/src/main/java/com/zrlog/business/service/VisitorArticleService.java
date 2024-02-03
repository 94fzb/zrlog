package com.zrlog.business.service;

import com.hibegin.common.util.StringUtils;
import com.zrlog.common.rest.request.PageRequest;
import com.zrlog.data.dto.PageData;
import com.zrlog.model.Log;
import com.zrlog.util.ParseUtil;
import org.jsoup.Jsoup;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * 与文章相关的业务代码
 */
public class VisitorArticleService {

    /**
     * 高亮用户检索的关键字
     */
    public static void wrapperSearchKeyword(PageData<Map<String, Object>> data, String keywords) {
        if (StringUtils.isNotEmpty(keywords)) {
            List<Map<String, Object>> logs = data.getRows();
            if (logs != null && !logs.isEmpty()) {
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
        }

    }

    public static String getPlainSearchText(String content) {
        if (StringUtils.isEmpty(content)) {
            return "";
        }
        return Jsoup.parse(content).body().text();
    }

    public PageData<Map<String, Object>> pageByKeywords(PageRequest pageRequest, String keywords) throws SQLException {
        PageData<Map<String, Object>> data = new Log().visitorFind(pageRequest, keywords);
        wrapperSearchKeyword(data, keywords);
        return data;
    }
}
