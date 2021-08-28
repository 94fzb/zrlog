package com.zrlog.business.service;

import com.hibegin.common.util.StringUtils;
import com.zrlog.common.rest.request.PageRequest;
import com.zrlog.data.dto.PageData;
import com.zrlog.model.Log;
import com.zrlog.util.ParseUtil;
import org.jsoup.Jsoup;

import java.util.List;

/**
 * 与文章相关的业务代码
 */
public class VisitorArticleService {

    /**
     * 高亮用户检索的关键字
     */
    public static void wrapperSearchKeyword(PageData<Log> data, String keywords) {
        if (StringUtils.isNotEmpty(keywords)) {
            List<Log> logs = data.getRows();
            if (logs != null && !logs.isEmpty()) {
                for (Log log : logs) {
                    String title = log.get("title");
                    String content = log.get("content");
                    String digest = log.get("digest");
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

    public PageData<Log> pageByKeywords(PageRequest pageRequest, String keywords) {
        PageData<Log> data = new Log().visitorFind(pageRequest, keywords);
        wrapperSearchKeyword(data, keywords);
        return data;
    }
}
