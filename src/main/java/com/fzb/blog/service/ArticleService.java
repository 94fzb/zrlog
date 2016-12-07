package com.fzb.blog.service;

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
import java.util.Map;

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
}
