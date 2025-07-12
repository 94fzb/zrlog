package com.zrlog.admin.web.interceptor;

import com.hibegin.http.server.api.HandleAbleInterceptor;
import com.hibegin.http.server.api.HttpRequest;
import com.hibegin.http.server.api.HttpResponse;
import com.hibegin.http.server.config.GsonHttpJsonMessageConverter;
import com.hibegin.http.server.util.MimeTypeUtil;
import com.hibegin.http.server.web.Controller;
import com.zrlog.admin.business.AdminConstants;
import com.zrlog.admin.business.service.AdminResourceImpl;
import com.zrlog.business.plugin.StaticSitePlugin;
import com.zrlog.business.service.TemplateInfoHelper;
import com.zrlog.common.Constants;
import com.zrlog.common.vo.TemplateVO;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 不检查 pwa 的 json 文件的请求
 */
public class AdminPwaInterceptor implements HandleAbleInterceptor {

    private final List<String> resourceUris = new ArrayList<>();

    public AdminPwaInterceptor() {
        resourceUris.add(AdminResourceImpl.ADMIN_ASSET_MANIFEST_JSON);
        TemplateVO defaultTemplateVO = TemplateInfoHelper.getDefaultTemplateVO();
        if (Objects.nonNull(defaultTemplateVO)) {
            resourceUris.add(defaultTemplateVO.getAdminPreviewImage());
        }
        resourceUris.add(Constants.ADMIN_SERVICE_WORKER_JS);
    }

    @Override
    public boolean isHandleAble(HttpRequest request) {
        if (resourceUris.contains(request.getUri())) {
            return true;
        }
        return Constants.ADMIN_PWA_MANIFEST_JSON.equals(request.getUri()) || AdminConstants.ADMIN_PWA_MANIFEST_API_URI_PATH.equals(request.getUri());
    }

    @Override
    public boolean doInterceptor(HttpRequest request, HttpResponse response) throws Exception {
        byte[] bytes = null;
        try {
            if (Objects.equals(Constants.ADMIN_SERVICE_WORKER_JS, request.getUri())) {
                response.addHeader("Content-Type", MimeTypeUtil.getMimeStrByExt(request.getUri().substring(request.getUri().lastIndexOf(".") + 1)));
                try (InputStream inputStream = Constants.zrLogConfig.getAdminResource().renderServiceWorker(request)) {
                    bytes = inputStream.readAllBytes();
                    response.write(new ByteArrayInputStream(bytes));
                    return false;
                }
            }
            if (resourceUris.contains(request.getUri())) {
                response.addHeader("Content-Type", MimeTypeUtil.getMimeStrByExt(request.getUri().substring(request.getUri().lastIndexOf(".") + 1)));
                try (InputStream inputStream = AdminPwaInterceptor.class.getResourceAsStream(request.getUri())) {
                    if (Objects.nonNull(inputStream)) {
                        bytes = inputStream.readAllBytes();
                        response.write(new ByteArrayInputStream(bytes));
                        return false;
                    }
                }
            }
            if (Constants.ADMIN_PWA_MANIFEST_JSON.equals(request.getUri()) || AdminConstants.ADMIN_PWA_MANIFEST_API_URI_PATH.equals(request.getUri())) {
                Method method = request.getServerConfig().getRouter().getMethod(AdminConstants.ADMIN_PWA_MANIFEST_API_URI_PATH);
                Object responseJson = method.invoke(Controller.buildController(method, request, response));
                if (responseJson != null) {
                    bytes = new GsonHttpJsonMessageConverter().toJson(responseJson).getBytes(StandardCharsets.UTF_8);
                    response.write(new ByteArrayInputStream(bytes));
                    return false;
                }
            }
            response.renderCode(404);
            return false;
        } finally {
            if (Objects.nonNull(bytes) && Constants.zrLogConfig.isStaticPluginRequest(request)) {
                StaticSitePlugin staticSitePlugin = Constants.zrLogConfig.getPlugin(StaticSitePlugin.class);
                if (Objects.nonNull(staticSitePlugin)) {
                    staticSitePlugin.saveResponseBodyToHtml(request, new String(bytes));
                }
            }
        }
    }
}
