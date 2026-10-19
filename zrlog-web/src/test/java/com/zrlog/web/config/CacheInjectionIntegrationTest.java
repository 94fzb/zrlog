package com.zrlog.web.config;

import com.hibegin.common.dao.DAO;
import com.hibegin.http.HttpMethod;
import com.hibegin.http.server.ApplicationContext;
import com.hibegin.http.server.api.HttpRequest;
import com.hibegin.http.server.api.HttpResponse;
import com.hibegin.http.server.di.SwsBindings;
import com.hibegin.http.server.web.Controller;
import com.hibegin.http.server.web.cookie.Cookie;
import com.zrlog.admin.business.AdminConstants;
import com.zrlog.admin.business.rest.base.AdminWebSiteInfo;
import com.zrlog.admin.business.service.MessageCenterService;
import com.zrlog.admin.business.service.UploadService;
import com.zrlog.admin.business.service.WebSiteSettingsService;
import com.zrlog.admin.plugin.rest.response.UploadServiceResponseEntity;
import com.zrlog.admin.web.controller.api.AccountSessionController;
import com.zrlog.admin.web.controller.api.AdminSystemController;
import com.zrlog.admin.web.controller.api.PluginUploadController;
import com.zrlog.admin.web.controller.api.UpgradeController;
import com.zrlog.admin.web.controller.page.AdminPageController;
import com.zrlog.admin.web.interceptor.AdminInterceptor;
import com.zrlog.admin.web.interceptor.AdminLoginInterceptor;
import com.zrlog.admin.web.interceptor.AdminPluginInterceptor;
import com.zrlog.admin.web.interceptor.PluginTokenValidator;
import com.zrlog.admin.web.token.AdminTokenThreadLocal;
import com.zrlog.blog.business.rest.response.ApiStandardResponse;
import com.zrlog.blog.web.controller.api.BlogApiCacheController;
import com.zrlog.blog.web.controller.page.ArticleController;
import com.zrlog.blog.web.interceptor.BlogApiInterceptor;
import com.zrlog.blog.web.interceptor.BlogPluginInterceptor;
import com.zrlog.business.exception.MissingInstallException;
import com.zrlog.business.plugin.PluginCorePlugin;
import com.zrlog.business.service.CacheRefreshService;
import com.zrlog.business.service.UpgradeNoticeService;
import com.zrlog.business.updater.UpdateVersionInfoPlugin;
import com.zrlog.common.CacheService;
import com.zrlog.common.TokenService;
import com.zrlog.common.cache.vo.BaseDataInitVO;
import com.zrlog.common.exception.ArgsException;
import com.zrlog.common.vo.Version;
import com.zrlog.web.WebSiteInfoProvider;
import com.zrlog.web.inteceptor.InstallStatusInterceptor;
import com.zrlog.web.setup.install.ZrLogInstallAction;
import com.zrlog.web.support.InstalledTestSite;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.lang.reflect.Proxy;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.*;

