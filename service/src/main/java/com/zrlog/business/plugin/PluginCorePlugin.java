package com.zrlog.business.plugin;

import com.hibegin.common.util.http.handle.CloseResponseHandle;
import com.hibegin.http.HttpMethod;
import com.hibegin.http.server.api.HttpRequest;
import com.hibegin.http.server.api.HttpResponse;
import com.zrlog.common.vo.AdminTokenVO;
import com.zrlog.plugin.IPlugin;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

public interface PluginCorePlugin extends IPlugin {

    void refreshCache(String cacheVersion,HttpRequest request);

    CloseResponseHandle getContext(String uri, HttpMethod method, HttpRequest request, AdminTokenVO adminTokenVO) throws IOException, URISyntaxException, InterruptedException;

    <T> T requestService(HttpRequest inputRequest, Map<String,String[]> params, AdminTokenVO adminTokenVO, Class<T> clazz) throws IOException, URISyntaxException, InterruptedException;

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
    boolean accessPlugin(String uri, HttpRequest request, HttpResponse response, AdminTokenVO adminTokenVO) throws IOException, URISyntaxException, InterruptedException;

    String getToken();
}
