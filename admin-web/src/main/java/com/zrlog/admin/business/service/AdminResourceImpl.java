package com.zrlog.admin.business.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hibegin.common.util.IOUtil;
import com.hibegin.common.util.StringUtils;
import com.zrlog.business.util.ResourceUtils;
import com.zrlog.common.AdminResource;
import com.zrlog.common.Constants;
import com.zrlog.util.ZrLogUtil;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

public class AdminResourceImpl implements AdminResource {

    public static final String ADMIN_ASSET_MANIFEST_JSON = "/admin/asset-manifest.json";
    public static final String ADMIN_SERVICE_WORKER_JS = "/admin/service-worker.js";

    @Override
    public boolean isAdminMainJs(String uri) {
        return uri.startsWith("/admin/static/js/main.") && uri.endsWith(".js");
    }

    @Override
    public ByteArrayInputStream renderServiceWorker() {
        StringJoiner sb = new StringJoiner(",\n    ");
        getAdminResourceUris(false).forEach(e -> {
            if (StringUtils.isNotEmpty(ZrLogUtil.getAdminStaticResourceBaseUrlByWebSite())) {
                sb.add("'" + ZrLogUtil.getAdminStaticResourceBaseUrlByWebSite() + e + "'");
            } else {
                sb.add("'" + e + "'");
            }
        });
        return new ByteArrayInputStream(IOUtil.getStringInputStream(ResourceUtils.class.getResourceAsStream(ADMIN_SERVICE_WORKER_JS)).replace("'___FILES___'", sb.toString()).getBytes());
    }

    private Set<String> getUris(String resourceName) {
        InputStream textIn = ResourceUtils.class.getResourceAsStream(resourceName);
        if (Objects.isNull(textIn)) {
            return new HashSet<>();
        }
        String strVendors = IOUtil.getStringInputStream(textIn);
        return new HashSet<>(Arrays.asList(strVendors.split("\n")));
    }

    private String getContextPath() {
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
        InputStream resourceAsStream = ResourceUtils.class.getResourceAsStream(ADMIN_ASSET_MANIFEST_JSON);
        if (Objects.nonNull(resourceAsStream)) {
            String str = IOUtil.getStringInputStream(resourceAsStream);
            if (StringUtils.isNotEmpty(str)) {
                Map<String, Object> map = new Gson().fromJson(str, new TypeToken<>() {
                });
                Map<String, Object> staticFiles = (Map<String, Object>) map.get("files");
                if (Objects.nonNull(staticFiles)) {
                    cacheUris.addAll(staticFiles.values().stream().filter(e -> ((String) e).endsWith(".js")).map(String::valueOf).collect(Collectors.toList()));
                }
            }
            if (!requiredStaticFiles) {
                cacheUris.addAll(getUris("/admin/pwa-page.txt"));
            }
            cacheUris.addAll(getUris("/admin/pwa-resource.txt"));
        }
        return wrapperUris(cacheUris);
    }

    public static void main(String[] args) {
        System.out.println("js = " + IOUtil.getStringInputStream(new AdminResourceImpl().renderServiceWorker()));
    }

}