public class CacheInjectionIntegrationTest {
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void installationInitializesFrozenBindingsAndAdminChangesReachBlogThroughTheSharedCache() throws Exception {
        withRuntime(root -> {
            ZrLogConfigImpl config = config();
            try {
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
                    AtomicReference<String> pluginCacheVersion = new AtomicReference<>();
                    config.getAllPlugins().add((PluginCorePlugin) Proxy.newProxyInstance(
                            getClass().getClassLoader(), new Class[]{PluginCorePlugin.class}, (proxy, method, args) -> {
                                if (method.getName().equals("refreshCache")) {
                                    pluginCacheVersion.set((String) args[0]);
                                }
                                return method.getReturnType() == boolean.class ? true : null;
                            }));
                    services.get(CacheRefreshService.class)
                            .updateCacheSynchronouslyOrThrow(request(config, "/api/admin/refreshCache"), Collections.emptyList());
                    BaseDataInitVO refreshed = blogController(config).index().getData();
                    assertNotSame(before, refreshed);
                    assertEquals("Changed by admin", refreshed.getWebSite().getTitle());
                    assertEquals(String.valueOf(refreshed.getVersion()), pluginCacheVersion.get());
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
                    assertSame(config.getServerConfig().getBindings().build().get(CacheService.class).getInitData(), blogController(config).index().getData());
                    assertEquals("Installed H2 Site", blogController(config).index().getData().getWebSite().getTitle());
                    assertNotNull(config.getTokenService());
                    // Controller dispatch must reuse this application's cache instance.
                    assertSame(config.getServerConfig().getBindings().build().get(CacheService.class).getInitData(), blogController(config).index().getData());
                } finally {
                    config.stop();
                }
            }
        });
    }

    @Test
    public void sessionConsumersUseTheirApplicationAfterInstallationWithoutTheGlobalConfig() throws Exception {
        withRuntime(root -> {
            ZrLogConfigImpl config = config();
            try {
                ApplicationContext context = new ApplicationContext(config.getServerConfig());
                context.init();
                AdminLoginInterceptor loginInterceptor = (AdminLoginInterceptor) context.getInterceptors().stream()
                        .filter(AdminLoginInterceptor.class::isInstance).findFirst().orElseThrow();
                AdminInterceptor adminInterceptor = (AdminInterceptor) context.getInterceptors().stream()
                        .filter(AdminInterceptor.class::isInstance).findFirst().orElseThrow();
                var installGuard = (InstallStatusInterceptor) context.getInterceptors().stream()
                        .filter(InstallStatusInterceptor.class::isInstance).findFirst().orElseThrow();
                var apiGuard = (BlogApiInterceptor) context.getInterceptors().stream()
                        .filter(BlogApiInterceptor.class::isInstance).findFirst().orElseThrow();
                SessionResponse beforeInstall = new SessionResponse();
                assertFalse(installGuard.doInterceptor(request(config, "/article"), beforeInstall.response()));
                assertEquals("/install?ref=%2Farticle", beforeInstall.redirect);
                assertThrows(MissingInstallException.class,
                        () -> apiGuard.doInterceptor(request(config, "/api/cache"), beforeInstall.response()));
                assertThrows(MissingInstallException.class, () -> adminInterceptor.doInterceptor(
                        sessionRequest(config, "/api/admin/user", "", new Cookie[0]), beforeInstall.response()));
                try (InstalledTestSite ignored = InstalledTestSite.open(root,
                        new ZrLogInstallAction(config, new File(root, "conf/install.lock")))) {
                    SessionResponse response = new SessionResponse();
                    assertThrows(com.zrlog.admin.business.exception.AdminAuthException.class,
                            () -> adminInterceptor.doInterceptor(sessionRequest(config, "/api/admin/user", "", new Cookie[0]), response.response()));
                    assertTrue(installGuard.doInterceptor(request(config, "/article"), response.response()));
                    assertFalse(apiGuard.doInterceptor(sessionRequest(config, "/api/cache", "", new Cookie[0]), response.response()));
                    var cacheResult = (ApiStandardResponse<?>) response.json;
                    assertEquals("Installed H2 Site", ((BaseDataInitVO) cacheResult.getData()).getWebSite().getTitle());
                    String loginBody = "{\"userName\":\"admin\",\"password\":\""
                            + com.hibegin.common.util.SecurityUtils.md5("local-test-password") + "\"}";
                    HttpRequest loginRequest = sessionRequest(config, "/api/admin/login",
                            loginBody, new Cookie[0]);
                    AccountSessionController login = (AccountSessionController) Controller.buildController(
                            AccountSessionController.class.getMethod("login"), loginRequest, response.response());
                    AdminConstants.adminResource = null;
                    var loginData = login.login().getData();
                    assertEquals("admin", loginData.getUserName());
                    assertEquals(config.getWebSetup(com.zrlog.admin.business.service.AdminResourceProvider.class)
                            .getAdminResource().getAdminCacheableApiUris(), loginData.getCacheableApiUris());
                    assertFalse(loginData.getCacheableApiUris().isEmpty());
                    TokenService tokens = config.getServerConfig().getBindings().build().get(TokenService.class);
                    assertSame(tokens, config.getTokenService());
                    Cookie firstCookie = response.cookies.get(0);
                    HttpRequest authenticated = sessionRequest(config, "/admin/login", "", new Cookie[]{firstCookie});
                    assertNotNull(tokens.getAdminTokenVO(authenticated));
                    var site = config.getServerConfig().getBindings().build().get(CacheService.class).getPublicWebSiteInfo();
                    var identity = tokens.getAdminTokenVO(authenticated);
                    var secureResponse = new SessionResponse();
                    site.setGenerator_html_status(true);
                    tokens.setAdminToken(identity.getUserId(), identity.getSecretKey(), "static-session", "https",
                            authenticated, secureResponse.response());
                    assertEquals("None", secureResponse.cookies.get(0).getSameSite());
                    site.setGenerator_html_status(false);
                    tokens.setAdminToken(identity.getUserId(), identity.getSecretKey(), "dynamic-session", "https",
                            authenticated, secureResponse.response());
                    assertNotEquals("None", secureResponse.cookies.get(1).getSameSite());
                    assertFalse(loginInterceptor.doInterceptor(authenticated, response.response()));
                    assertEquals("/admin/index", response.redirect);

                    AdminWebSiteInfo settings = new AdminWebSiteInfo();
                    settings.setSession_timeout(17L);
                    config.getServerConfig().getBindings().build().get(WebSiteSettingsService.class)
                            .updateAdmin(settings, sessionRequest(config, "/api/admin/website/admin", "", new Cookie[0]));
                    AccountSessionController nextLogin = (AccountSessionController) Controller.buildController(
                            AccountSessionController.class.getMethod("login"),
                            sessionRequest(config, "/api/admin/login",
                                    loginBody, new Cookie[0]),
                            response.response());
                    nextLogin.login();
                    Cookie nextCookie = response.cookies.get(response.cookies.size() - 1);
                    var token = tokens.getAdminTokenVO(sessionRequest(config, "/admin", "", new Cookie[]{nextCookie}));
                    assertNotNull(token);
                    assertEquals(17 * 60000L, token.getExpiresAt() - token.getCreatedDate());

                    AdminPageController logout = (AdminPageController) Controller.buildController(
                            AdminPageController.class.getMethod("logout"),
                            sessionRequest(config, "/admin/logout", "", new Cookie[]{nextCookie}), response.response());
                    logout.logout();
                    assertEquals("", response.cookies.get(response.cookies.size() - 1).getValue());
                    assertEquals("/admin/login", response.redirect);
                }
            } finally {
                AdminTokenThreadLocal.remove();
                config.stop();
            }
        });
    }

    @Test
    public void pluginConsumersFollowLateInstallationAndReplacementWithoutGlobalConfig() throws Exception {
        withRuntime(root -> {
            ZrLogConfigImpl config = config();
            try {
                ApplicationContext context = new ApplicationContext(config.getServerConfig());
                context.init();
                BlogPluginInterceptor blog = (BlogPluginInterceptor) context.getInterceptors().stream()
                        .filter(BlogPluginInterceptor.class::isInstance).findFirst().orElseThrow();
                AdminPluginInterceptor admin = (AdminPluginInterceptor) context.getInterceptors().stream()
                        .filter(AdminPluginInterceptor.class::isInstance).findFirst().orElseThrow();
                var services = config.getServerConfig().getBindings().build();
                PluginTokenValidator tokens = services.get(PluginTokenValidator.class);
                HttpRequest firstToken = sessionRequest(config, "/api/admin/refreshCache", "", new Cookie[0],
                        Map.of("X-Plugin-Token", "first-token"));
                assertThrows(ArgsException.class, () -> tokens.validate(firstToken));
                try (InstalledTestSite ignored = InstalledTestSite.open(root,
                        new ZrLogInstallAction(config, new File(root, "conf/install.lock")))) {
                    AtomicReference<String> forwarded = new AtomicReference<>();
                    config.getAllPlugins().add(plugin("first-token", forwarded));
                    tokens.validate(firstToken);
                    HttpResponse response = new SessionResponse().response();
                    assertFalse(blog.doInterceptor(sessionRequest(config, "/plugin/comment/index", "", new Cookie[0]), response));
                    assertEquals("/comment/index", forwarded.get());
                    assertFalse(admin.doInterceptor(sessionRequest(config, "/admin/plugins/comment/manifest.json", "", new Cookie[0]), response));
                    assertEquals("/comment/manifest.json", forwarded.get());

                    PluginUploadController upload = (PluginUploadController) Controller.buildController(
                            PluginUploadController.class.getMethod("upload"),
                            sessionRequest(config, "/api/admin/plugin/upload", "", new Cookie[0]), response);
                    upload.upload();
                    assertEquals("/api/upload", forwarded.get());
                    ArticleController article = (ArticleController) Controller.buildController(
                            ArticleController.class.getMethod("addComment"),
                            sessionRequest(config, "/addComment", "", new Cookie[0]), response);
                    article.addComment();
                    assertEquals("/p/comment/addComment", forwarded.get());
                    assertEquals("https://files.example/cover.png", services.get(UploadService.class).getCloudUrl(
                            "/attached/cover.png", "/tmp/cover.png", firstToken, null).getUrl());

                    config.getAllPlugins().clear();
                    AtomicReference<String> replacementForwarded = new AtomicReference<>();
                    config.getAllPlugins().add(plugin("second-token", replacementForwarded));
                    assertThrows(ArgsException.class, () -> tokens.validate(firstToken));
                    tokens.validate(sessionRequest(config, "/api/admin/refreshCache", "", new Cookie[0],
                            Map.of("X-Plugin-Token", "second-token")));
                    upload.upload();
                    assertEquals("/api/upload", replacementForwarded.get());
                }
            } finally {
                config.stop();
            }
        });
    }

    @Test
    public void upgradeNoticesAndControllersFollowTheirInstalledApplicationWithoutGlobalConfig() throws Exception {
        withRuntime(root -> {
            ZrLogConfigImpl config = config();
            try {
                new ApplicationContext(config.getServerConfig()).init();
                var services = config.getServerConfig().getBindings().build();
                var notices = services.get(UpgradeNoticeService.class);
                var future = new Version();
                future.setVersion("99.0.0");
                future.setBuildId("future-application-version");
                future.setBuildDate(new Date(4070908800000L));
                notices.sync(future);
                try (InstalledTestSite ignored = InstalledTestSite.open(root,
                        new ZrLogInstallAction(config, new File(root, "conf/install.lock")))) {
                    assertFalse(notices.getNotice().getUpgrade());
                    notices.sync(future);
                    var controller = (UpgradeController) Controller.buildController(
                            UpgradeController.class.getMethod("notice"),
                            sessionRequest(config, "/api/admin/upgrade/notice", "", new Cookie[0]), new SessionResponse().response());
                    assertTrue(controller.notice().getData().getUpgrade());
                    assertEquals("future-application-version", controller.notice().getData().getVersion().getBuildId());
                    assertTrue(services.get(MessageCenterService.class).listNotices()
                            .stream().anyMatch(notice -> "server.system.version-update".equals(notice.getTaskKey())));

                    notices.clearStoredUpgradeNotice();
                    config.getAllPlugins().add(updatePlugin(notices, future));
                    assertEquals("future-application-version", controller.notice().getData().getVersion().getBuildId());
                    config.getAllPlugins().clear();
                    var replacement = new Version();
                    replacement.setVersion("100.0.0");
                    replacement.setBuildId("replacement-version");
                    replacement.setBuildDate(future.getBuildDate());
                    config.getAllPlugins().add(updatePlugin(notices, replacement));
                    assertEquals("replacement-version", controller.notice().getData().getVersion().getBuildId());
                }
            } finally {
                config.stop();
            }
        });
    }

    @Test
    public void systemInfoCreatedBeforeInstallationReportsItsInstalledDatabaseWithoutGlobalConfig() throws Exception {
        withRuntime(root -> {
            ZrLogConfigImpl config = config();
            try {
                new ApplicationContext(config.getServerConfig()).init();
                var runtime = config.getServerConfig().getBindings().build().get(com.zrlog.admin.util.SystemInfoUtils.class);
                HttpRequest request = request(config, "/api/admin/system");
                assertNull(runtime.serverInfo(request).stream().filter(info -> "dbInfo".equals(info.getKey()))
                        .findFirst().orElseThrow().getValue());
                try (InstalledTestSite ignored = InstalledTestSite.open(root,
                        new ZrLogInstallAction(config, new File(root, "conf/install.lock")))) {
                    var controller = (AdminSystemController) Controller.buildController(
                            AdminSystemController.class.getMethod("index"), request, new SessionResponse().response());
                    var result = controller.index().getData();
                    var version = result.getServerInfos().stream().filter(info -> "dbInfo".equals(info.getKey()))
                            .findFirst().orElseThrow().getValue();
                    assertNotNull(version);
                    assertNotEquals("Unknown", version);
                    var pool = result.getServerInfos2().stream().filter(info -> "dbConnectSize".equals(info.getKey()))
                            .findFirst().orElseThrow().getValue();
                    assertTrue(pool, pool.matches("[0-9]+ / [0-9]+"));
                    assertFalse(result.getServerInfos2().stream().filter(info -> "uptime".equals(info.getKey()))
                            .findFirst().orElseThrow().getValue().isBlank());
                }
            } finally {
                config.stop();
            }
        });
    }

    @Test
    public void backendAndOauthFollowTheirApplicationAfterInstallationAndAddressChanges() throws Exception {
        withRuntime(root -> {
            ZrLogConfigImpl config = config();
            try {
                new ApplicationContext(config.getServerConfig()).init();
                var services = config.getServerConfig().getBindings().build();
                var backend = services.get(com.zrlog.admin.util.BackendServerUrl.class);
                var oauth = services.get(com.zrlog.admin.business.service.OAuthService.class);
                try (InstalledTestSite ignored = InstalledTestSite.open(root,
                        new ZrLogInstallAction(config, new File(root, "conf/install.lock")))) {
                    config.getServerConfig().setContextPath("/blog");
                    services.get(CacheService.class).getPublicWebSiteInfo().setHost("blog.example");
                    assertEquals("https://blog.example/blog", backend.configured());
                    assertEquals("https://blog.example/blog/oauth/token", oauth.metadata().token_endpoint);
                    assertTrue(oauth.isMcpEnabled());
                    new com.zrlog.model.WebSite().updateByKV("backend_server_url", "https://api.example/proxy/blog");
                    assertEquals("https://api.example/proxy/blog", oauth.issuer());
                    backend.requireAdminRequestOrigin(sessionRequest(config, "/api/admin/members", "", new Cookie[0],
                            Map.of("Origin", "https://api.example")));
                    assertThrows(IllegalArgumentException.class, () -> backend.requireAdminRequestOrigin(
                            sessionRequest(config, "/api/admin/members", "", new Cookie[0], Map.of("Origin", "https://attacker.example"))));
                    new com.zrlog.model.WebSite().updateByKV("backend_server_url", "");
                    services.get(CacheService.class).getPublicWebSiteInfo().setHost("");
                    config.getServerConfig().setPort(19123);
                    assertEquals("http://localhost:19123/blog/mcp", oauth.mcpResource());
                }
            } finally {
                config.stop();
            }
        });
    }

    @Test
    public void pageMetadataCreatedBeforeInstallationFollowsTheInstalledSiteWithoutGlobals() throws Exception {
        withRuntime(root -> {
            ZrLogConfigImpl config = config();
            var previousLanguage = com.zrlog.util.I18nUtil.threadLocal.get();
            var previousResources = com.zrlog.admin.business.AdminConstants.adminResource;
            try {
                var context = new ApplicationContext(config.getServerConfig());
                context.init();
                var language = (com.zrlog.web.inteceptor.MyI18nInterceptor) context.getInterceptors().stream()
                        .filter(com.zrlog.web.inteceptor.MyI18nInterceptor.class::isInstance).findFirst().orElseThrow();
                var services = config.getServerConfig().getBindings().build();
                var pages = services.get(com.zrlog.admin.business.service.AdminPageDataFactory.class);
                try (InstalledTestSite ignored = InstalledTestSite.open(root,
                        new ZrLogInstallAction(config, new File(root, "conf/install.lock")))) {
                    com.zrlog.admin.business.AdminConstants.adminResource = null;
                    com.zrlog.util.I18nUtil.removeI18n();
                    var site = services.get(CacheService.class).getPublicWebSiteInfo();
                    site.setTitle("Installed application");
                    site.setLanguage("en_US");
                    language.doInterceptor(request(config, "/api/admin/system"), new SessionResponse().response());
                    assertEquals("en_US", com.zrlog.util.I18nUtil.getCurrentLocale());
                    site.setLanguage("zh_CN");
                    language.doInterceptor(request(config, "/api/admin/system"), new SessionResponse().response());
                    assertEquals("zh_CN", com.zrlog.util.I18nUtil.getCurrentLocale());
                    site.setLanguage("en_US");
                    language.doInterceptor(request(config, "/api/admin/system"), new SessionResponse().response());
                    site.setSystem_notification("Scheduled maintenance");
                    var controller = (AdminSystemController) Controller.buildController(
                            AdminSystemController.class.getMethod("index"), request(config, "/api/admin/system"),
                            new SessionResponse().response());
                    var page = controller.index();
                    assertTrue(page.getDocumentTitle().contains("Installed application"));
                    assertTrue(page.getDocumentTitle().contains("System"));
                    assertEquals(config.getWebSetup(com.zrlog.admin.business.service.AdminResourceProvider.class)
                            .getAdminResource().getStaticResourceBuildId(), page.getPageBuildId());
                    var json = new com.google.gson.Gson().toJsonTree(page).getAsJsonObject();
                    assertEquals("Scheduled maintenance", json.get("systemNotification").getAsString());
                    site.setHost("public.example");
                    new com.zrlog.model.WebSite().updateByKV("admin_static_resource_base_url", "https://cdn.example");
                    var resourceInfo = services.get(com.zrlog.admin.business.service.AdminResourceService.class)
                            .adminResourceInfo(request(config, "/api/public/adminResource"));
                    assertEquals("//public.example/", resourceInfo.getHomeUrl());
                    assertEquals("en_US", resourceInfo.getLang());
                    assertEquals("https://cdn.example", resourceInfo.getAdmin_static_resource_base_url());
                    var manifest = (com.zrlog.admin.web.controller.page.AdminManifestController) Controller.buildController(
                            com.zrlog.admin.web.controller.page.AdminManifestController.class.getMethod("manifest"),
                            request(config, "/api/admin/manifest"), new SessionResponse().response());
                    assertEquals("Installed application", manifest.manifest().getShort_name());
                    site.setTitle("Renamed application");
                    assertTrue(pages.response(null, "", "/admin/unknown").getDocumentTitle().contains("Renamed application"));
                }
            } finally {
                config.stop();
                com.zrlog.admin.business.AdminConstants.adminResource = previousResources;
                if (previousLanguage == null) com.zrlog.util.I18nUtil.removeI18n();
                else com.zrlog.util.I18nUtil.threadLocal.set(previousLanguage);
            }
        });
    }

    @Test
    public void publicBlogControllerUsesLiveInstalledSiteWithoutGlobalConfig() throws Exception {
        withRuntime(root -> {
            ZrLogConfigImpl config = config();
            var previousLanguage = com.zrlog.util.I18nUtil.threadLocal.get();
            try {
                var context = new ApplicationContext(config.getServerConfig());
                context.init();
                var services = config.getServerConfig().getBindings().build();
                var common = services.get(com.zrlog.business.service.CommonService.class);
                try (InstalledTestSite ignored = InstalledTestSite.open(root,
                        new ZrLogInstallAction(config, new File(root, "conf/install.lock")))) {
                    var site = services.get(CacheService.class).getPublicWebSiteInfo();
                    site.setTitle("Public installed site");
                    site.setHost("blog.example");
                    var language = new com.zrlog.common.vo.I18nVO();
                    language.setLocale("en_US");
                    language.getBlog().put("en_US", new java.util.HashMap<>(Map.of("comment", "Comment")));
                    com.zrlog.util.I18nUtil.threadLocal.set(language);
                    var controller = (com.zrlog.blog.web.controller.api.BlogApiPublicController) Controller.buildController(
                            com.zrlog.blog.web.controller.api.BlogApiPublicController.class.getMethod("blogResource"),
                            request(config, "/api/public/blogResource"), new SessionResponse().response());
                    var first = controller.blogResource().getData();
                    assertEquals("Public installed site", first.get("websiteTitle"));
                    assertEquals("//blog.example/", first.get("homeUrl"));
                    site.setTitle("Renamed public site");
                    site.setHost("renamed.example");
                    var second = controller.blogResource().getData();
                    assertEquals("Renamed public site", second.get("websiteTitle"));
                    assertEquals("//renamed.example/", second.get("homeUrl"));
                    assertEquals("Public installed site", first.get("websiteTitle"));
                    assertEquals(Map.of("comment", "Comment"), language.getBlog().get("en_US"));
                    site.setGenerator_html_status(true);
                    var categories = (com.zrlog.admin.web.controller.api.TypeController) Controller.buildController(
                            com.zrlog.admin.web.controller.api.TypeController.class.getMethod("index"),
                            request(config, "/api/admin/type"), new SessionResponse().response());
                    var types = categories.index().getData().getRows();
                    assertFalse(types.isEmpty());
                    assertTrue(types.get(0).getUrl().startsWith("//renamed.example/sort/"));
                    assertTrue(types.get(0).getUrl().endsWith(".html"));
                    new com.zrlog.model.Tag().set("text", "injected").set("count", 1).save();
                    var tags = (com.zrlog.admin.web.controller.api.AdminTagController) Controller.buildController(
                            com.zrlog.admin.web.controller.api.AdminTagController.class.getMethod("index"),
                            request(config, "/api/admin/tag"), new SessionResponse().response());
                    assertTrue(tags.index().getData().getRows().stream()
                            .anyMatch(tag -> "//renamed.example/tag/injected.html".equals(tag.getUrl())));
                    site.setGenerator_html_status(false);
                    assertTrue(tags.index().getData().getRows().stream()
                            .anyMatch(tag -> "//renamed.example/tag/injected".equals(tag.getUrl())));
                    assertSame(common, services.get(com.zrlog.business.service.CommonService.class));
                }
            } finally {
                config.stop();
                if (previousLanguage == null) com.zrlog.util.I18nUtil.removeI18n();
                else com.zrlog.util.I18nUtil.threadLocal.set(previousLanguage);
            }
        });
    }

    @Test
    public void staticPublishersCreatedBeforeInstallationExportWithoutGlobalConfig() throws Exception {
        withRuntime(root -> {
            ZrLogConfigImpl config = config();
            java.util.List<com.zrlog.business.plugin.StaticSitePlugin> publishers = new java.util.ArrayList<>();
            try {
                new ApplicationContext(config.getServerConfig()).init();
                for (var module : java.util.List.of(config.getWebSetup(com.zrlog.blog.web.BlogWebSetup.class),
                        config.getWebSetup(com.zrlog.admin.web.AdminUiWebSetup.class))) {
                    for (var plugin : module.getPlugins()) {
                        if (plugin instanceof com.zrlog.business.plugin.StaticSitePlugin) {
                            publishers.add((com.zrlog.business.plugin.StaticSitePlugin) plugin);
                        }
                    }
                }
                assertEquals(2, publishers.size());
                assertTrue(publishers.stream().allMatch(com.zrlog.business.plugin.StaticSitePlugin::isDisabled));
                try (InstalledTestSite ignored = InstalledTestSite.open(root,
                        new ZrLogInstallAction(config, new File(root, "conf/install.lock")))) {
                    config.getAllPlugins().add(plugin("static-test", new AtomicReference<>()));
                    var site = config.getServerConfig().getBindings().build().get(CacheService.class).getPublicWebSiteInfo();
                    site.setHost("static.example");
                    site.setGenerator_html_status(true);
                    for (var publisher : publishers) {
                        assertFalse(publisher.isDisabled());
                        assertSame(site, publisher.getSiteInfo());
                        assertTrue(publisher.start());
                        assertTrue(publisher.getCacheFile(publisher.getVersionFileName()).isFile());
                        assertFalse(publisher.getCacheFiles().isEmpty());
                        assertTrue(publisher.getHandleStatusPageMap().values().stream()
                                .allMatch(state -> state == com.zrlog.business.plugin.StaticSitePlugin.HandleState.HANDLED));
                    }
                    var blog = publishers.stream().filter(com.zrlog.blog.web.plugin.BlogPageStaticSitePlugin.class::isInstance)
                            .findFirst().orElseThrow();
                    assertTrue(blog.getCacheFile("/index.html").isFile());
                    assertTrue(java.nio.file.Files.readString(blog.getCacheFile("/hello-world.html").toPath())
                            .contains("static plugin content"));
                    assertTrue(blog.getCacheFile("/error/404.html").isFile());
                    site.setGenerator_html_status(false);
                    assertTrue(publishers.stream().allMatch(com.zrlog.business.plugin.StaticSitePlugin::isDisabled));
                }
            } finally {
                for (var publisher : publishers) {
                    ((java.util.concurrent.ExecutorService) publisher.getExecutorService()).shutdownNow();
                }
                config.stop();
                com.zrlog.util.I18nUtil.removeI18n();
            }
        });
    }

    @Test
    public void passkeyOriginCreatedBeforeInstallationFollowsSiteHostWithoutGlobalConfig() throws Exception {
        withRuntime(root -> {
            ZrLogConfigImpl config = config();
            try {
                new ApplicationContext(config.getServerConfig()).init();
                var services = config.getServerConfig().getBindings().build();
                var passkeys = services.get(com.zrlog.admin.business.service.PasskeyService.class);
                try (InstalledTestSite ignored = InstalledTestSite.open(root,
                        new ZrLogInstallAction(config, new File(root, "conf/install.lock")))) {
                    var site = services.get(CacheService.class).getPublicWebSiteInfo();
                    site.setHost("localhost:18080");
                    HttpRequest original = sessionRequest(config, "/api/admin/passkey/authentication/options", "", new Cookie[0],
                            Map.of("Host", "localhost:28080", "Origin", "http://localhost:18080"));
                    var session = (AccountSessionController) Controller.buildController(
                            AccountSessionController.class.getMethod("passkeyAuthenticationOptions"), original,
                            new SessionResponse().response());
                    var challenge = session.passkeyAuthenticationOptions().getData();
                    assertEquals("localhost", challenge.getOptions().getRpId());
                    assertFalse(challenge.getOptions().getChallenge().isBlank());
                    assertFalse(challenge.getRequestId().isBlank());
                    assertThrows(com.zrlog.admin.business.exception.PasskeyVerificationException.class,
                            () -> passkeys.startAuthentication(sessionRequest(config, "/api/admin/passkey/authentication/options", "", new Cookie[0],
                                    Map.of("Host", "localhost:28080", "Origin", "https://attacker.example"))));
                    site.setHost("localhost:19090");
                    assertThrows(com.zrlog.admin.business.exception.PasskeyVerificationException.class, session::passkeyAuthenticationOptions);
                    var updated = passkeys.startAuthentication(sessionRequest(config, "/api/admin/passkey/authentication/options", "", new Cookie[0],
                            Map.of("Host", "localhost:28080", "Origin", "http://localhost:19090")));
                    assertEquals("localhost", updated.getOptions().getRpId());
                    assertNotEquals(challenge.getRequestId(), updated.getRequestId());
                }
            } finally {
                config.stop();
            }
        });
    }

    private UpdateVersionInfoPlugin updatePlugin(
            UpgradeNoticeService notices, Version version) {
        return new UpdateVersionInfoPlugin(notices, () -> "en_US", null) {
            @Override public Version getLastVersion(boolean fetch) { return version; }
        };
    }

    private PluginCorePlugin plugin(String token, AtomicReference<String> forwarded) {
        return (PluginCorePlugin) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{PluginCorePlugin.class},
                (proxy, method, args) -> {
                    switch (method.getName()) {
                        case "getToken": return token;
                        case "getContext":
                            var handle = new com.hibegin.common.util.http.handle.CloseResponseHandle();
                            var pluginResponse = (java.net.http.HttpResponse<java.io.InputStream>) Proxy.newProxyInstance(
                                    getClass().getClassLoader(), new Class[]{java.net.http.HttpResponse.class},
                                    (p, m, a) -> {
                                        if (m.getName().equals("statusCode")) return 200;
                                        if (m.getName().equals("body")) return new java.io.ByteArrayInputStream(
                                                "<div>static plugin content</div>".getBytes(java.nio.charset.StandardCharsets.UTF_8));
                                        return null;
                                    });
                            handle.handle(null, pluginResponse);
                            return handle;
                        case "accessPlugin": forwarded.set((String) args[0]); return true;
                        case "requestService":
                            UploadServiceResponseEntity uploaded = new UploadServiceResponseEntity();
                            uploaded.setUrl("https://files.example/cover.png");
                            return new UploadServiceResponseEntity[]{uploaded};
                        default: return method.getReturnType() == boolean.class ? false : null;
                    }
                });
    }

    private HttpRequest sessionRequest(ZrLogConfigImpl config, String uri, String body, Cookie[] cookies) {
        return sessionRequest(config, uri, body, cookies, Collections.emptyMap());
    }

    private HttpRequest sessionRequest(ZrLogConfigImpl config, String uri, String body, Cookie[] cookies,
                                       Map<String, String> headers) {
        var input = new ByteArrayInputStream(body.getBytes(StandardCharsets.UTF_8));
        var attributes = new HashMap<String, Object>();
        return (HttpRequest) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{HttpRequest.class},
                (proxy, method, args) -> {
                    switch (method.getName()) {
                        case "getServerConfig": return config.getServerConfig();
                        case "getRequestConfig": return config.getRequestConfig();
                        case "getContextPath": return config.getServerConfig().getContextPath();
                        case "getUri": return uri;
                        case "getMethod": return body.isEmpty() ? HttpMethod.GET : HttpMethod.POST;
                        case "getInputStream": return input;
                        case "getCookies": return cookies;
                        case "getHeader": return headers.get(args[0]);
                        case "getHeaderMap": return headers;
                        case "getParamMap": return Collections.emptyMap();
                        case "getAttr": return attributes;
                        case "getRemoteAddr": return "127.0.0.1";
                        case "getScheme": return "http";
                        default: return null;
                    }
                });
    }

    private static final class SessionResponse {
        private final List<Cookie> cookies = new ArrayList<>();
        private String redirect;
        private Object json;

        HttpResponse response() {
            return (HttpResponse) Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{HttpResponse.class},
                    (proxy, method, args) -> {
                        if (method.getName().equals("addCookie")) cookies.add((Cookie) args[0]);
                        if (method.getName().equals("redirect")) redirect = args[0].toString();
                        if (method.getName().equals("renderJson")) json = args[0];
                        return null;
                    });
        }
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
                        case "getContextPath": return config.getServerConfig().getContextPath();
                        case "getUri": return uri;
                        case "decodeParamMap": return Collections.emptyMap();
                        case "getParaToInt": return args.length > 1 ? args[1] : null;
                        case "getParaToStr": return args.length > 1 ? args[1] : null;
                        case "getHeaderMap": return Collections.emptyMap();
                        case "getRemoteAddr": return "127.0.0.1";
                        default: return null;
                    }
                });
    }

    private void withRuntime(RuntimeTest test) throws Exception {
        String oldRoot = System.getProperty("sws.root.path");
        String oldEnv = System.getProperty("env");
        var oldResource = AdminConstants.adminResource;
        var oldDataSource = DAO.getDefaultDataSource();
        File root = temporaryFolder.newFolder();
        try {
            System.setProperty("sws.root.path", root.getAbsolutePath());
            System.setProperty("env", "junit-test");
            test.run(root);
        } finally {
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
