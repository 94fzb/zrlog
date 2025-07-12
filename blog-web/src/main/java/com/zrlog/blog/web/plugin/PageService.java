package com.zrlog.blog.web.plugin;

import com.zrlog.business.plugin.StaticSitePlugin;

import java.io.ByteArrayInputStream;

/**
 * 用于适配 pages 服务
 */
public class PageService {

    private final StaticSitePlugin staticSitePlugin;

    public PageService(StaticSitePlugin staticSitePlugin) {
        this.staticSitePlugin = staticSitePlugin;
    }

    public void saveRedirectRules(String notFileUri) {
        staticSitePlugin.saveToCacheFolder(new ByteArrayInputStream(("/* " + notFileUri + " 404").getBytes()), "/_redirects");
    }
}
