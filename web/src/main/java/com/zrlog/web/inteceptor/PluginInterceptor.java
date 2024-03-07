package com.zrlog.web.inteceptor;

import com.hibegin.common.util.LoggerUtil;
import com.hibegin.http.server.api.HttpRequest;
import com.hibegin.http.server.api.HttpResponse;
import com.hibegin.http.server.api.Interceptor;
import com.zrlog.admin.web.token.AdminTokenService;
import com.zrlog.admin.web.token.AdminTokenThreadLocal;
import com.zrlog.blog.web.util.WebTools;
import com.zrlog.business.util.PluginHelper;
import com.zrlog.common.Constants;
import com.zrlog.common.vo.AdminTokenVO;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 这个类负责了对所有的插件路由的代理中转给插件服务（及plugin-core.jar这个进程），使用的是Handler而非Interception也是由于Interception
 * 不处理静态请求。
 * 目前插件服务拦截了
 * 1./admin/plugins/* （进行权限检查）
 * 2./plugin/* /p/* （是plugin的短路由，代码逻辑上是不区分的，不检查权限）
 * 如果想了解更多关于插件的实现可以浏览这篇文章 <a href="https://blog.zrlog.com/zrlog-plugin-dev">插件实现</a>
 */
public class PluginInterceptor implements Interceptor {

    private static final Logger LOGGER = LoggerUtil.getLogger(PluginInterceptor.class);

    private final AdminTokenService adminTokenService = new AdminTokenService();
    public static final List<String> pluginHandlerPaths = Arrays.asList(Constants.ADMIN_URI_BASE_PATH + "/plugins/", "/plugin/", "/p/");


    /**
     * 检查是否登录，未登录的请求直接放回403的错误页面
     *
     * @param target
     * @param request
     * @param response
     * @param entry
     * @throws IOException
     * @throws InstantiationException
     */
    private void adminPermission(String target, HttpRequest request, HttpResponse response, AdminTokenVO entry) throws IOException, InstantiationException, URISyntaxException, InterruptedException {
        if (entry != null) {
            PluginHelper.accessPlugin(target.replace(Constants.ADMIN_URI_BASE_PATH + "/plugins", ""), request, response, entry);
        } else {
            WebTools.blockUnLoginRequestHandler(request, response);
        }
    }

    /**
     * 不检查是否登录，错误请求直接直接转化为403
     *
     * @param target
     * @param request
     * @param response
     * @param key
     * @throws IOException
     * @throws InstantiationException
     */
    private void visitorPermission(String target, HttpRequest request, HttpResponse response, AdminTokenVO key) throws IOException, InstantiationException, URISyntaxException, InterruptedException {
        if (!PluginHelper.accessPlugin(target.replace("/plugin", "").replace("/p", ""), request, response, key)) {
            response.renderCode(403);
        }
    }

    @Override
    public boolean doInterceptor(HttpRequest request, HttpResponse response) {
        String target = request.getUri();
        if (pluginHandlerPaths.stream().noneMatch(target::startsWith)) {
            return true;
        }
        AdminTokenVO entry = null;
        try {
            entry = adminTokenService.getAdminTokenVO(request);
            if (target.startsWith(Constants.ADMIN_URI_BASE_PATH + "/plugins/")) {
                try {
                    adminPermission(target, request, response, entry);
                } catch (IOException | InstantiationException | URISyntaxException | InterruptedException e) {
                    LOGGER.log(Level.SEVERE, "", e);
                }
            } else if (target.startsWith("/plugin/") || target.startsWith("/p/")) {
                try {
                    visitorPermission(target, request, response, entry);
                } catch (IOException | InstantiationException | URISyntaxException | InterruptedException e) {
                    LOGGER.log(Level.SEVERE, "", e);
                }
            }
        } finally {
            if (entry != null) {
                AdminTokenThreadLocal.remove();
            }
        }
        return true;
    }
}
