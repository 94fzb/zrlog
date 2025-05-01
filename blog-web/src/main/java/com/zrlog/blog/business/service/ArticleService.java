package com.zrlog.blog.business.service;

import com.hibegin.common.util.ObjectHelpers;
import com.hibegin.common.util.ObjectUtil;
import com.hibegin.common.util.StringUtils;
import com.hibegin.http.server.api.HttpRequest;
import com.zrlog.blog.web.util.WebTools;
import com.zrlog.business.service.VisitorArticleService;
import com.zrlog.common.Constants;
import com.zrlog.model.Comment;
import com.zrlog.model.Log;
import com.zrlog.util.I18nUtil;
import com.zrlog.util.ThreadUtils;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

public class ArticleService {

    private List<Map<String, Object>> getTags(Map<String, Object> log, HttpRequest request) {
        String keywords = ObjectHelpers.requireNonNullElse(log.get("keywords"), "").toString();
        List<Map<String, Object>> tags = new ArrayList<>();
        for (String tag : keywords.split(",")) {
            if (StringUtils.isEmpty(tag.trim())) {
                continue;
            }
            Map<String, Object> map = new HashMap<>();
            map.put("name", tag);
            map.put("url", WebTools.buildEncodedUrl(request, "tag/" + tag + Constants.getSuffix(request)));
            tags.add(map);
        }
        return tags;
    }

    public Map<String, Object> detail(Object idOrAlias, HttpRequest request) throws SQLException {
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        ExecutorService executor = ThreadUtils.newFixedThreadPool(4);
        try {
            Map<String, Object> log = new Log().findByIdOrAlias(idOrAlias);
            if (Objects.isNull(log)) {
                return null;
            }
            log.put("tags", getTags(log, request));
            int logId = ((Number) log.get("logId")).intValue();
            String noLastLog = I18nUtil.getBlogStringFromRes("noLastLog");
            String noNextLog = I18nUtil.getBlogStringFromRes("noNextLog");
            futures.add(CompletableFuture.runAsync(() -> {
                Map<String, Object> lastLog;
                try {
                    lastLog = ObjectUtil.requireNonNullElse(new Log().findLastLog(logId), new HashMap<>(Map.of("title", noLastLog, "alias", idOrAlias)));
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                log.put("lastLog", lastLog);
            }));
            futures.add(CompletableFuture.runAsync(() -> {
                Map<String, Object> nextLog;
                try {
                    nextLog = ObjectUtil.requireNonNullElse(new Log().findNextLog(logId), new HashMap<>(Map.of("title", noNextLog, "alias", idOrAlias)));
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                log.put("nextLog", nextLog);
            }));
            futures.add(CompletableFuture.runAsync(() -> {
                try {
                    log.put("comments", new Comment().findAllByLogId(logId));
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }));
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
            return VisitorArticleService.handlerArticle(log);
        } finally {
            executor.shutdownNow();
        }
    }

}
