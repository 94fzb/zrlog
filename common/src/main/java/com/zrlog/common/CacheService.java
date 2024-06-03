package com.zrlog.common;

import com.hibegin.http.server.api.HttpRequest;

import java.io.File;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface CacheService {

    void refreshFavicon();

    File loadCacheFile(HttpRequest request);

    File saveResponseBodyToHtml(HttpRequest httpRequest, String copy);

    void saveToCacheFolder(InputStream inputStream, String uri);

    CompletableFuture<Void> refreshInitDataCacheAsync(HttpRequest servletRequest, boolean cleanAble);

    String getFileFlag(String uriPath);

    boolean isCacheableByRequest(HttpRequest request);

    Map<String, Object> refreshWebSite();
}
