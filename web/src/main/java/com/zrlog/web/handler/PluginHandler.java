package com.zrlog.web.handler;

import com.jfinal.handler.Handler;
import com.zrlog.common.vo.AdminTokenVO;
import com.zrlog.util.BlogBuildInfoUtil;
import com.zrlog.web.token.AdminTokenService;
import com.zrlog.web.token.AdminTokenThreadLocal;
import com.zrlog.web.util.PluginHelper;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * 这个类负责了对所有的插件路由的代理中转给插件服务（及plugin-core.jar这个进程），使用的是Handler而非Interception也是由于Interception
 * 不处理静态请求。
 * 目前插件服务拦截了
 * 1./admin/plugins/* （进行权限检查）
 * 2./plugin/* /p/* （是plugin的短路由，代码逻辑上是不区分的，不检查权限）
 * <p>
 * 如果想了解更多关于插件的实现可以浏览这篇文章 http://blog.zrlog.com/post/zrlog-plugin-dev
 */
public class PluginHandler extends Handler {

    private static final Logger LOGGER = Logger.getLogger(PluginHandler.class);

    private AdminTokenService adminTokenService = new AdminTokenService();

    private List<String> pluginHandlerPaths = Arrays.asList("/admin/plugins/", "/plugin/", "/p/");

    @Override
    public void handle(String target, HttpServletRequest request, HttpServletResponse response, boolean[] isHandled) {
        //便于Wappalyzer读取
        response.addHeader("X-ZrLog", BlogBuildInfoUtil.getVersion());
        boolean isPluginPath = false;
        for (String path : pluginHandlerPaths) {
            if (target.startsWith(path)) {
                isPluginPath = true;
            }
        }
        if (isPluginPath) {
            AdminTokenVO entry = null;
            try {
                entry = adminTokenService.getAdminTokenVO(request);
                if (target.startsWith("/admin/plugins/")) {
                    try {
                        adminPermission(target, request, response, entry);
                    } catch (IOException | InstantiationException e) {
                        LOGGER.error(e);
                    }
                } else if (target.startsWith("/plugin/") || target.startsWith("/p/")) {
                    try {
                        visitorPermission(target, request, response, entry);
                    } catch (IOException | InstantiationException e) {
                        LOGGER.error(e);
                    }
                }
            } finally {
                isHandled[0] = true;
                if (entry != null) {
                    AdminTokenThreadLocal.remove();
                }
            }
        } else {
            this.next.handle(target, request, response, isHandled);
        }
    }

    /**
     * 检查是否登陆，未登陆的请求直接放回403的错误页面
     *
     * @param target
     * @param request
     * @param response
     * @param entry
     * @throws IOException
     * @throws InstantiationException
     */
    private void adminPermission(String target, HttpServletRequest request, HttpServletResponse response, AdminTokenVO entry) throws IOException, InstantiationException {
        if (entry != null) {
            PluginHelper.accessPlugin(target.replace("/admin/plugins", ""), request, response, entry);
        } else {
            response.sendRedirect(request.getContextPath()
                    + "/admin/login?redirectFrom="
                    + request.getRequestURL() + (request.getQueryString() != null ? "?" + request.getQueryString() : ""));
        }
    }

    /**
     * 不检查是否登陆，错误请求直接直接转化为403
     *
     * @param target
     * @param request
     * @param response
     * @param key
     * @throws IOException
     * @throws InstantiationException
     */
    private void visitorPermission(String target, HttpServletRequest request, HttpServletResponse response, AdminTokenVO key) throws IOException, InstantiationException {
        if (!PluginHelper.accessPlugin(target.replace("/plugin", "").replace("/p", ""), request, response, key)) {
            response.sendError(403);
        }
    }

}
