package com.zrlog.web.handler;

import com.hibegin.common.util.IOUtil;
import com.hibegin.common.util.http.HttpUtil;
import com.hibegin.common.util.http.handle.CloseResponseHandle;
import com.jfinal.core.JFinal;
import com.jfinal.handler.Handler;
import com.zrlog.common.Constants;
import com.zrlog.common.vo.AdminTokenVO;
import com.zrlog.model.User;
import com.zrlog.service.AdminTokenService;
import com.zrlog.service.AdminTokenThreadLocal;
import com.zrlog.service.PluginHelper;
import com.zrlog.util.BlogBuildInfoUtil;
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
        //便于Wappalyzer读取
        response.addHeader("X-ZrLog", BlogBuildInfoUtil.getVersion());
        boolean isPluginPath = false;
        for (String path : pluginHandlerPaths) {
            if (target.startsWith(path)) {
                isPluginPath = true;
            }
        }
        if (isPluginPath) {
            try {
                Map.Entry<AdminTokenVO, User> entry = adminTokenService.getAdminTokenVOUserEntry(request);
                if (entry != null) {
                    adminTokenService.setAdminToken(entry.getValue(), entry.getKey().getSessionId(), entry.getKey().getProtocol(), request, response);
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
                isHandled[0] = true;
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
        CloseResponseHandle handle = getContext(uri, request.getMethod(), request, true);
        try {
            if (handle.getT() != null && handle.getT().getEntity() != null) {
                response.setStatus(handle.getT().getStatusLine().getStatusCode());
                //防止多次被Transfer-Encoding
                handle.getT().removeHeaders("Transfer-Encoding");
                for (Header header : handle.getT().getAllHeaders()) {
                    response.addHeader(header.getName(), header.getValue());
                }
                //将插件服务的HTTP的body返回给调用者
                byte[] bytes = IOUtil.getByteByInputStream(handle.getT().getEntity().getContent());
                response.addHeader("Content-Length", Integer.valueOf(bytes.length).toString());
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

    public static CloseResponseHandle getContext(String uri, String method, HttpServletRequest request, boolean disableRedirect) throws IOException, InstantiationException {
        String pluginServerHttp = Constants.pluginServer;
        CloseableHttpResponse httpResponse;
        CloseResponseHandle handle = new CloseResponseHandle();
        HttpUtil httpUtil = disableRedirect ? HttpUtil.getDisableRedirectInstance() : HttpUtil.getInstance();
        //GET请求不关心request.getInputStream() 的数据
        if (method.equals(request.getMethod()) && "GET".equalsIgnoreCase(method)) {
            httpResponse = httpUtil.sendGetRequest(pluginServerHttp + uri, request.getParameterMap(), handle, PluginHelper.genHeaderMapByRequest(request)).getT();
        } else {
            //如果是表单数据提交不关心请求头，反之将所有请求头都发到插件服务
            if ("application/x-www-form-urlencoded".equals(request.getContentType())) {
                httpResponse = httpUtil.sendPostRequest(pluginServerHttp + uri, request.getParameterMap(), handle, PluginHelper.genHeaderMapByRequest(request)).getT();
            } else {
                httpResponse = httpUtil.sendPostRequest(pluginServerHttp + uri + "?" + request.getQueryString(), IOUtil.getByteByInputStream(request.getInputStream()), handle, PluginHelper.genHeaderMapByRequest(request)).getT();
            }
        }
        //添加插件服务的HTTP响应头到调用者响应头里面
        if (httpResponse != null) {
            Map<String, String> headerMap = new HashMap<>();
            Header[] headers = httpResponse.getAllHeaders();
            for (Header header : headers) {
                headerMap.put(header.getName(), header.getValue());
            }
            if (JFinal.me().getConstants().getDevMode()) {
                LOGGER.info(uri + " --------------------------------- response");
            }
            for (Map.Entry<String, String> t : headerMap.entrySet()) {
                if (JFinal.me().getConstants().getDevMode()) {
                    LOGGER.info("key " + t.getKey() + " value-> " + t.getValue());
                }
            }
        }
        return handle;
    }
}
