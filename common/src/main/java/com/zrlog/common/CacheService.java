package com.zrlog.common;

import com.hibegin.http.server.api.HttpRequest;
import com.zrlog.business.cache.vo.BaseDataInitVO;

import java.io.File;
import java.io.InputStream;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface CacheService {

    void refreshFavicon();

    File loadCacheFile(HttpRequest request);

    File saveResponseBodyToHtml(HttpRequest httpRequest, String copy);

    File getCacheHtmlFolder();

    void saveToCacheFolder(InputStream inputStream, String uri);

    CompletableFuture<BaseDataInitVO> refreshInitDataCacheAsync(HttpRequest servletRequest, boolean cleanAble);

    String getFileFlagFirstByCache(String uriPath);

    boolean isCacheableByRequest(HttpRequest request);

    Map<String, Object> refreshWebSite();
}
