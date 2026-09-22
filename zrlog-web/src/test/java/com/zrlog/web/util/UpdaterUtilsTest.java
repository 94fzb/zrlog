package com.zrlog.web.util;

import com.zrlog.common.Updater;
import com.zrlog.common.UpdaterTypeEnum;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.FileOutputStream;
import java.util.jar.Attributes;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

import static org.junit.Assert.*;

public class UpdaterUtilsTest {
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void shouldCreateZipUpdaterForTheLauncherRatherThanTheLastDependency() throws Exception {
        File launcher = jar("my blog launcher.jar", "com.zrlog.web.Application");
        File dependency = jar("slf4j-simple-2.0.13.jar", null);
        String original = System.getProperty("java.class.path");
        try {
            System.setProperty("java.class.path", launcher + File.pathSeparator + dependency);
            Updater updater = UpdaterUtils.getUpdater(new String[0], null);
            assertNotNull(updater);
            assertEquals(UpdaterTypeEnum.ZIP, updater.getType());
            assertEquals(launcher.getCanonicalFile(), updater.execFile());
        } finally {
            System.setProperty("java.class.path", original);
        }
    }

    @Test
    public void shouldResolveSingleJarLaunchAndDeduplicateClasspathEntries() throws Exception {
        File launcher = jar("launcher.jar", "com.zrlog.web.Application");
        assertEquals(launcher.getCanonicalFile(), resolve(launcher.toString()));
        assertEquals(launcher.getCanonicalFile(), resolve(launcher + File.pathSeparator + launcher));
    }

    @Test
    public void shouldUseOnlyAValidatedDistributionLauncherForClasspathLaunches() throws Exception {
        File dependency = jar("dependency.jar", "another.Application");
        assertNull(resolve(dependency.toString()));
        File launcher = jar("zrlog-starter.jar", "com.zrlog.web.Application");
        assertEquals(launcher.getCanonicalFile(), resolve(dependency.toString()));
    }

    @Test
    public void shouldRejectUnreadableUnrelatedAndAmbiguousJars() throws Exception {
        File broken = temporaryFolder.newFile("broken.jar");
        jar("zrlog-starter.jar", "another.Application");
        assertNull(resolve(broken.toString()));
        File first = jar("first.jar", "com.zrlog.web.Application");
        File second = jar("second.jar", "com.zrlog.web.Application");
        assertNull(resolve(first + File.pathSeparator + second));
    }

    private File resolve(String classPath) {
        return UpdaterUtils.findLauncherJar(classPath, temporaryFolder.getRoot());
    }

    private File jar(String name, String mainClass) throws Exception {
        File file = temporaryFolder.newFile(name);
        Manifest manifest = new Manifest();
        manifest.getMainAttributes().put(Attributes.Name.MANIFEST_VERSION, "1.0");
        if (mainClass != null) {
            manifest.getMainAttributes().put(Attributes.Name.MAIN_CLASS, mainClass);
        }
        try (JarOutputStream ignored = new JarOutputStream(new FileOutputStream(file), manifest)) {
            // An actual JAR manifest exercises the same entrypoint lookup as packaged applications.
        }
        return file;
    }
}
