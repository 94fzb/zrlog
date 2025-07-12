package com.zrlog.business.plugin;

import com.hibegin.http.server.api.HttpRequest;
import com.zrlog.plugin.IPlugin;

import java.io.File;
import java.io.InputStream;

public interface StaticSitePlugin extends IPlugin {

    String versionFileName = "version.txt";

    void copyResourceToCacheFolder(String resourceName);


    File saveResponseBodyToHtml(HttpRequest httpRequest, String copy);

    File getCacheHtmlFolder();

    String saveCacheHtmlFolderVersion();

    boolean isStaticPlugin(HttpRequest request);

    void setVersion(long version);

    boolean isSynchronized();

    File loadCacheFile(HttpRequest request);

    void saveToCacheFolder(InputStream inputStream, String uri);
}
