package com.zrlog.web.config;

import com.hibegin.common.dao.DAO;
import com.hibegin.http.server.ApplicationContext;
import com.hibegin.http.server.api.HttpRequest;
import com.hibegin.http.server.di.SwsBindings;
import com.hibegin.http.server.web.Controller;
import com.zrlog.admin.business.AdminConstants;
import com.zrlog.admin.business.service.WebSiteSettingsService;
import com.zrlog.blog.web.controller.api.BlogApiCacheController;
import com.zrlog.common.CacheService;
import com.zrlog.common.Constants;
import com.zrlog.common.cache.vo.BaseDataInitVO;
import com.zrlog.install.web.InstallConstants;
import com.zrlog.web.WebSiteInfoProvider;
import com.zrlog.web.setup.install.ZrLogInstallAction;
import com.zrlog.web.support.InstalledTestSite;
import java.io.File;
import java.lang.reflect.Proxy;
import java.util.Collections;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import static org.junit.Assert.*;

public class CacheInjectionIntegrationTest {
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void installationInitializesFrozenBindingsAndAdminChangesReachBlogThroughTheSharedCache() throws Exception {
        withRuntime(root -> {
            ZrLogConfigImpl config = config();
            try {
                Constants.zrLogConfig = config;
                new ApplicationContext(config.getServerConfig()).init();
                SwsBindings.Container services = config.getServerConfig().getBindings().build();
                assertNull(config.getDataSource());
                assertThrows(SwsBindings.CreationException.class, () -> services.get(CacheService.class));

                try (InstalledTestSite ignored = InstalledTestSite.open(root,
                        new ZrLogInstallAction(config, new File(root, "conf/install.lock")))) {
                    assertTrue(config.isInstalled());
                    CacheService cache = services.get(CacheService.class);
                    assertSame(cache.getPublicWebSiteInfo(), config.getWebSetup(WebSiteInfoProvider.class).getPublicWebSiteInfo());
                    assertEquals("Installed H2 Site", cache.getPublicWebSiteInfo().getTitle());

                    BlogApiCacheController first = blogController(config);
                    BaseDataInitVO before = first.index().getData();
                    services.get(WebSiteSettingsService.class).update(new SiteTitle("Changed by admin"),
                            request(config, "/api/admin/website"));
                    BlogApiCacheController second = blogController(config);
                    assertNotSame(first, second);
                    assertSame(before, second.index().getData());
                    assertEquals("Changed by admin", second.index().getData().getWebSite().getTitle());
                    assertEquals("Changed by admin", new com.zrlog.model.WebSite().getStringValueByName("title"));
                    config.configDatabase();
                    assertSame(cache, services.get(CacheService.class));
                }
            } finally {
                config.stop();
            }
        });
    }

    @Test
    public void installedStartupDiscoversTheDataProviderAndUsesTheSameCacheForDirectControllerDispatch() throws Exception {
        withRuntime(root -> {
            try (InstalledTestSite ignored = InstalledTestSite.open(root)) {
                ZrLogConfigImpl config = config();
                try {
                    Constants.zrLogConfig = config;
                    assertSame(config.getServerConfig().getBindings().build().get(CacheService.class).getInitData(), blogController(config).index().getData());
                    assertEquals("Installed H2 Site", blogController(config).index().getData().getWebSite().getTitle());
                    assertNotNull(config.getTokenService());
                    // Controller dispatch and its cache dependency must not resolve through the global bridge.
                    Constants.zrLogConfig = null;
                    assertSame(config.getServerConfig().getBindings().build().get(CacheService.class).getInitData(), blogController(config).index().getData());
                } finally {
                    config.stop();
                }
            }
        });
    }

    private ZrLogConfigImpl config() {
        return new ZrLogConfigImpl(19088, null, "") {
            // Exercise the real installation callback without starting background plugins in this test.
            @Override public void startPlugins(boolean async) { }
        };
    }

    private BlogApiCacheController blogController(ZrLogConfigImpl config) throws Exception {
        return (BlogApiCacheController) Controller.buildController(BlogApiCacheController.class.getMethod("index"),
                request(config, "/api/cache"), null);
    }

    private HttpRequest request(ZrLogConfigImpl config, String uri) {
        return (HttpRequest) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{HttpRequest.class},
                (proxy, method, args) -> {
                    switch (method.getName()) {
                        case "getServerConfig": return config.getServerConfig();
                        case "getUri": return uri;
                        case "getHeaderMap": return Collections.emptyMap();
                        case "getRemoteAddr": return "127.0.0.1";
                        default: return null;
                    }
                });
    }

    private void withRuntime(RuntimeTest test) throws Exception {
        String oldRoot = System.getProperty("sws.root.path");
        String oldEnv = System.getProperty("env");
        var oldConfig = Constants.zrLogConfig;
        var oldInstall = InstallConstants.installConfig;
        var oldResource = AdminConstants.adminResource;
        var oldDataSource = DAO.getDefaultDataSource();
        File root = temporaryFolder.newFolder();
        try {
            System.setProperty("sws.root.path", root.getAbsolutePath());
            System.setProperty("env", "junit-test");
            Constants.zrLogConfig = null;
            test.run(root);
        } finally {
            Constants.zrLogConfig = oldConfig;
            InstallConstants.installConfig = oldInstall;
            AdminConstants.adminResource = oldResource;
            DAO.setDs(oldDataSource);
            restore("sws.root.path", oldRoot);
            restore("env", oldEnv);
        }
    }

    private static void restore(String key, String value) {
        if (value == null) System.clearProperty(key);
        else System.setProperty(key, value);
    }

    public static final class SiteTitle {
        private final String title;
        SiteTitle(String title) { this.title = title; }
        public String getTitle() { return title; }
    }

    private interface RuntimeTest { void run(File root) throws Exception; }
}
