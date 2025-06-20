package com.zrlog.admin.business.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hibegin.common.util.IOUtil;
import com.hibegin.common.util.SecurityUtils;
import com.hibegin.common.util.StringUtils;
import com.hibegin.http.server.api.HttpRequest;
import com.zrlog.business.util.ResourceUtils;
import com.zrlog.common.AdminResource;
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
    private final long buildId;

    public AdminResourceImpl() {
        this.pageUris = wrapperUris(getUris("/admin/pwa-page.txt"));
        InputStream resourceAsStream = ResourceUtils.class.getResourceAsStream(ADMIN_ASSET_MANIFEST_JSON);
        Set<String> cacheUris = new LinkedHashSet<>();
        if (Objects.nonNull(resourceAsStream)) {

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
        }
        this.staticUris = cacheUris;
        Map<String, Object> resourceMap = new TreeMap<>();
        resourceMap.put("uris", pageUris);
        resourceMap.put("static", staticUris);
        resourceMap.put("i18n", I18nUtil.getAdmin());
        this.buildId = Math.abs(SecurityUtils.md5(new Gson().toJson(resourceMap)).hashCode());
    }

    public static void main(String[] args) {
        System.out.println("js = " + IOUtil.getStringInputStream(new AdminResourceImpl().renderServiceWorker(null)));
        //Set<String> adminPageUris = new AdminResourceImpl().getAdminPageUris();
        // System.out.println(adminPageUris);
    }

    @Override
    public boolean isAdminMainJs(String uri) {
        return uri.contains("/admin/static/js/main.") && uri.endsWith(".js");
    }

    @Override
    public ByteArrayInputStream renderServiceWorker(HttpRequest request) {
        StringJoiner sj = new StringJoiner(",\n    ");
        String adminResourceUrl = ZrLogUtil.getAdminStaticResourceBaseUrlByWebSite();
        getAdminResourceUris(false).forEach(e -> {
            if (e.startsWith("/api")) {
                return;
            }
            if (pageUris.contains(e)) {
                if (!ZrLogUtil.isStaticPlugin(request)) {
                    StringBuilder sb = new StringBuilder();
                    String[] split = e.split("\\?");
                    if (split.length == 1) {
                        sb.append("\"").append(e).append(".html?");
                    } else {
                        sb.append("\"").append(split[0]).append(".html?");
                    }
                    sb.append("v=").append(buildId);
                    if (split.length > 1) {
                        sb.append("&");
                        sb.append(split[1]);
                    }
                    sb.append("\"");
                    sj.add(sb.toString());
                } else {
                    sj.add("\"" + e + "\"");
                }
            } else {
                if (StringUtils.isNotEmpty(adminResourceUrl) && !ZrLogUtil.isStaticPlugin(request)) {
                    sj.add("\"" + adminResourceUrl + e + "\"");
                } else {
                    sj.add("\"" + e + "\"");
                }
            }
        });
        return new ByteArrayInputStream(IOUtil.getStringInputStream(ResourceUtils.class.getResourceAsStream(Constants.ADMIN_SERVICE_WORKER_JS)).replace("'___FILES___'", sj.toString()).getBytes());
    }

    @Override
    public String getStaticResourceBuildId() {
        return buildId + "";
    }

    private Set<String> getUris(String resourceName) {
        InputStream textIn = ResourceUtils.class.getResourceAsStream(resourceName);
        if (Objects.isNull(textIn)) {
            return new HashSet<>();
        }
        String strVendors = IOUtil.getStringInputStream(textIn);
        return new HashSet<>(Arrays.asList(strVendors.split("\n")));
    }

    @Override
    public String getContextPath() {
        return Objects.nonNull(Constants.zrLogConfig) ? Constants.zrLogConfig.getContextPath() + "/" : "/";
    }

    private Set<String> wrapperUris(Set<String> uris) {
        Set<String> cacheUris = new LinkedHashSet<>();
        uris.forEach(file -> {
            if (file.startsWith("/admin") || file.startsWith("api/") || file.startsWith("admin/")) {
                cacheUris.add((getContextPath() + file));
            } else if (file.startsWith("./")) {
                cacheUris.add(getContextPath() + file.substring(2));
            } else {
                //vendors
                cacheUris.add(new File((getContextPath() + "admin/" + file)).toString());
            }
        });
        return cacheUris;
    }

    @Override
    public Set<String> getAdminResourceUris(boolean requiredStaticFiles) {
        Set<String> cacheUris = new LinkedHashSet<>();
        if (!requiredStaticFiles) {
            cacheUris.addAll(pageUris);
        }
        cacheUris.addAll(staticUris);
        return cacheUris;
    }

    @Override
    public Set<String> getAdminPageUris() {
        return pageUris.stream().filter(e -> e.startsWith("/admin/")).map(e -> e.split("\\?")[0]).collect(Collectors.toSet());
    }

    @Override
    public Set<String> getAdminCacheableApiUris() {
        return pageUris.stream().filter(e -> e.startsWith("/api")).collect(Collectors.toSet());
    }

}
