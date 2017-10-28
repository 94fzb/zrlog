package com.zrlog.web.controller.admin.page;

import com.zrlog.common.Constants;
import com.zrlog.model.Comment;
import com.zrlog.model.Log;
import com.zrlog.util.ParseUtil;
import com.zrlog.web.controller.admin.api.UpgradeController;
import com.zrlog.web.token.AdminTokenThreadLocal;
import com.zrlog.web.controller.BaseController;
import com.zrlog.web.token.AdminTokenService;
import com.jfinal.core.JFinal;

import javax.servlet.http.Cookie;
import java.util.List;
import java.util.Map;

public class AdminPageController extends BaseController {

    private AdminTokenService adminTokenService = new AdminTokenService();

    public String index() {
        if (AdminTokenThreadLocal.getUser() != null) {
            Map<String, Object> commentMap = Comment.dao.noRead(1, 5);
            if (commentMap.get("rows") != null) {
                List<Comment> rows = (List<Comment>) commentMap.get("rows");
                for (Comment comment : rows) {
                    comment.put("userComment", ParseUtil.autoDigest(comment.get("userComment").toString(), 15));
                }
            }
            JFinal.me().getServletContext().setAttribute("comments", commentMap);
            JFinal.me().getServletContext().setAttribute("commCount", Comment.dao.getCommentCount());
            JFinal.me().getServletContext().setAttribute("toDayCommCount", Comment.dao.getToDayCommentCount());
            JFinal.me().getServletContext().setAttribute("clickCount", Log.dao.sumAllClick());
            JFinal.me().getServletContext().setAttribute("lastVersion", new UpgradeController().lastVersion());
            if (getPara(0) == null || getRequest().getRequestURI().endsWith("admin/") || "login".equals(getPara(0))) {
                redirect("/admin/index");
                return null;
            } else {
                return "/admin/" + getPara(0);
            }
        } else {
            return "/admin/login";
        }
    }

    public String login() {
        if (AdminTokenThreadLocal.getUser() != null) {
            redirect("/admin/index");
            return null;
        } else {
            return "/admin/login";
        }
    }

    public void logout() {
        Cookie cookies[] = getRequest().getCookies();
        for (Cookie cookie : cookies) {
            if ("zId".equals(cookie.getName())) {
                cookie.setValue("");
                cookie.setMaxAge(adminTokenService.getSessionTimeout().intValue());
                getResponse().addCookie(cookie);
            }
            if (Constants.ADMIN_TOKEN.equals(cookie.getName())) {
                cookie.setValue("");
                cookie.setMaxAge(adminTokenService.getSessionTimeout().intValue());
                cookie.setPath("/");
                adminTokenService.setCookieDomain(getRequest(), cookie);
                getResponse().addCookie(cookie);
            }
        }
        redirect("/admin/login");
    }
}
