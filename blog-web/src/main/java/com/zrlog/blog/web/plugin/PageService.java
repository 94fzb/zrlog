package com.zrlog.blog.web.plugin;

import java.io.ByteArrayInputStream;

/**
 * 用于适配 pages 服务
 */
public class PageService {

    private final BlogPageStaticSitePlugin staticSitePlugin;

    public PageService(BlogPageStaticSitePlugin staticSitePlugin) {
        this.staticSitePlugin = staticSitePlugin;
    }

    public void saveRedirectRules(String notFileUri) {
        staticSitePlugin.saveToCacheFolder(new ByteArrayInputStream(("/* " + notFileUri + " 404").getBytes()), "/_redirects");
    }
}
