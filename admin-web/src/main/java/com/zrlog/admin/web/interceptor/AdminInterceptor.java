package com.zrlog.admin.web.interceptor;

import com.hibegin.http.server.api.HttpRequest;
import com.hibegin.http.server.api.HttpResponse;
import com.hibegin.http.server.api.Interceptor;
import com.hibegin.http.server.web.MethodInterceptor;
import com.zrlog.admin.web.annotation.RefreshCache;
import com.zrlog.admin.web.token.AdminTokenService;
import com.zrlog.admin.web.token.AdminTokenThreadLocal;
import com.zrlog.blog.web.util.WebTools;
import com.zrlog.business.cache.CacheService;
import com.zrlog.common.Constants;
import com.zrlog.common.vo.AdminTokenVO;
import com.zrlog.model.User;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;

/**
 * 负责全部后台请求的处理（/admin/plugins/*,/api/admin/plugins/* 除外）
 */
public class AdminInterceptor implements Interceptor {

    private final AdminTokenService adminTokenService = new AdminTokenService();
    private final CacheService cacheService = new CacheService();


    /**
     * 为了规范代码，这里做了一点类是Spring的ResponseEntity的东西，及通过方法的返回值来判断是应该返回页面还会对应JSON数据
     * 具体方式看 AdminRouters，这里用到了 ThreadLocal
     */
    public boolean doInterceptor(HttpRequest request, HttpResponse response) throws Exception {
        try {
            String uri = request.getUri();
            if (Constants.ADMIN_LOGIN_URI_PATH.equals(uri)) {
                AdminTokenVO adminTokenVO = adminTokenService.getAdminTokenVO(request);
                if (adminTokenVO != null) {
                    response.redirect(Constants.ADMIN_URI_BASE_PATH + Constants.INDEX_URI_PATH);
                } else {
                    new MethodInterceptor().doInterceptor(request, response);
                }
                return false;
            }
            if ((Constants.ADMIN_URI_BASE_PATH + "/logout").equals(uri) ||
                    ("/api" + Constants.ADMIN_LOGIN_URI_PATH).equals(uri)) {
                new MethodInterceptor().doInterceptor(request, response);
                return false;
            }
            AdminTokenVO adminTokenVO = adminTokenService.getAdminTokenVO(request);
            if (adminTokenVO == null) {
                WebTools.blockUnLoginRequestHandler(request, response);
                return false;
            }

            Map<String, Object> user = new User().loadById(adminTokenVO.getUserId());
            adminTokenService.setAdminToken(user, adminTokenVO.getSessionId(), adminTokenVO.getProtocol(), request, response);
            new MethodInterceptor().doInterceptor(request, response);
            Method method = request.getServerConfig().getRouter().getMethod(request.getUri());
            if (Objects.nonNull(method)) {
                RefreshCache annotation = method.getAnnotation(RefreshCache.class);
                if (Objects.nonNull(annotation)) {
                    if (annotation.async()) {
                        cacheService.refreshInitDataCacheAsync(request, true);
                    } else {
                        cacheService.refreshInitDataCache(request, true);
                    }
                }
            }
            return false;
        } finally {
            AdminTokenThreadLocal.remove();
        }
    }

}
