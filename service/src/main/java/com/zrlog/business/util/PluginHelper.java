package com.zrlog.business.util;

import com.hibegin.common.util.IOUtil;
import com.hibegin.common.util.StringUtils;
import com.hibegin.common.util.http.HttpUtil;
import com.hibegin.common.util.http.handle.CloseResponseHandle;
import com.hibegin.http.HttpMethod;
import com.hibegin.http.server.api.HttpRequest;
import com.hibegin.http.server.api.HttpResponse;
import com.zrlog.common.Constants;
import com.zrlog.common.vo.AdminTokenVO;
import com.zrlog.util.BlogBuildInfoUtil;
import com.zrlog.util.I18nUtil;
import com.zrlog.util.ZrLogUtil;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.*;

public class PluginHelper {


    public static Map<String, String> genHeaderMapByRequest(HttpRequest request, AdminTokenVO adminTokenVO) {
        Map<String, String> map = new HashMap<>();
        if (adminTokenVO != null) {
            map.put("LoginUserId", adminTokenVO.getUserId() + "");
        }
        map.put("IsLogin", (adminTokenVO != null) + "");
        map.put("Current-Locale", I18nUtil.getCurrentLocale());
        map.put("Blog-Version", BlogBuildInfoUtil.getVersion());
        map.put("Dark-Mode", Constants.getBooleanByFromWebSite("admin_darkMode") + "");
        if (request != null) {
            String fullUrl = ZrLogUtil.getFullUrl(request);
            if (request.getQueryStr() != null) {
                fullUrl = fullUrl + "?" + request.getQueryStr();
            }
            if (adminTokenVO != null) {
                fullUrl = adminTokenVO.getProtocol() + ":" + fullUrl;
            }
            map.put("Cookie", request.getHeader("Cookie"));
            map.put("AccessUrl", "http://127.0.0.1:" + request.getServerConfig().getPort());
            if (request.getHeader("Content-Type") != null) {
                map.put("Content-Type", request.getHeader("Content-Type"));
            }
            if (StringUtils.isNotEmpty(request.getHeader("Referer"))) {
                map.put("Referer", request.getHeader("Referer"));
            }
            map.put("Full-Url", fullUrl);
        }
        return map;
    }

    public static CloseResponseHandle getContext(String uri, HttpMethod method, HttpRequest request, AdminTokenVO adminTokenVO) throws IOException, URISyntaxException, InterruptedException {
        String pluginServerHttp = Constants.pluginServer;
        CloseResponseHandle handle = new CloseResponseHandle();
        //GET请求不关心request.getInputStream() 的数据
        if (method.equals(request.getMethod()) && "GET".equalsIgnoreCase(method.name())) {
            HttpUtil.getInstance().sendGetRequest(pluginServerHttp + uri, request.getParamMap(), handle, PluginHelper.genHeaderMapByRequest(request, adminTokenVO));
        } else {
            //如果是表单数据提交不关心请求头，反之将所有请求头都发到插件服务
            if ("application/x-www-form-urlencoded".equals(request.getHeader("Content-Type"))) {
                HttpUtil.getInstance().sendPostRequest(pluginServerHttp + uri, request.getParamMap(), handle, PluginHelper.genHeaderMapByRequest(request, adminTokenVO));
            } else {
                HttpUtil.getInstance().sendPostRequest(pluginServerHttp + uri + "?" + request.getQueryStr(), IOUtil.getByteByInputStream(request.getInputStream()), handle, PluginHelper.genHeaderMapByRequest(request, adminTokenVO)).getT();
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
    public static boolean accessPlugin(String uri, HttpRequest request, HttpResponse response, AdminTokenVO adminTokenVO) throws IOException, URISyntaxException, InterruptedException {
        CloseResponseHandle handle = PluginHelper.getContext(uri, request.getMethod(), request, adminTokenVO);
        if (Objects.isNull(handle.getT()) || Objects.isNull(handle.getT().body())) {
            response.renderCode(404);
            return true;
        }
        List<String> ignoreHeaderKeys = Arrays.asList("content-encoding", "transfer-encoding", "content-length", "server", "connection");
        try (InputStream inputStream = handle.getT().body()) {
            for (Map.Entry<String, List<String>> header : handle.getT().headers().map().entrySet()) {
                if (ignoreHeaderKeys.stream().anyMatch(x -> Objects.equals(x, header.getKey()))) {
                    continue;
                }
                response.addHeader(header.getKey(), header.getValue().get(0));
            }
            response.addHeader("Connection", "close");
            //将插件服务的HTTP的body返回给调用者
            response.write(inputStream, handle.getT().statusCode());
            return true;
        }

    }
}
