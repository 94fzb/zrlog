package com.zrlog.web.util;

import com.hibegin.common.util.EnvKit;
import com.hibegin.common.util.LoggerUtil;
import com.zrlog.common.updater.NativeImageUpdater;
import com.zrlog.common.updater.ZipUpdater;
import com.zrlog.common.Updater;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UpdaterUtils {

    private static final Logger LOGGER = LoggerUtil.getLogger(UpdaterUtils.class);

    public static Updater getUpdater(String[] args, File file) {
        boolean nativeImage = EnvKit.isNativeImage();
        try {
            if (nativeImage) {
                return new NativeImageUpdater(args, file);
            }
            File jarFile = new File(System.getProperty("java.class.path"));
            return new ZipUpdater(args, jarFile);
        } catch (Throwable e) {
            LOGGER.log(Level.WARNING,
                    "Create " + (nativeImage ? "native" : "zip") + " updater failed, target file: " + file, e);
            return null;
        }
    }
}
