package com.fzb.blog.util;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

public class ResUtil {

    public static String getStringFromRes(String key, HttpServletRequest request) {
        return ((Map) request.getAttribute("_res")).get(key).toString();
    }
}
