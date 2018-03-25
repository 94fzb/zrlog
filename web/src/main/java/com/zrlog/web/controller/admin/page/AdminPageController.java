package com.zrlog.web.controller.admin.page;

import com.jfinal.core.JFinal;
import com.zrlog.common.Constants;
import com.zrlog.model.Comment;
import com.zrlog.model.Log;
import com.zrlog.web.controller.BaseController;
import com.zrlog.web.controller.admin.api.UpgradeController;
import com.zrlog.service.AdminTokenService;
import com.zrlog.service.AdminTokenThreadLocal;

import javax.servlet.http.Cookie;

public class AdminPageController extends BaseController {

    private AdminTokenService adminTokenService = new AdminTokenService();

    public String index() {
        if (AdminTokenThreadLocal.getUser() != null) {
            JFinal.me().getServletContext().setAttribute("noReadComments", Comment.dao.findHaveReadIsFalse());
            JFinal.me().getServletContext().setAttribute("commCount", Comment.dao.count());
            JFinal.me().getServletContext().setAttribute("toDayCommCount", Comment.dao.countToDayComment());
            JFinal.me().getServletContext().setAttribute("clickCount", Log.dao.sumAllClick());
            JFinal.me().getServletContext().setAttribute("articleCount", Log.dao.adminCount());
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
        Cookie[] cookies = getRequest().getCookies();
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
