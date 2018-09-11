package com.zrlog.service;

import com.jfinal.core.JFinal;
import com.zrlog.common.vo.AdminTokenVO;
import com.zrlog.model.User;
import com.zrlog.util.I18nUtil;
import com.zrlog.util.ZrLogUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

public class PluginHelper {

    public static Map<String, String> genHeaderMapByRequest(HttpServletRequest request) {
        Map<String, String> map = new HashMap<>();
        AdminTokenVO adminTokenVO = AdminTokenThreadLocal.getUser();
        if (adminTokenVO != null) {
            User user = User.dao.findById(adminTokenVO.getUserId());
            map.put("LoginUserName", user.get("userName").toString());
            map.put("LoginUserId", adminTokenVO.getUserId() + "");
        }
        map.put("IsLogin", (adminTokenVO != null) + "");
        map.put("Current-Locale", I18nUtil.getCurrentLocale());
        map.put("Blog-Version", ((Map) JFinal.me().getServletContext().getAttribute("zrlog")).get("version").toString());
        if (request != null) {
            String fullUrl = ZrLogUtil.getFullUrl(request);
            if (request.getQueryString() != null) {
                fullUrl = fullUrl + "?" + request.getQueryString();
            }
            if (adminTokenVO != null) {
                fullUrl = adminTokenVO.getProtocol() + ":" + fullUrl;
            }
            map.put("Cookie", request.getHeader("Cookie"));
            map.put("AccessUrl", "http://127.0.0.1:" + request.getServerPort() + request.getContextPath());
            if (request.getHeader("Content-Type") != null) {
                map.put("Content-Type", request.getHeader("Content-Type"));
            }
            map.put("Full-Url", fullUrl);
        }
        return map;
    }
}
