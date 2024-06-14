package com.zrlog.admin.web.interceptor;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hibegin.common.util.IOUtil;
import com.hibegin.common.util.StringUtils;
import com.hibegin.http.server.api.HandleAbleInterceptor;
import com.hibegin.http.server.api.HttpRequest;
import com.hibegin.http.server.api.HttpResponse;
import com.hibegin.http.server.util.MimeTypeUtil;
import com.hibegin.http.server.web.Controller;
import com.zrlog.common.Constants;

import java.io.ByteArrayInputStream;
import java.lang.reflect.Method;
import java.util.*;

/**
 * 不检查 pwa 的 json 文件的请求
 */
public class AdminPwaInterceptor implements HandleAbleInterceptor {

    private final List<String> resourceUris = new ArrayList<>();
    private static final String ADMIN_SERVICE_WORKER_JS = "/admin/service-worker.js";
    private static final String ADMIN_ASSET_MANIFEST_JSON = "/admin/asset-manifest.json";
    private static final Set<String> files = new LinkedHashSet<>();

    static {
        String str = IOUtil.getStringInputStream(AdminPwaInterceptor.class.getResourceAsStream(ADMIN_ASSET_MANIFEST_JSON));
        String strVendors = IOUtil.getStringInputStream(AdminPwaInterceptor.class.getResourceAsStream("/admin/vendors-resource.txt"));
        if (StringUtils.isNotEmpty(str)) {
            Map<String, Object> map = new Gson().fromJson(str, new TypeToken<>() {
            });
            Map<String, Object> staticFiles = (Map<String, Object>) map.get("files");
            if (Objects.nonNull(staticFiles)) {
                files.addAll(staticFiles.values().stream().filter(e -> ((String) e).endsWith(".js")).map(e -> ((String) e).replace("./", "/")).toList());
            }
        }
        if (StringUtils.isNotEmpty(strVendors)) {
            for (String file : strVendors.split("\n")) {
                files.add("/admin/" + file);
            }
        }
    }

    public static void main(String[] args) {
        System.out.println("js = " + IOUtil.getStringInputStream(renderServiceWorker()));
    }

    public AdminPwaInterceptor() {
        resourceUris.add(ADMIN_SERVICE_WORKER_JS);
        resourceUris.add(ADMIN_ASSET_MANIFEST_JSON);
    }

    @Override
    public boolean isHandleAble(HttpRequest request) {
        if (resourceUris.contains(request.getUri())) {
            return true;
        }

        return Constants.ADMIN_PWA_MANIFEST_JSON.equals(request.getUri()) || Constants.ADMIN_PWA_MANIFEST_API_URI_PATH.equals(request.getUri());
    }

    private static ByteArrayInputStream renderServiceWorker() {
        StringJoiner sb = new StringJoiner(",\n    ");
        files.forEach(e -> {
            sb.add("'" + e + "'");
        });
        return new ByteArrayInputStream(IOUtil.getStringInputStream(AdminPwaInterceptor.class.getResourceAsStream(ADMIN_SERVICE_WORKER_JS)).replace("'___FILES___'", sb.toString()).getBytes());
    }

    @Override
    public boolean doInterceptor(HttpRequest request, HttpResponse response) throws Exception {
        if (Objects.equals(ADMIN_SERVICE_WORKER_JS, request.getUri())) {
            response.addHeader("Content-Type", MimeTypeUtil.getMimeStrByExt(request.getUri().substring(request.getUri().lastIndexOf(".") + 1)));
            response.write(renderServiceWorker());
            return false;
        }
        if (resourceUris.contains(request.getUri())) {
            response.addHeader("Content-Type", MimeTypeUtil.getMimeStrByExt(request.getUri().substring(request.getUri().lastIndexOf(".") + 1)));
            response.write(AdminPwaInterceptor.class.getResourceAsStream(request.getUri()));
            return false;
        }
        if (Constants.ADMIN_PWA_MANIFEST_JSON.equals(request.getUri()) || Constants.ADMIN_PWA_MANIFEST_API_URI_PATH.equals(request.getUri())) {
            Method method = request.getServerConfig().getRouter().getMethod(Constants.ADMIN_PWA_MANIFEST_API_URI_PATH);
            Object responseJson = method.invoke(Controller.buildController(method, request, response));
            if (responseJson != null) {
                response.renderJson(responseJson);
                return false;
            }
        }
        response.renderCode(404);
        return false;
    }
}
