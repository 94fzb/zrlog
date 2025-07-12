package com.zrlog.business.plugin;

import com.hibegin.http.server.api.HttpRequest;
import com.zrlog.plugin.IPlugin;

import java.io.File;
import java.io.InputStream;

public interface StaticSitePlugin extends IPlugin {

    void copyResourceToCacheFolder(String resourceName);

    File saveResponseBodyToHtml(HttpRequest httpRequest, String copy);

    boolean isStaticPlugin(HttpRequest request);

    void setVersion(long version);

    boolean isSynchronized();

    File loadCacheFile(HttpRequest request);

    void saveToCacheFolder(InputStream inputStream, String uri);
}
