package com.zrlog.admin.web.interceptor;

import com.hibegin.http.server.api.HandleAbleInterceptor;
import com.hibegin.http.server.api.HttpRequest;
import com.hibegin.http.server.api.HttpResponse;
import com.zrlog.admin.util.AdminWebTools;
import com.zrlog.admin.web.token.AdminTokenThreadLocal;
import com.zrlog.business.plugin.PluginCorePlugin;
import com.zrlog.common.Constants;
import com.zrlog.common.vo.AdminTokenVO;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * 这个类负责了对所有的插件路由的代理中转给插件服务（及 plugin-core 这个进程）
 * 目前插件服务拦截了 /admin/plugins/* （进行权限检查）
 * 如果想了解更多关于插件的实现可以浏览这篇文章 <a href="https://blog.zrlog.com/zrlog-plugin-dev.html">插件实现</a>
 */
public class AdminPluginInterceptor implements HandleAbleInterceptor {

    private static final String notGoodAdminUriPath = Constants.ADMIN_URI_BASE_PATH + "/plugins";
    private static final String adminPluginUriPath = Constants.ADMIN_URI_BASE_PATH + "/plugins/";
    private final List<String> pluginHandlerPaths = Arrays.asList(notGoodAdminUriPath, adminPluginUriPath);

    @Override
    public boolean isHandleAble(HttpRequest request) {
        String actionKey = request.getUri();
        return pluginHandlerPaths.stream().anyMatch(actionKey::startsWith);
    }

    /**
     * 检查是否登录，未登录的请求直接放回403的错误页面
     *
     * @param target
     * @param request
     * @param response
     * @param entry
     * @throws IOException
     */
    private void adminPermission(String target, HttpRequest request, HttpResponse response, AdminTokenVO entry) throws IOException, URISyntaxException, InterruptedException {
        if (Objects.isNull(entry)) {
            AdminWebTools.blockUnLoginRequestHandler(request, response);
            return;
        }
        if (Constants.zrLogConfig.getPlugin(PluginCorePlugin.class).accessPlugin(target.replaceFirst(adminPluginUriPath, "/"), request, response, entry)) {
            return;
        }
        response.renderCode(404);
    }

    @Override
    public boolean doInterceptor(HttpRequest request, HttpResponse response) {
        String target = request.getUri();
        if (Objects.equals(notGoodAdminUriPath, target)) {
            response.redirect(adminPluginUriPath);
            return false;
        }
        AdminTokenVO entry = Constants.zrLogConfig.getTokenService().getAdminTokenVO(request);
        try {
            adminPermission(target, request, response, entry);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return false;
    }
}
