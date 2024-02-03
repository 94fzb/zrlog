package com.zrlog.blog.web.util;

import com.hibegin.http.server.api.HttpRequest;
import com.hibegin.http.server.api.HttpResponse;
import com.zrlog.common.Constants;
import com.zrlog.common.exception.AdminAuthException;
import com.zrlog.util.ZrLogUtil;

import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

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
        if (ip == null || ip.length() == 0) {
            ip = request.getHeader("X-forwarded-for");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteHost();
        }
        return ip;
    }

    public static String getHomeUrlWithHost(HttpRequest request) {
        return "//" + request.getHeader("host") + "/";
    }

    public static String getHomeUrlWithHostNotProtocol(HttpRequest request) {
        return request.getHeader("host") + "/";
    }

    public static String getHomeUrl(HttpRequest request) {
        return "/";
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

    private static boolean containsHanScript(String s) {
        return s.codePoints().anyMatch(codepoint -> Character.UnicodeScript.of(codepoint) == Character.UnicodeScript.HAN);
    }

    /**
     * 用于转化 GET 的中文乱码
     */
    public static String convertRequestParam(String param) {
        if (param == null) {
            return "";
        }
        //如果可以正常读取到中文的情况，直接跳过转换
        if (containsHanScript(param)) {
            return param;
        }
        return URLDecoder.decode(new String(param.getBytes(StandardCharsets.ISO_8859_1)), StandardCharsets.UTF_8);
    }


    public static void blockUnLoginRequestHandler(HttpRequest request, HttpResponse response) {
        String actionKey = request.getUri();
        if (actionKey.startsWith("/api")) {
            throw new AdminAuthException();
        } else {
            try {
                String url = Constants.ADMIN_LOGIN_URI_PATH + "?redirectFrom="
                        + URLEncoder.encode(getRequestUriWithQueryString(request), "UTF-8");
                response.redirect(url);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static String getRequestUriWithQueryString(HttpRequest request) {
        String realUri = request.getUri();
        if (request.getUri().endsWith(Constants.ADMIN_URI_BASE_PATH)) {
            realUri = realUri + Constants.INDEX_URI_PATH;
        }
        return realUri + (request.getQueryStr() != null ? "?" + request.getQueryStr() : "");
    }
}
