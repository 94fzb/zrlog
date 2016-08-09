package com.fzb.blog.config;

import com.fzb.blog.model.User;
import com.fzb.blog.util.ZrlogUtil;
import com.fzb.common.util.IOUtil;
import com.fzb.common.util.http.HttpUtil;
import com.fzb.common.util.http.handle.CloseResponseHandle;
import com.jfinal.handler.Handler;
import org.apache.http.Header;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class PluginHandler extends Handler {

    private static final Logger LOGGER = Logger.getLogger(PluginHandler.class);

    @Override
    public void handle(String target, HttpServletRequest request, HttpServletResponse response, boolean[] isHandled) {
        if (target.startsWith("/admin/plugins/")) {
            try {
                adminPermission(target, request, response);
            } catch (IOException e) {
                LOGGER.error(e);
            } catch (InstantiationException e) {
                LOGGER.error(e);
            }
        } else if (target.startsWith("/plugin/") || target.startsWith("/p/")) {
            try {
                visitorPermission(target, request, response);
            } catch (IOException e) {
                LOGGER.error(e);
            } catch (InstantiationException e) {
                LOGGER.error(e);
            }
        } else {
            this.next.handle(target, request, response, isHandled);
        }
    }

    private void adminPermission(String target, HttpServletRequest request, HttpServletResponse response) throws IOException, InstantiationException {
        User user = (User) request.getSession().getAttribute("user");
        if (user != null) {
            accessPlugin(target.replace("/admin/plugins", ""), request, response);
        } else {
            response.sendRedirect(request.getContextPath()
                    + "/admin/login?redirectFrom="
                    + request.getRequestURL() + (request.getQueryString() != null ? "?" + request.getQueryString() : ""));
        }
    }

    private void visitorPermission(String target, HttpServletRequest request, HttpServletResponse response) throws IOException, InstantiationException {
        if (!accessPlugin(target.replace("/plugin", "").replace("/p", ""), request, response)) {
            response.sendError(403);
        }
    }

    private boolean accessPlugin(String uri, HttpServletRequest request, HttpServletResponse response) throws IOException, InstantiationException {
        String pluginServerHttp = ZrlogUtil.getPluginServer();
        CloseableHttpResponse httpResponse;
        CloseResponseHandle data = new CloseResponseHandle();
        Map<String, String[]> paramMap = request.getParameterMap();
        if ("GET".equals(request.getMethod())) {
            httpResponse = HttpUtil.sendGetRequest(pluginServerHttp + uri, paramMap, data, ZrlogUtil.genHeaderMapByRequest(request)).getT();
        } else {
            if (request.getHeader("Content-Type").contains("application/x-www-form-urlencoded")) {
                httpResponse = HttpUtil.sendPostRequest(pluginServerHttp + uri, paramMap, data, ZrlogUtil.genHeaderMapByRequest(request)).getT();
            } else {
                httpResponse = HttpUtil.sendPostRequest(pluginServerHttp + uri, IOUtil.getByteByInputStream(request.getInputStream()), data, ZrlogUtil.genHeaderMapByRequest(request)).getT();
            }
        }
        if (httpResponse != null) {
            Map<String, String> headerMap = new HashMap<String, String>();
            Header[] headers = httpResponse.getAllHeaders();
            for (Header header : headers) {
                headerMap.put(header.getName(), header.getValue());
            }

            headerMap.remove("Transfer-Encoding");
            LOGGER.info("--------------------------------- response");
            for (Map.Entry<String, String> t : headerMap.entrySet()) {
                response.addHeader(t.getKey(), t.getValue());
                LOGGER.info("key " + t.getKey() + " value-> " + t.getValue());
            }
        }

        if (data.getT() != null && data.getT().getEntity() != null) {
            request.getSession();
            byte[] bytes = IOUtil.getByteByInputStream(data.getT().getEntity().getContent());
            response.addHeader("Content-Length", bytes.length + "");
            response.getOutputStream().write(bytes);
            response.getOutputStream().close();
            return true;
        } else {
            return false;
        }
    }
}
