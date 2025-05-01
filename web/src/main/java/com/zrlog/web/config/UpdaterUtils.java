package com.zrlog.web.config;


import com.zrlog.admin.web.plugin.ZipUpdater;

import java.io.File;

public class UpdaterUtils {

    public static ZipUpdater getZipUpdater(String[] args) {
        try {
            File jarFile = new File(System.getProperty("java.class.path"));
            return new ZipUpdater(args, jarFile);
        } catch (Throwable e) {
            return null;
        }
    }
}
