package com.fzb.blog.util;

import com.fzb.blog.model.User;
import com.jfinal.core.JFinal;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

public class ZrlogUtil {

    private ZrlogUtil() {

    }

    public static String getPluginServer() {
        return JFinal.me().getServletContext().getAttribute("pluginServer").toString();
    }

    public static Map<String, String> genHeaderMapByRequest(HttpServletRequest request) {
        Map<String, String> map = new HashMap<String, String>();
        User user = (User) request.getSession().getAttribute("user");
        if (user != null) {
            map.put("LoginUserName", user.get("userName").toString());
            map.put("LoginUserId", user.get("userId").toString());
        }
        map.put("IsLogin", (user != null) + "");
        map.put("Blog-Version", ((Map) JFinal.me().getServletContext().getAttribute("zrlog")).get("version").toString());
        map.put("Full-Url", request.getRequestURL().toString());
        if (request.getHeader("Content-Type") != null) {
            map.put("Content-Type", request.getHeader("Content-Type"));
        }
        /*while (request.getHeaderNames().hasMoreElements()) {
            String key = request.getHeaderNames().nextElement().toString();
            map.put(key, request.getHeader(key));
        }*/
        return map;
    }
}
