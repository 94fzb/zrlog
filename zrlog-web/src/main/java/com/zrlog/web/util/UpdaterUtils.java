package com.zrlog.web.util;


import com.hibegin.common.util.EnvKit;
import com.zrlog.admin.web.plugin.NativeImageUpdater;
import com.zrlog.admin.web.plugin.ZipUpdater;
import com.zrlog.common.Updater;

import java.io.File;

public class UpdaterUtils {

    public static Updater getUpdater(String[] args, File file) {
        try {
            if (EnvKit.isNativeImage()) {
                return new NativeImageUpdater(args, file);
            }
            File jarFile = new File(System.getProperty("java.class.path"));
            return new ZipUpdater(args, jarFile);
        } catch (Throwable e) {
            return null;
        }
    }
}
