package com.zrlog.web.filter;

import jakarta.servlet.FilterConfig;
import jakarta.servlet.GenericFilter;
import jakarta.servlet.ServletContext;
import com.hibegin.http.server.config.AbstractServerConfig;
import com.zrlog.common.Constants;
import com.zrlog.common.Updater;
import com.zrlog.common.UpdaterTypeEnum;
import com.zrlog.common.updater.WarUpdater;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ZrLogSwsServletFilterTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void shouldResolveRootWarFileFromServletContext() throws Exception {
        File webapps = temporaryFolder.newFolder("webapps");
        File root = new File(webapps, "ROOT");

        String warFile = getWarFile("/", root.getAbsolutePath());

        assertEquals(new File(webapps, "ROOT.war").getAbsolutePath(), warFile);
    }

    @Test
    public void shouldResolveContextWarFileFromServletContext() throws Exception {
        File webapps = temporaryFolder.newFolder("webapps");
        File blog = new File(webapps, "blog");

        String warFile = getWarFile("/blog", blog.getAbsolutePath());

        assertEquals(new File(webapps, "blog.war").getAbsolutePath(), warFile);
    }

    @Test
    public void shouldUseWarUpdaterOutsideDevMode() throws Exception {
        File webapps = temporaryFolder.newFolder("webapps");
        File blog = new File(webapps, "blog");
        ZrLogSwsServletFilter filter = new ZrLogSwsServletFilter();
        String previousRunMode = System.getProperty("sws.run.mode");
        try {
            System.clearProperty("sws.run.mode");
            setFilterConfig(filter, filterConfig("/blog", blog.getAbsolutePath()));
            Method method = ZrLogSwsServletFilter.class.getDeclaredMethod("getUpdater");
            method.setAccessible(true);

            Updater updater = (Updater) method.invoke(filter);

            assertTrue(updater instanceof WarUpdater);
        } finally {
            restoreProperty("sws.run.mode", previousRunMode);
        }
    }

    @Test
    public void shouldUseZipUpdaterInDevMode() throws Exception {
        File webapps = temporaryFolder.newFolder("webapps");
        File blog = new File(webapps, "blog");
        ZrLogSwsServletFilter filter = new ZrLogSwsServletFilter();
        String previousRunMode = System.getProperty("sws.run.mode");
        try {
            System.setProperty("sws.run.mode", "dev");
            setFilterConfig(filter, filterConfig("/blog", blog.getAbsolutePath()));
            Method method = ZrLogSwsServletFilter.class.getDeclaredMethod("getUpdater");
            method.setAccessible(true);

            Updater updater = (Updater) method.invoke(filter);

            assertNotNull(updater);
            assertEquals(UpdaterTypeEnum.ZIP, updater.getType());
        } finally {
            restoreProperty("sws.run.mode", previousRunMode);
        }
    }

    @Test
    public void shouldBuildServerConfigFromServletContext() throws Exception {
        File webapps = temporaryFolder.newFolder("webapps");
        File blog = new File(webapps, "blog");
        ZrLogSwsServletFilter filter = new ZrLogSwsServletFilter();
        String previousRunMode = System.getProperty("sws.run.mode");
        String previousEnv = System.getProperty("env");
        Object previousConfig = Constants.zrLogConfig;
        try {
            System.setProperty("sws.run.mode", "dev");
            System.setProperty("env", "junit-test");
            setFilterConfig(filter, filterConfig("/blog", blog.getAbsolutePath()));
            Method method = ZrLogSwsServletFilter.class.getDeclaredMethod("getServerConfig");
            method.setAccessible(true);

            AbstractServerConfig config = (AbstractServerConfig) method.invoke(filter);

            assertNotNull(config);
            assertEquals("/blog", Constants.zrLogConfig.getServerConfig().getContextPath());
            assertEquals(config, Constants.zrLogConfig);
        } finally {
            Constants.zrLogConfig = (com.zrlog.common.ZrLogConfig) previousConfig;
            restoreProperty("env", previousEnv);
            restoreProperty("sws.run.mode", previousRunMode);
        }
    }

    @Test
    public void shouldStartPluginsDuringServletInit() throws Exception {
        File webapps = temporaryFolder.newFolder("init-webapps");
        File blog = new File(webapps, "blog");
        assertTrue(blog.mkdirs());
        ZrLogSwsServletFilter filter = new ZrLogSwsServletFilter();
        String previousRunMode = System.getProperty("sws.run.mode");
        String previousEnv = System.getProperty("env");
        String previousRootPath = System.getProperty("sws.root.path");
        String previousConfPath = System.getProperty("sws.conf.path");
        String previousLogPath = System.getProperty("sws.log.path");
        String previousTempPath = System.getProperty("sws.temp.path");
        Object previousConfig = Constants.zrLogConfig;
        try {
            System.setProperty("sws.run.mode", "dev");
            System.setProperty("env", "junit-test");
            setFilterConfig(filter, filterConfig("/blog", blog.getAbsolutePath()));

            filter.init();

            assertNotNull(Constants.zrLogConfig);
            assertEquals("/blog", Constants.zrLogConfig.getServerConfig().getContextPath());
        } finally {
            Constants.zrLogConfig = (com.zrlog.common.ZrLogConfig) previousConfig;
            restoreProperty("env", previousEnv);
            restoreProperty("sws.run.mode", previousRunMode);
            restoreProperty("sws.root.path", previousRootPath);
            restoreProperty("sws.conf.path", previousConfPath);
            restoreProperty("sws.log.path", previousLogPath);
            restoreProperty("sws.temp.path", previousTempPath);
        }
    }

    private static String getWarFile(String contextPath, String realPath) throws Exception {
        ZrLogSwsServletFilter filter = new ZrLogSwsServletFilter();
        setFilterConfig(filter, filterConfig(contextPath, realPath));
        Method method = ZrLogSwsServletFilter.class.getDeclaredMethod("getWarFile");
        method.setAccessible(true);
        return (String) method.invoke(filter);
    }

    private static void setFilterConfig(GenericFilter filter, FilterConfig filterConfig) throws Exception {
        Field field = GenericFilter.class.getDeclaredField("config");
        field.setAccessible(true);
        field.set(filter, filterConfig);
    }

    private static void restoreProperty(String key, String value) {
        if (value == null) {
            System.clearProperty(key);
        } else {
            System.setProperty(key, value);
        }
    }

    private static FilterConfig filterConfig(String contextPath, String realPath) {
        ServletContext servletContext = (ServletContext) Proxy.newProxyInstance(
                ZrLogSwsServletFilterTest.class.getClassLoader(),
                new Class[]{ServletContext.class},
                (proxy, method, args) -> {
                    if ("getContextPath".equals(method.getName())) {
                        return contextPath;
                    }
                    if ("getRealPath".equals(method.getName())) {
                        return realPath;
                    }
                    if ("toString".equals(method.getName())) {
                        return "ServletContextProxy";
                    }
                    return null;
                });
        return (FilterConfig) Proxy.newProxyInstance(
                ZrLogSwsServletFilterTest.class.getClassLoader(),
                new Class[]{FilterConfig.class},
                (proxy, method, args) -> {
                    if ("getServletContext".equals(method.getName())) {
                        return servletContext;
                    }
                    if ("getFilterName".equals(method.getName())) {
                        return "zrlog";
                    }
                    if ("toString".equals(method.getName())) {
                        return "FilterConfigProxy";
                    }
                    return null;
                });
    }
}
