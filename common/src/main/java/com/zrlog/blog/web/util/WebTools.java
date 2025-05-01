package com.zrlog.blog.web.util;

import com.hibegin.common.util.EnvKit;
import com.hibegin.common.util.UrlEncodeUtils;
import com.hibegin.http.server.api.HttpRequest;
import com.zrlog.util.ZrLogUtil;

import java.util.Objects;

/**
 * 存放与Web相关的工具代码
 */
public class WebTools {

    /**
     * 处理由于浏览器使用透明代理，或者是WebServer运行在诸如 nginx/apache 这类 HttpServer后面的情况，通过获取请求头真实IP地址
     */
    public static String getRealIp(HttpRequest request) {
        String ip = null;
        //bae env
        if (ZrLogUtil.isBae() && request.getHeader("clientip") != null) {
            ip = request.getHeader("clientip");
        }
        if (EnvKit.isFaaSMode() && request.getHeader("cf-connecting-ip") != null) {
            ip = request.getHeader("cf-connecting-ip");
        }
        if (ip == null || ip.isEmpty()) {
            ip = request.getHeader("X-forwarded-for");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteHost();
        }
        return ip;
    }


    public static String getHomeUrl(HttpRequest request) {
        if (Objects.equals("/", request.getContextPath())) {
            return "/";
        }
        return request.getContextPath() + "/";
    }

    public static String buildEncodedUrl(HttpRequest request, String url) {
        if (request == null) {
            return UrlEncodeUtils.encodeUrl(url);
        }
        if (url.startsWith("/")) {
            return UrlEncodeUtils.encodeUrl(getHomeUrl(request) + url.substring(1));
        }
        return UrlEncodeUtils.encodeUrl(getHomeUrl(request) + url);
    }

    public static String htmlEncode(String source) {
        if (source == null) {
            return "";
        }
        String html;
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < source.length(); i++) {
            char c = source.charAt(i);
            switch (c) {
                case '<':
                    buffer.append("&lt;");
                    break;
                case '>':
                    buffer.append("&gt;");
                    break;
                case '&':
                    buffer.append("&amp;");
                    break;
                case '"':
                    buffer.append("&quot;");
                    break;
                case 10:
                case 13:
                    break;
                default:
                    buffer.append(c);
            }
        }
        html = buffer.toString();
        return html;
    }

}
