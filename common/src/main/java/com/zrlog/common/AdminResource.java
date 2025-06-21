package com.zrlog.common;

import com.hibegin.http.server.api.HttpRequest;

import java.io.InputStream;
import java.util.Set;

public interface AdminResource  {

    String getContextPath();

    Set<String> getAdminResourceUris(boolean requiredStaticFiles);

    Set<String> getAdminPageUris();

    Set<String> getAdminCacheableApiUris();

    boolean isAdminMainJs(String uri);

    InputStream renderServiceWorker(HttpRequest request);

    String getStaticResourceBuildId();
}
