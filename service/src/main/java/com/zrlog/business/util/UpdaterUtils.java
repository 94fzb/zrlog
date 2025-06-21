package com.zrlog.business.util;

import com.zrlog.business.service.ZipUpdater;

import java.io.File;

public class UpdaterUtils {

    public static ZipUpdater getZipUpdater(String[] args) {
        File jarFile = new File(System.getProperty("java.class.path"));
        return new ZipUpdater(args, jarFile);
    }
}
