package com.zrlog.common;

import java.util.List;
import java.util.Map;

public interface CacheService<T> {

    String ZRLOG_SQL_VERSION_KEY = "zrlogSqlVersion";

    long getCurrentSqlVersion();

    long getWebSiteVersion();

    T getInitData();

    T refreshInitData();

    Object getPublicWebSiteInfoFirstByCache(String key);

    List<Map<String, Object>> getArticleTypes();

    List<Map<String, Object>> getTags();

}
