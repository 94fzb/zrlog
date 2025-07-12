package com.zrlog.admin.web.interceptor;

import com.hibegin.common.util.StringUtils;
import com.hibegin.http.HttpMethod;
import com.hibegin.http.server.api.HandleAbleInterceptor;
import com.hibegin.http.server.api.HttpRequest;
import com.hibegin.http.server.api.HttpResponse;
import com.hibegin.http.server.web.MethodInterceptor;
import com.zrlog.admin.business.AdminConstants;
import com.zrlog.admin.util.AdminWebTools;
import com.zrlog.admin.web.annotation.RefreshCache;
import com.zrlog.admin.web.token.AdminTokenThreadLocal;
import com.zrlog.business.plugin.PluginCorePlugin;
import com.zrlog.common.Constants;
import com.zrlog.common.exception.ArgsException;
import com.zrlog.common.vo.AdminFullTokenVO;
import com.zrlog.common.vo.AdminTokenVO;

import java.lang.reflect.Method;
import java.util.Objects;

/**
 * 负责全部后台请求的处理（/admin/plugins/*,/api/admin/plugins/* 除外）
 */
public class AdminInterceptor implements HandleAbleInterceptor {


    private void validPluginToken(HttpRequest request) {
        String requestToken = request.getHeader("X-Plugin-Token");
        if (StringUtils.isEmpty(requestToken)) {
            throw new ArgsException("missing_token");
        }
        PluginCorePlugin plugin = Constants.zrLogConfig.getPlugin(PluginCorePlugin.class);
        if (Objects.isNull(plugin) || !Objects.equals(plugin.getToken(), requestToken)) {
            throw new ArgsException("token");
        }
    }

    /**
     * 为了规范代码，这里做了一点类是Spring的ResponseEntity的东西，及通过方法的返回值来判断是应该返回页面还会对应JSON数据
     * 具体方式看 AdminRouters，这里用到了 ThreadLocal
     */
    public boolean doInterceptor(HttpRequest request, HttpResponse response) throws Exception {
        try {
            String uri = request.getUri();
            if (AdminConstants.ADMIN_LOGIN_URI_PATH.equals(uri)) {
                AdminTokenVO adminTokenVO = Constants.zrLogConfig.getTokenService().getAdminTokenVO(request);
                if (adminTokenVO != null) {
                    response.redirect(AdminConstants.ADMIN_URI_BASE_PATH + AdminConstants.INDEX_URI_PATH);
                } else {
                    new MethodInterceptor().doInterceptor(request, response);
                }
                return false;
            }
            if (Objects.equals(AdminConstants.ADMIN_REFRESH_CACHE_API_URI_PATH, uri)) {
                AdminTokenVO adminTokenVO = Constants.zrLogConfig.getTokenService().getAdminTokenVO(request);
                if (Objects.isNull(adminTokenVO)) {
                    validPluginToken(request);
                }
                new MethodInterceptor().doInterceptor(request, response);
                return false;
            }
            if ((AdminConstants.ADMIN_URI_BASE_PATH + "/logout").equals(uri) || ("/api" + AdminConstants.ADMIN_LOGIN_URI_PATH).equals(uri)) {
                new MethodInterceptor().doInterceptor(request, response);
                return false;
            }
            Method method = request.getServerConfig().getRouter().getMethod(request.getUri());
            if (Objects.nonNull(method) && !Constants.zrLogConfig.isStaticPluginRequest(request)) {
                AdminFullTokenVO adminTokenVO = Constants.zrLogConfig.getTokenService().getAdminTokenVO(request);
                if (adminTokenVO == null) {
                    AdminWebTools.blockUnLoginRequestHandler(request, response);
                    return false;
                }
                Constants.zrLogConfig.getTokenService().setAdminToken(adminTokenVO.getUserId(), adminTokenVO.getSecretKey(), adminTokenVO.getSessionId(), adminTokenVO.getProtocol(), request, response);
            }
            new MethodInterceptor().doInterceptor(request, response);
            if (Objects.nonNull(method)) {
                RefreshCache annotation = method.getAnnotation(RefreshCache.class);
                if (Objects.nonNull(annotation)) {
                    //跳过非更新
                    if (annotation.onlyOnPostMethod() && request.getMethod() != HttpMethod.POST) {
                        return false;
                    }
                    if (annotation.async()) {
                        Constants.zrLogConfig.getCacheService().refreshInitDataCacheAsync(request, true);
                    } else {
                        Constants.zrLogConfig.getCacheService().refreshInitDataCacheAsync(request, true).get();
                    }
                }
            }
            return false;
        } finally {
            AdminTokenThreadLocal.remove();
        }
    }

    @Override
    public boolean isHandleAble(HttpRequest request) {
        if (Objects.equals(request.getUri(), "/admin") || Objects.equals(request.getUri(), "/api/admin")) {
            return true;
        }
        return request.getUri().startsWith("/admin/") || request.getUri().startsWith("/api/admin/");
    }
}
