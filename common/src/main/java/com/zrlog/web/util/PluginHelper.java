package com.zrlog.web.util;

import com.hibegin.common.util.IOUtil;
import com.hibegin.common.util.http.HttpUtil;
import com.hibegin.common.util.http.handle.CloseResponseHandle;
import com.zrlog.common.Constants;
import com.zrlog.common.vo.AdminTokenVO;
import com.zrlog.util.BlogBuildInfoUtil;
import com.zrlog.util.I18nUtil;
import com.zrlog.util.ZrLogUtil;
import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class PluginHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(PluginHelper.class);

    public static Map<String, String> genHeaderMapByRequest(HttpServletRequest request, AdminTokenVO adminTokenVO) {
        Map<String, String> map = new HashMap<>();
        if (adminTokenVO != null) {
            map.put("LoginUserId", adminTokenVO.getUserId() + "");
        }
        map.put("IsLogin", (adminTokenVO != null) + "");
        map.put("Current-Locale", I18nUtil.getCurrentLocale());
        map.put("Blog-Version", BlogBuildInfoUtil.getVersion());
        if (request != null) {
            String fullUrl = ZrLogUtil.getFullUrl(request);
            if (request.getQueryString() != null) {
                fullUrl = fullUrl + "?" + request.getQueryString();
            }
            if (adminTokenVO != null) {
                fullUrl = adminTokenVO.getProtocol() + ":" + fullUrl;
            }
            map.put("Cookie", request.getHeader("Cookie"));
            map.put("AccessUrl", "http://127.0.0.1:" + request.getServerPort() + request.getContextPath());
            if (request.getHeader("Content-Type") != null) {
                map.put("Content-Type", request.getHeader("Content-Type"));
            }
            map.put("Full-Url", fullUrl);
        }
        return map;
    }

    public static CloseResponseHandle getContext(String uri, String method, HttpServletRequest request, boolean disableRedirect, AdminTokenVO adminTokenVO) throws IOException, InstantiationException {
        String pluginServerHttp = Constants.pluginServer;
        CloseableHttpResponse httpResponse;
        CloseResponseHandle handle = new CloseResponseHandle();
        HttpUtil httpUtil = disableRedirect ? HttpUtil.getDisableRedirectInstance() : HttpUtil.getInstance();
        //GET请求不关心request.getInputStream() 的数据
        if (method.equals(request.getMethod()) && "GET".equalsIgnoreCase(method)) {
            httpResponse = httpUtil.sendGetRequest(pluginServerHttp + uri, request.getParameterMap(), handle, PluginHelper.genHeaderMapByRequest(request, adminTokenVO)).getT();
        } else {
            //如果是表单数据提交不关心请求头，反之将所有请求头都发到插件服务
            if ("application/x-www-form-urlencoded".equals(request.getContentType())) {
                httpResponse = httpUtil.sendPostRequest(pluginServerHttp + uri, request.getParameterMap(), handle, PluginHelper.genHeaderMapByRequest(request, adminTokenVO)).getT();
            } else {
                httpResponse = httpUtil.sendPostRequest(pluginServerHttp + uri + "?" + request.getQueryString(), IOUtil.getByteByInputStream(request.getInputStream()), handle, PluginHelper.genHeaderMapByRequest(request, adminTokenVO)).getT();
            }
        }
        //添加插件服务的HTTP响应头到调用者响应头里面
        if (httpResponse != null) {
            Map<String, String> headerMap = new HashMap<>();
            Header[] headers = httpResponse.getAllHeaders();
            for (Header header : headers) {
                headerMap.put(header.getName(), header.getValue());
            }
            if (BlogBuildInfoUtil.isDev()) {
                LOGGER.info("{} --------------------------------- response",uri);
            }
            for (Map.Entry<String, String> t : headerMap.entrySet()) {
                if (BlogBuildInfoUtil.isDev()) {
                    LOGGER.info("{} value-> {}" ,t.getKey(), t.getValue());
                }
            }
        }
        return handle;
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
    public static boolean accessPlugin(String uri, HttpServletRequest request, HttpServletResponse response, AdminTokenVO adminTokenVO) throws IOException, InstantiationException {
        CloseResponseHandle handle = PluginHelper.getContext(uri, request.getMethod(), request, true, adminTokenVO);
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
                response.addHeader("Content-Length", Integer.toString(bytes.length));
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
