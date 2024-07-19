package com.zrlog.business.util;

import com.zrlog.common.Constants;

import java.io.ByteArrayInputStream;

/**
 * 用于适配 pages 服务
 */
public class PageServiceUtil {

    public static void saveRedirectRules(String notFileUri) {
        Constants.zrLogConfig.getCacheService().saveToCacheFolder(new ByteArrayInputStream(("/* " + notFileUri + " 404").getBytes()), "/_redirects");
    }
}
