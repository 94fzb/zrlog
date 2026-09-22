package com.zrlog.web.util;

import com.hibegin.common.util.EnvKit;
import com.hibegin.common.util.LoggerUtil;
import com.zrlog.common.updater.NativeImageUpdater;
import com.zrlog.common.updater.ZipUpdater;
import com.zrlog.common.Updater;
import com.zrlog.common.Constants;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.regex.Pattern;
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
            File jarFile = findLauncherJar(System.getProperty("java.class.path", ""),
                    new File(Constants.getZrLogHome()));
            if (jarFile == null) {
                LOGGER.fine("No unambiguous ZrLog launcher JAR found; ZIP updater is unavailable");
                return null;
            }
            return new ZipUpdater(args, jarFile);
        } catch (Throwable e) {
            LOGGER.log(Level.WARNING,
                    "Create " + (nativeImage ? "native" : "zip") + " updater failed, target file: " + file, e);
            return null;
        }
    }

    static File findLauncherJar(String classPath, File applicationHome) {
        Set<File> launchers = new LinkedHashSet<>();
        for (String entry : classPath.split(Pattern.quote(File.pathSeparator))) {
            File candidate = launcherJar(new File(entry));
            if (candidate != null) {
                launchers.add(candidate);
            }
        }
        if (launchers.size() == 1) {
            return launchers.iterator().next();
        }
        if (!launchers.isEmpty()) {
            return null;
        }
        // Classpath launches may omit the distribution's launcher from the classpath.
        return launcherJar(new File(applicationHome, "zrlog-starter.jar"));
    }

    private static File launcherJar(File file) {
        if (!file.isFile() || !file.getName().endsWith(".jar")) {
            return null;
        }
        try (JarFile jar = new JarFile(file)) {
            Manifest manifest = jar.getManifest();
            if (manifest != null && "com.zrlog.web.Application".equals(
                    manifest.getMainAttributes().getValue(Attributes.Name.MAIN_CLASS))) {
                return file.getCanonicalFile();
            }
        } catch (IOException ignored) {
            // A missing or unreadable dependency must never become the restart target.
        }
        return null;
    }
}
