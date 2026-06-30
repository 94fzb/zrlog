package com.zrlog.web;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ApplicationStartupOptionsTest {

    @Test
    public void shouldParsePortAndContextPathFromArgs() {
        ApplicationStartupOptions options = ApplicationStartupOptions.parse(new String[]{
                "--port=19080",
                "--contextPath=/blog"
        });

        assertEquals(19080, options.getPort());
        assertEquals("/blog", options.getContextPath());
    }

    @Test
    public void shouldUseDefaultStartupOptionsWhenArgsAreMissing() {
        ApplicationStartupOptions options = ApplicationStartupOptions.parse(null);

        assertEquals(8080, options.getPort());
        assertEquals("", options.getContextPath());
    }

    @Test
    public void shouldParseIndependentStartupOptions() {
        assertEquals(19081, ApplicationStartupOptions.parse(new String[]{"--port=19081"}).getPort());
        assertEquals("/admin", ApplicationStartupOptions.parse(new String[]{"--contextPath=/admin"}).getContextPath());
    }
}
