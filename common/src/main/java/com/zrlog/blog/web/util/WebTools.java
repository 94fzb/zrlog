package com.zrlog.blog.web.util;

import com.zrlog.common.Constants;
import com.zrlog.common.exception.AdminAuthException;
import com.zrlog.util.ZrLogUtil;
import org.apache.http.conn.util.InetAddressUtils;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
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
    public static String getRealIp(HttpServletRequest request) {
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
            ip = request.getRemoteAddr();
        }
        if (InetAddressUtils.isIPv4Address(ip) || InetAddressUtils.isIPv6Address(ip)) {
            return ip;
        }
        throw new IllegalArgumentException(ip + " not ipAddress");
    }

    public static String getHomeUrlWithHost(HttpServletRequest request) {
        return "//" + request.getHeader("host") + request.getContextPath() + "/";
    }

    public static String getHomeUrlWithHostNotProtocol(HttpServletRequest request) {
        return request.getHeader("host") + request.getContextPath() + "/";
    }

    public static String getHomeUrl(HttpServletRequest request) {
        return request.getContextPath() + "/";
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
        try {
            return URLDecoder.decode(new String(param.getBytes(StandardCharsets.ISO_8859_1)), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            LoggerFactory.getLogger(WebTools.class).error("request convert to UTF-8 error ", e);
        }
        return "";
    }


    public static void blockUnLoginRequestHandler(HttpServletRequest request, HttpServletResponse response) {
        String actionKey = request.getRequestURI().substring(request.getContextPath().length());
        if (actionKey.startsWith("/api")) {
            throw new AdminAuthException();
        } else {
            try {
                String url = request.getContextPath()
                        + Constants.ADMIN_LOGIN_URI_PATH + "?redirectFrom="
                        + URLEncoder.encode(getRequestUriWithQueryString(request), "UTF-8");
                response.sendRedirect(url);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private static String getRequestUriWithQueryString(HttpServletRequest request) {
        String realUri = request.getRequestURI();
        if (request.getRequestURI().endsWith(Constants.ADMIN_URI_BASE_PATH)) {
            realUri = realUri + Constants.INDEX_URI_PATH;
        }
        return realUri + (request.getQueryString() != null ? "?" + request.getQueryString() : "");
    }
}
