package com.zrlog.business.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hibegin.common.util.IOUtil;
import com.hibegin.common.util.StringUtils;
import com.zrlog.common.Constants;
import com.zrlog.util.ZrLogUtil;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

public class ResourceUtils {

    public static final String ADMIN_ASSET_MANIFEST_JSON = "/admin/asset-manifest.json";
    public static final String ADMIN_SERVICE_WORKER_JS = "/admin/service-worker.js";

    public static boolean isAdminMainJs(String uri) {
        return uri.startsWith("/admin/static/js/main.") && uri.endsWith(".js");
    }

    public static ByteArrayInputStream renderServiceWorker() {
        StringJoiner sb = new StringJoiner(",\n    ");
        getAdminStaticResourceUris().forEach(e -> {
            if (StringUtils.isNotEmpty(ZrLogUtil.getAdminStaticResourceBaseUrlByWebSite())) {
                sb.add("'" + ZrLogUtil.getAdminStaticResourceBaseUrlByWebSite() + e + "'");
            } else {
                sb.add("'" + e + "'");
            }
        });
        return new ByteArrayInputStream(IOUtil.getStringInputStream(ResourceUtils.class.getResourceAsStream(ADMIN_SERVICE_WORKER_JS)).replace("'___FILES___'", sb.toString()).getBytes());
    }

    public static Set<String> getAdminStaticResourceUris() {
        Set<String> cacheUris = new LinkedHashSet<>();
        InputStream resourceAsStream = ResourceUtils.class.getResourceAsStream(ADMIN_ASSET_MANIFEST_JSON);
        if (Objects.nonNull(resourceAsStream)) {
            String str = IOUtil.getStringInputStream(resourceAsStream);
            InputStream textIn = ResourceUtils.class.getResourceAsStream("/admin/pwa-resource.txt");
            if (Objects.isNull(textIn)) {
                return cacheUris;
            }
            String strVendors = IOUtil.getStringInputStream(textIn);
            if (StringUtils.isNotEmpty(str)) {
                Map<String, Object> map = new Gson().fromJson(str, new TypeToken<>() {
                });
                Map<String, Object> staticFiles = (Map<String, Object>) map.get("files");
                if (Objects.nonNull(staticFiles)) {
                    cacheUris.addAll(staticFiles.values().stream().filter(e -> ((String) e).endsWith(".js")).map(e -> Constants.zrLogConfig.getContextPath() +  ((String) e).replace("./", "/")).collect(Collectors.toList()));
                }
            }
            if (StringUtils.isNotEmpty(strVendors)) {
                for (String file : strVendors.split("\n")) {
                    if (file.startsWith("/admin") || file.startsWith("api/") || file.startsWith("admin/")) {
                        cacheUris.add(Constants.zrLogConfig.getContextPath() + "/" + file);
                    } else {
                        cacheUris.add(Constants.zrLogConfig.getContextPath() + "/admin/" + file);
                    }
                }
            }
        }
        return cacheUris;
    }

    public static void main(String[] args) {
        System.out.println("js = " + IOUtil.getStringInputStream(ResourceUtils.renderServiceWorker()));
    }

}
