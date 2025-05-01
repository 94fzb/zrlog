package com.zrlog.admin.business.service;

import com.hibegin.http.server.api.HttpRequest;

import java.io.InputStream;
import java.util.Map;
import java.util.Set;

public interface AdminResource  {

    Set<String> getAdminStaticResourceUris();

    Set<String> getAdminPageUris();

    Set<String> getAdminStaticCacheUris();

    Set<String> getAdminCacheableApiUris();

    boolean isAdminMainJs(String uri);

    InputStream renderServiceWorker(HttpRequest request);

    String getStaticResourceBuildId();

    Map<String, Object> adminResourceInfo(HttpRequest request);
}
