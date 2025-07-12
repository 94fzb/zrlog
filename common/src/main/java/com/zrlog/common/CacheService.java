package com.zrlog.common;

import com.hibegin.http.server.api.HttpRequest;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface CacheService<T> {


    void refreshFavicon();

    long getWebSiteVersion();

    CompletableFuture<T> refreshInitDataCacheAsync(HttpRequest servletRequest, boolean cleanAble);

    String getFileFlagFirstByCache(String uriPath);

    boolean isCacheableByRequest(HttpRequest request);

    Map<String, Object> refreshWebSite();

    Object getPublicWebSiteInfoFirstByCache(String key);

    List<Map<String, Object>> getArticleTypes(HttpRequest request);

    List<Map<String, Object>> getTags(HttpRequest request);

}
