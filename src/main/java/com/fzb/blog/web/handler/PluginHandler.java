package com.fzb.blog.web.handler;

import com.fzb.blog.util.ZrlogUtil;
import com.fzb.blog.web.incp.AdminTokenService;
import com.fzb.blog.web.incp.AdminTokenThreadLocal;
import com.fzb.common.util.IOUtil;
import com.fzb.common.util.http.HttpUtil;
import com.fzb.common.util.http.handle.CloseResponseHandle;
import com.jfinal.core.JFinal;
import com.jfinal.handler.Handler;
import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        boolean isPluginPath = false;
        for (String path : pluginHandlerPaths) {
            if (target.startsWith(path)) {
                isPluginPath = true;
            }
        }
        if (isPluginPath) {
            try {
                int userId = adminTokenService.getUserId(request);
                if (userId > 0) {
                    adminTokenService.setAdminToken(userId, request, response);
                }
                if (target.startsWith("/admin/plugins/")) {
                    try {
                        adminPermission(target, request, response);
                    } catch (IOException | InstantiationException e) {
                        LOGGER.error(e);
                    }
                } else if (target.startsWith("/plugin/") || target.startsWith("/p/")) {
                    try {
                        visitorPermission(target, request, response);
                    } catch (IOException | InstantiationException e) {
                        LOGGER.error(e);
                    }
                }
            } finally {
                AdminTokenThreadLocal.remove();
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
     * @throws IOException
     * @throws InstantiationException
     */
    private void adminPermission(String target, HttpServletRequest request, HttpServletResponse response) throws IOException, InstantiationException {
        if (AdminTokenThreadLocal.getUser() != null) {
            accessPlugin(target.replace("/admin/plugins", ""), request, response);
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
     * @throws IOException
     * @throws InstantiationException
     */
    private void visitorPermission(String target, HttpServletRequest request, HttpServletResponse response) throws IOException, InstantiationException {
        if (!accessPlugin(target.replace("/plugin", "").replace("/p", ""), request, response)) {
            response.sendError(403);
        }
    }

    /**
     * 代理中转HTTP请求，目前仅支持，GET，POST 请求方式的中转。
     *
     * @param uri
     * @param request
     * @param response
     * @return true 表示请求正常执行，false 代表遇到了一些问题
     * @throws IOException
     * @throws InstantiationException
     */
    private boolean accessPlugin(String uri, HttpServletRequest request, HttpServletResponse response) throws IOException, InstantiationException {
        String pluginServerHttp = ZrlogUtil.getPluginServer();
        CloseableHttpResponse httpResponse;
        CloseResponseHandle handle = new CloseResponseHandle();
        Map<String, String[]> paramMap = request.getParameterMap();
        //GET请求不关心request.getInputStream() 的数据
        if ("GET".equals(request.getMethod())) {
            httpResponse = HttpUtil.getDisableRedirectInstance().sendGetRequest(pluginServerHttp + uri, paramMap, handle, ZrlogUtil.genHeaderMapByRequest(request)).getT();
        } else {
            //如果是表单数据提交不关心请求头，反之将所有请求头都发到插件服务
            if (request.getHeader("Content-Type").contains("application/x-www-form-urlencoded")) {
                httpResponse = HttpUtil.getDisableRedirectInstance().sendPostRequest(pluginServerHttp + uri, paramMap, handle, ZrlogUtil.genHeaderMapByRequest(request)).getT();
            } else {
                httpResponse = HttpUtil.getDisableRedirectInstance().sendPostRequest(pluginServerHttp + uri, IOUtil.getByteByInputStream(request.getInputStream()), handle, ZrlogUtil.genHeaderMapByRequest(request)).getT();
            }
        }
        //添加插件服务的HTTP响应头到调用者响应头里面
        if (httpResponse != null) {
            Map<String, String> headerMap = new HashMap<String, String>();
            Header[] headers = httpResponse.getAllHeaders();
            for (Header header : headers) {
                headerMap.put(header.getName(), header.getValue());
            }
            //防止多次被Transfer-Encoding
            headerMap.remove("Transfer-Encoding");
            if (JFinal.me().getConstants().getDevMode()) {
                LOGGER.info("--------------------------------- response");
            }
            for (Map.Entry<String, String> t : headerMap.entrySet()) {
                response.addHeader(t.getKey(), t.getValue());
                if (JFinal.me().getConstants().getDevMode()) {
                    LOGGER.info("key " + t.getKey() + " value-> " + t.getValue());
                }
            }
            response.setStatus(httpResponse.getStatusLine().getStatusCode());
        }
        try {
            //将插件服务的HTTP的body返回给调用者
            if (handle.getT() != null && handle.getT().getEntity() != null) {
                request.getSession();
                byte[] bytes = IOUtil.getByteByInputStream(handle.getT().getEntity().getContent());
                response.addHeader("Content-Length", bytes.length + "");
                response.getOutputStream().write(bytes);
                response.getOutputStream().close();
                return true;
            } else {
                return false;
            }
        } finally {
            handle.close();
        }
    }
}
