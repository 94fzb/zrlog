package com.fzb.blog.service;

import com.fzb.blog.common.Constants;
import com.fzb.blog.common.request.PageableRequest;
import com.fzb.blog.common.response.ArticleResponseEntry;
import com.fzb.blog.common.response.CreateOrUpdateLogResponse;
import com.fzb.blog.common.response.PageableResponse;
import com.fzb.blog.model.Log;
import com.fzb.blog.model.Tag;
import com.fzb.blog.util.BeanUtil;
import com.fzb.blog.util.ParseUtil;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 与文章相关的业务代码
 */
public class ArticleService {

    public CreateOrUpdateLogResponse createOrUpdate(Integer userId, Map<String, String[]> createArticleRequestMap) {
        String[] logId = createArticleRequestMap.get("logId");
        if (logId == null || StringUtils.isEmpty(logId[0])) {
            return add(userId, createArticleRequestMap);
        } else {
            return update(userId, createArticleRequestMap);
        }
    }

    private CreateOrUpdateLogResponse add(Integer userId, Map<String, String[]> createArticleRequestMap) {
        Log log = getLog(userId, createArticleRequestMap);
        if (BooleanUtils.isFalse(log.getBoolean("rubbish"))) {
            Tag.dao.insertTag(log.getStr("keywords"));
        }
        CreateOrUpdateLogResponse createOrUpdateLogResponse = new CreateOrUpdateLogResponse();
        createOrUpdateLogResponse.setError(log.save() ? 0 : 1);
        createOrUpdateLogResponse.setLogId(log.getInt("logId"));
        createOrUpdateLogResponse.setAlias(log.getStr("alias"));
        return createOrUpdateLogResponse;
    }

    private CreateOrUpdateLogResponse update(Integer userId, Map<String, String[]> createArticleRequestMap) {
        Log log = getLog(userId, createArticleRequestMap);
        String oldTagStr = Log.dao.findById(log.getInt("logId")).get("keywords");
        Tag.dao.update(log.getStr("keywords"), oldTagStr);
        CreateOrUpdateLogResponse updateLogResponse = new CreateOrUpdateLogResponse();
        updateLogResponse.setLogId(log.getInt("logId"));
        updateLogResponse.setError(log.update() ? 0 : 1);
        updateLogResponse.setAlias(log.getStr("alias"));
        return updateLogResponse;
    }

    private Log getLog(Integer userId, Map<String, String[]> createArticleRequestMap) {
        Log log = new Log();
        for (Map.Entry<String, String[]> map : createArticleRequestMap.entrySet()) {
            if (map.getValue().length > 0) {
                log.set(map.getKey(), map.getValue()[0]);
            }
        }
        Object logId = log.get("logId");
        if (StringUtils.isEmpty(logId + "")) {
            logId = Log.dao.getMaxRecord() + 1;
            log.set("logId", logId);
            log.set("releaseTime", new Date());
        } else {
            log.set("logId", Integer.valueOf(logId.toString()));
        }
        if (log.get("alias") == null || "".equals((log.get("alias") + "").trim())) {
            log.set("alias", logId + "");
        } else {
            log.set("alias", (log.get("alias") + "").trim().replace(" ", "-"));
        }
        log.set("userId", userId);
        log.set("canComment", createArticleRequestMap.get("canComment") != null);
        log.set("recommended", createArticleRequestMap.get("recommended") != null);
        log.set("private", createArticleRequestMap.get("private") != null);
        log.set("rubbish", createArticleRequestMap.get("rubbish") != null);
        // 自动摘要
        if (log.get("digest") == null || "".equals(log.get("digest"))) {
            log.set("digest", ParseUtil.autoDigest(log.get("content").toString(), 200));
        }
        return log;
    }

    public PageableResponse<ArticleResponseEntry> page(PageableRequest pageableRequest, String keywords) {
        return BeanUtil.convertPageable(Log.dao.queryAll(
                pageableRequest.getPage(), pageableRequest.getRows(), keywords, pageableRequest.getOrder(), pageableRequest.getSort()), ArticleResponseEntry.class);
    }

    /**
     * 高亮用户检索的关键字
     *
     * @param logs
     * @param keyword
     */
    public void wrapperSearchKeyword(List<Log> logs, String keyword) {
        for (Log log : logs) {
            String title = log.get("title").toString();
            String content = log.get("content").toString();
            String digest = log.get("digest").toString();
            log.put("title", wrapper(title, keyword));
            String tryWrapperDigest = wrapper(digest, keyword);
            boolean findInDigest = tryWrapperDigest.length() != digest.length();

            if (findInDigest) {
                log.put("digest", tryWrapperDigest);
            } else {
                String wrapperContent = wrapper(ParseUtil.removeHtmlElement(content), keyword);
                log.put("digest", wrapperContent);
            }
        }
    }

    private String wrapper(String content, String keyword) {
        String newContent = content;
        if (content.contains(keyword)) {
            newContent = content.replace(keyword, wrapperFontRed(keyword));
        } else {
            String lowerContent = content.toLowerCase();
            if (lowerContent.contains(keyword.toLowerCase())) {
                String[] strings = lowerContent.split(keyword.toLowerCase());
                StringBuilder sb = new StringBuilder();
                int count = 0;
                if (strings.length > 1) {
                    for (int i = 0; i < strings.length - 1; i++) {
                        count += strings[i].length();
                        String str = wrapperFontRed(content.substring(count, count + keyword.length()));
                        System.out.println(str);
                        System.out.println(sb.toString());
                        sb.append(content.substring(count - strings[i].length(), count));
                        sb.append(str);
                        count += keyword.length();
                    }
                    // 添加最后一段数据
                    sb.append(content.substring(count).replace(keyword.toLowerCase(), wrapperFontRed(keyword)));
                    newContent = sb.toString();
                } else if (strings.length > 0) {
                    sb.append(content.substring(0, strings[0].length()));
                    sb.append(lowerContent.substring(strings[0].length()).replace(keyword.toLowerCase(), wrapperFontRed(content.substring(strings[0].length()))));
                    newContent = sb.toString();
                } else {
                    newContent = wrapperFontRed(content);
                }
            }
        }
        if (newContent.length() != content.length()) {
            int first = newContent.indexOf("<font") - 5;
            if (first > -1 && newContent.length() > Constants.DEFAULT_ARTICLE_DIGEST_LENGTH) {
                String[] fontArr = (newContent.substring(first)).split("</font>");
                newContent = "";
                for (String str : fontArr) {
                    if (!"".equals(newContent) && newContent.length() + str.length() > Constants.DEFAULT_ARTICLE_DIGEST_LENGTH) {
                        if (newContent.length() < 100) {
                            newContent += str.substring(0, 100);
                        }
                        break;
                    }
                    newContent += str + "</font>";
                }
            }
        }
        return newContent;
    }

    private String wrapperFontRed(String content) {
        return "<font color=\"#CC0000\">" + content + "</font>";
    }
}
