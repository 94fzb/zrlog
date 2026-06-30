package com.zrlog.web.config;

import com.zrlog.web.WebSetup;
import com.zrlog.web.WebSetupContext;
import com.zrlog.web.WebSetupProvider;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class SetupConfigTest {

    @Test
    public void shouldParseDisableModules() {
        Set<String> modules = SetupConfig.parseDisableModules(" admin,blog,, install ");

        assertEquals(new HashSet<>(Arrays.asList("admin", "blog", "install")), modules);
        assertTrue(SetupConfig.parseDisableModules(null).isEmpty());
    }

    @Test
    public void shouldSortAndDeduplicateWebSetupProviders() {
        FakeProvider lateAdmin = new FakeProvider("admin", 20);
        FakeProvider earlyAdmin = new FakeProvider("admin", 5);
        FakeProvider blog = new FakeProvider("blog", 10);
        FakeProvider unnamed = new FakeProvider(" ", 1);

        List<WebSetupProvider> providers = SetupConfig.normalizeWebSetupProviders(
                Arrays.asList(lateAdmin, blog, unnamed, earlyAdmin));

        assertEquals(2, providers.size());
        assertSame(earlyAdmin, providers.get(0));
        assertSame(blog, providers.get(1));
    }

    @Test
    public void shouldSortProvidersByOrderThenName() {
        FakeProvider blog = new FakeProvider("blog", 10);
        FakeProvider admin = new FakeProvider("admin", 10);
        FakeProvider install = new FakeProvider("install", 20);

        List<WebSetupProvider> providers = SetupConfig.normalizeWebSetupProviders(Arrays.asList(install, blog, admin));

        assertEquals(Arrays.asList(admin, blog, install), providers);
    }

    @Test
    public void shouldSkipNullProviderNameWhenNormalizing() {
        List<WebSetupProvider> providers = SetupConfig.normalizeWebSetupProviders(Arrays.asList(
                new FakeProvider(null, 10),
                new FakeProvider("admin", 20)));

        assertEquals(1, providers.size());
        assertEquals("admin", providers.get(0).name());
    }

    @Test
    public void shouldParseStrictMode() {
        assertTrue(SetupConfig.parseStrictMode("true"));
        assertTrue(SetupConfig.parseStrictMode(" TRUE "));
        assertTrue(SetupConfig.parseStrictMode("1"));
        assertTrue(SetupConfig.parseStrictMode("yes"));
        assertTrue(SetupConfig.parseStrictMode(" Yes "));
        assertFalse(SetupConfig.parseStrictMode("false"));
        assertFalse(SetupConfig.parseStrictMode(null));
    }

    @Test(expected = IllegalStateException.class)
    public void shouldFailStrictModeOnDuplicatedWebSetupProvider() {
        SetupConfig.normalizeWebSetupProviders(Arrays.asList(
                new FakeProvider("admin", 10),
                new FakeProvider("admin", 20)), true);
    }

    @Test(expected = IllegalStateException.class)
    public void shouldFailStrictModeOnUnnamedWebSetupProvider() {
        SetupConfig.normalizeWebSetupProviders(Arrays.asList(
                new FakeProvider(" ", 10),
                new FakeProvider("admin", 20)), true);
    }

    private static class FakeProvider implements WebSetupProvider {

        private final String name;
        private final int order;

        FakeProvider(String name, int order) {
            this.name = name;
            this.order = order;
        }

        @Override
        public String name() {
            return name;
        }

        @Override
        public int order() {
            return order;
        }

        @Override
        public WebSetup create(WebSetupContext context) {
            return () -> {
            };
        }
    }
}
