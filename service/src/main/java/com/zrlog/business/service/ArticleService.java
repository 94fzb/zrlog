package com.zrlog.business.service;

import com.zrlog.model.Comment;
import com.zrlog.model.Log;
import com.zrlog.util.I18nUtil;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ArticleService {

    public Map<String, Object> detail(Object idOrAlias) throws SQLException {
        Map<String, Object> log = new Log().findByIdOrAlias(idOrAlias);
        if (Objects.isNull(log)) {
            return null;
        }
        Integer logId = (Integer) log.get("logId");
        Map<String, Object> lastLog = Objects.requireNonNullElse(new Log().findLastLog(logId), new HashMap<>(Map.of("title", I18nUtil.getBlogStringFromRes("noLastLog"), "alias", idOrAlias)));
        Map<String, Object> nextLog = Objects.requireNonNullElse(new Log().findNextLog(logId), new HashMap<>(Map.of("title", I18nUtil.getBlogStringFromRes("noNextLog"), "alias", idOrAlias)));
        log.put("lastLog", lastLog);
        log.put("nextLog", nextLog);
        log.put("comments", new Comment().findAllByLogId(logId));
        return log;
    }

}
