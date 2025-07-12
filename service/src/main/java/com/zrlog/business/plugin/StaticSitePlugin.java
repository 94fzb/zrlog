package com.zrlog.business.plugin;

import com.hibegin.http.server.api.HttpRequest;
import com.zrlog.plugin.IPlugin;

public interface StaticSitePlugin extends IPlugin {


    boolean isStaticPlugin(HttpRequest request);

    void setVersion(long version);

    boolean isSynchronized();
}
