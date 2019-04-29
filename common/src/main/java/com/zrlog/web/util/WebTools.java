package com.zrlog.web.util;

import com.zrlog.util.ZrLogUtil;

import javax.servlet.http.HttpServletRequest;

/**
 * 存放与Web相关的工具代码
 */
public class WebTools {

    /**
     * 处理由于浏览器使用透明代理，或者是WebServer运行在诸如 nginx/apache 这类 HttpServer后面的情况，通过获取请求头真实IP地址
     *
     * @param request
     * @return
     */
    public static String getRealIp(HttpServletRequest request) {
        //bae env
        if (ZrLogUtil.isBae() && request.getHeader("clientip") != null) {
            return request.getHeader("clientip");
        }
        String ip = request.getHeader("X-forwarded-for");
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
        return ip;
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
}
