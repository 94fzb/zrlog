package com.zrlog.blog.web.util;

import com.hibegin.common.util.StringUtils;
import com.hibegin.http.HttpMethod;
import com.hibegin.http.server.ApplicationContext;
import com.hibegin.http.server.api.HttpRequest;
import com.hibegin.http.server.api.HttpResponse;
import com.hibegin.http.server.config.RequestConfig;
import com.hibegin.http.server.util.HttpRequestBuilder;
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
                        + URLEncoder.encode(getRequestUriWithQueryString(request), StandardCharsets.UTF_8);
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
        if (StringUtils.isEmpty(request.getQueryStr())) {
            return realUri;
        }
        return realUri + "?" + request.getQueryStr();
    }

    public static String encodeUrl(String path) {
        StringBuilder encoded = new StringBuilder();
        for (char c : path.toCharArray()) {
            if (isUnsafeCharacter(c)) {
                encoded.append(URLEncoder.encode(String.valueOf(c), StandardCharsets.UTF_8));
            } else {
                encoded.append(c);
            }
        }
        return encoded.toString();
    }

    private static boolean isUnsafeCharacter(char c) {
        // 检查字符是否为需要编码的字符，保留保留字符和子分隔符
        return !(isUnreservedCharacter(c) || isReservedCharacter(c));
    }

    private static boolean isUnreservedCharacter(char c) {
        // 不需要编码的普通字符：字母、数字、- . _ ~
        return (c >= 'a' && c <= 'z') ||
                (c >= 'A' && c <= 'Z') ||
                (c >= '0' && c <= '9') ||
                c == '-' || c == '.' || c == '_' || c == '~';
    }

    private static boolean isReservedCharacter(char c) {
        // 保留字符和子分隔符
        return ":/?#[]@!$&'()*+,;=".indexOf(c) != -1;
    }

    public static HttpRequest buildMockRequest(HttpMethod method, String uri, RequestConfig requestConfig, ApplicationContext applicationContext) throws Exception {
        return HttpRequestBuilder.buildRequest(method, uri, ZrLogUtil.getBlogHostByWebSite(), ZrLogUtil.STATIC_USER_AGENT, requestConfig, applicationContext);
    }

}
