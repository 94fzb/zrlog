package com.zrlog.admin.business.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hibegin.common.util.IOUtil;
import com.hibegin.common.util.SecurityUtils;
import com.hibegin.common.util.StringUtils;
import com.hibegin.http.server.api.HttpRequest;
import com.zrlog.business.util.ResourceUtils;
import com.zrlog.common.AdminResource;
import com.zrlog.common.CacheService;
import com.zrlog.common.Constants;
import com.zrlog.util.I18nUtil;
import com.zrlog.util.ZrLogUtil;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

public class AdminResourceImpl implements AdminResource {

    public static final String ADMIN_ASSET_MANIFEST_JSON = "/admin/asset-manifest.json";

    private final Set<String> pageUris;
    private final Set<String> staticUris;
    private final Set<String> apiUris;
    private final long fileBuildId;
    private final CacheService cacheService;

    public AdminResourceImpl(CacheService cacheService) {
        this.cacheService = cacheService;
        this.pageUris = wrapperUris(getUris("/admin/pwa-page.txt"));
        this.apiUris = getUris("/conf/pwa-api.txt");
        this.staticUris = getStaticUri();
        Map<String, Object> resourceMap = new TreeMap<>();
        resourceMap.put("uris", pageUris);
        resourceMap.put("static", staticUris);
        resourceMap.put("i18n", I18nUtil.getAdmin());
        this.fileBuildId = Math.abs(SecurityUtils.md5(new Gson().toJson(resourceMap)).hashCode());
    }

    public static void main(String[] args) {
        System.out.println("js = " + IOUtil.getStringInputStream(new AdminResourceImpl(null).renderServiceWorker(null)));
        Set<String> adminPageUris = new AdminResourceImpl(null).getAdminPageUris();
        System.out.println(adminPageUris);
    }

    private Set<String> getStaticUri() {
        InputStream resourceAsStream = ResourceUtils.class.getResourceAsStream(ADMIN_ASSET_MANIFEST_JSON);
        Set<String> cacheUris = new LinkedHashSet<>();
        if (Objects.isNull(resourceAsStream)) {
            return cacheUris;
        }
        String str = IOUtil.getStringInputStream(resourceAsStream);
        if (StringUtils.isNotEmpty(str)) {
            Map<String, Object> map = new Gson().fromJson(str, new TypeToken<>() {
            });
            Map<String, Object> staticFiles = (Map<String, Object>) map.get("files");
            if (Objects.nonNull(staticFiles)) {
                cacheUris.addAll(staticFiles.values().stream().filter(e -> ((String) e).endsWith(".js")).map(String::valueOf).collect(Collectors.toList()));
                cacheUris = new LinkedHashSet<>(wrapperUris(cacheUris));
            }
        }
        cacheUris.addAll(wrapperUris(getUris("/admin/pwa-resource.txt")));
        return cacheUris;
    }

    @Override
    public boolean isAdminMainJs(String uri) {
        return uri.contains("/admin/static/js/main.") && uri.endsWith(".js");
    }

    private String buildRealPageUrl(String e, String adminResourceUrl, HttpRequest request) {
        StringBuilder sb = new StringBuilder();
        String[] split = e.split("\\?");
        if (split.length == 1) {
            sb.append(adminResourceUrl).append(e);
        } else {
            sb.append(adminResourceUrl).append(split[0]);
        }
        if (e.endsWith("/?pwa=true") || e.endsWith("/")) {
            sb.append("?");
        } else {
            sb.append(ZrLogUtil.isStaticPlugin(request) ? ".html?" : "?");
        }
        if (e.contains("?pwa=true")) {
            sb.append(split[1]);
        } else {
            sb.append("v=").append(fileBuildId);
            if (split.length > 1) {
                sb.append("&");
                sb.append(split[1]);
            }
        }
        return sb.toString();
    }

    @Override
    public ByteArrayInputStream renderServiceWorker(HttpRequest request) {
        List<String> realUris = new ArrayList<>();
        String adminResourceUrl = ZrLogUtil.getAdminStaticResourceBaseUrlByWebSite(request);
        pageUris.forEach(uri -> realUris.add(buildRealPageUrl(uri, adminResourceUrl, request)));
        staticUris.forEach(uri -> realUris.add(adminResourceUrl + uri));
        String newUrls = "const urlsToCache = " + new Gson().newBuilder().disableHtmlEscaping().setPrettyPrinting().create().toJson(realUris);
        return new ByteArrayInputStream(IOUtil.getStringInputStream(ResourceUtils.class.getResourceAsStream(Constants.ADMIN_SERVICE_WORKER_JS)).replace("const urlsToCache = []", newUrls).getBytes());
    }

    @Override
    public String getStaticResourceBuildId() {
        if (Objects.isNull(cacheService)) {
            return Math.abs(fileBuildId) + "";
        }
        return Math.abs(fileBuildId + cacheService.getWebSiteVersion()) + "";
    }

    private Set<String> getUris(String resourceName) {
        InputStream textIn = ResourceUtils.class.getResourceAsStream(resourceName);
        if (Objects.isNull(textIn)) {
            return new HashSet<>();
        }
        String strVendors = IOUtil.getStringInputStream(textIn);
        return new LinkedHashSet<>(Arrays.asList(strVendors.split("\n")));
    }

    @Override
    public String getContextPath() {
        return Objects.nonNull(Constants.zrLogConfig) ? Constants.zrLogConfig.getContextPath() + "/" : "/";
    }

    private Set<String> wrapperUris(Set<String> uris) {
        Set<String> cacheUris = new LinkedHashSet<>();
        uris.forEach(file -> {
            if (file.startsWith("/admin")) {
                cacheUris.add((getContextPath() + file.substring(1)));
            } else if (file.startsWith("admin/")) {
                cacheUris.add((getContextPath() + file));
            } else {
                //vendors
                cacheUris.add(new File((getContextPath() + "admin/" + file)).toString());
            }
        });
        return cacheUris;
    }

    @Override
    public Set<String> getAdminStaticResourceUris() {
        return staticUris;
    }

    @Override
    public Set<String> getAdminPageUris() {
        return pageUris.stream().filter(e -> e.startsWith("/admin/")).map(e -> e.split("\\?")[0]).collect(Collectors.toSet());
    }

    @Override
    public Set<String> getAdminCacheableApiUris() {
        return apiUris;
    }

}
