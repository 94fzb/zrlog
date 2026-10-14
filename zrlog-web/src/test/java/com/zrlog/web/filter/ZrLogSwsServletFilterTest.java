package com.zrlog.web.filter;

import jakarta.servlet.FilterConfig;
import jakarta.servlet.GenericFilter;
import jakarta.servlet.ServletContext;
import com.hibegin.http.server.config.AbstractServerConfig;
import com.zrlog.common.Constants;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Proxy;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class ZrLogSwsServletFilterTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

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

            AbstractServerConfig config = filter.getServerConfig();

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
