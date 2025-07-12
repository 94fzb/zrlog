package com.zrlog.common;

import com.hibegin.http.server.api.HttpRequest;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface CacheService<T> {

    String versionFileName = "version.txt";

    void refreshFavicon();

    File loadCacheFile(HttpRequest request);

    File saveResponseBodyToHtml(HttpRequest httpRequest, String copy);

    File getCacheHtmlFolder();

    String saveCacheHtmlFolderVersion();

    long getWebSiteVersion();

    void saveToCacheFolder(InputStream inputStream, String uri);

    CompletableFuture<T> refreshInitDataCacheAsync(HttpRequest servletRequest, boolean cleanAble);

    String getFileFlagFirstByCache(String uriPath);

    boolean isCacheableByRequest(HttpRequest request);

    Map<String, Object> refreshWebSite();

    Object getPublicWebSiteInfoFirstByCache(String key);

    List<Map<String, Object>> getArticleTypes(HttpRequest request);

    List<Map<String, Object>> getTags(HttpRequest request);

    void copyResourceToCacheFolder(String resourceName);
}
