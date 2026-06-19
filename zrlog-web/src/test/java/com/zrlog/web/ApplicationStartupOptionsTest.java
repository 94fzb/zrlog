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
}
