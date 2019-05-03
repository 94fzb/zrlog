package com.zrlog.web.controller.admin.page;

import com.hibegin.common.util.StringUtils;
import com.jfinal.core.Controller;
import com.jfinal.core.JFinal;
import com.zrlog.common.Constants;
import com.zrlog.common.response.CheckVersionResponse;
import com.zrlog.model.Comment;
import com.zrlog.model.Log;
import com.zrlog.web.token.AdminTokenService;
import com.zrlog.web.token.AdminTokenThreadLocal;
import com.zrlog.service.TemplateService;
import com.zrlog.util.ZrLogUtil;
import com.zrlog.web.config.ZrLogConfig;
import com.zrlog.web.controller.BaseController;
import com.zrlog.web.controller.admin.api.UpgradeController;
import com.zrlog.web.interceptor.TemplateHelper;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

public class AdminPageController extends BaseController {

    private static final String LOGOUT_URI = "/admin/login";

    private AdminTokenService adminTokenService = new AdminTokenService();

    private TemplateService templateService = new TemplateService();

    public String index() {
        if (AdminTokenThreadLocal.getUser() != null) {
            initIndex(getRequest());
            if (getPara(0) == null || getRequest().getRequestURI().endsWith("admin/") || "login".equals(getPara(0))) {
                redirect(Constants.ADMIN_INDEX);
                return null;
            } else {
                if ("dashboard".equals(getPara(0))) {
                    fillStatistics();
                } else if ("website".equals(getPara(0))) {
                    setAttr("templates", templateService.getAllTemplates(getRequest().getContextPath(), TemplateHelper.getTemplatePathByCookie(getRequest().getCookies())));
                }
                return "/admin/" + getPara(0);
            }
        } else {
            return LOGOUT_URI;
        }
    }

    public static void initIndex(HttpServletRequest request) {
        request.setAttribute("previewDb", com.zrlog.web.config.ZrLogConfig.isPreviewDb());

        CheckVersionResponse response = new UpgradeController().lastVersion();
        JFinal.me().getServletContext().setAttribute("lastVersion", response);
        List<Comment> commentList = new Comment().findHaveReadIsFalse();
        if (commentList != null && !commentList.isEmpty()) {
            request.setAttribute("noReadComments", commentList);
            for (Comment comment : commentList) {
                if (StringUtils.isEmpty(comment.get("header"))) {
                    comment.set("header", Constants.DEFAULT_HEADER);
                }
            }
        }
        request.setAttribute("lastVersion", response);
        request.setAttribute("zrlog", ZrLogConfig.blogProperties);
        request.setAttribute("system", ZrLogConfig.SYSTEM_PROP);
    }


    private void fillStatistics() {
        getRequest().setAttribute("commCount", new Comment().count());
        getRequest().setAttribute("toDayCommCount", new Comment().countToDayComment());
        getRequest().setAttribute("clickCount", new Log().sumClick());
        getRequest().setAttribute("articleCount", new Log().adminCount());
    }

    public String login() {
        previewField(this);
        if (AdminTokenThreadLocal.getUser() != null) {
            redirect(Constants.ADMIN_INDEX);
            return null;
        }
        return LOGOUT_URI;
    }

    public static void previewField(Controller controller) {
        if (ZrLogUtil.isPreviewMode()) {
            controller.getRequest().setAttribute("userName", System.getenv("DEFAULT_USERNAME"));
            controller.getRequest().setAttribute("password", System.getenv("DEFAULT_PASSWORD"));
        }
    }

    public void logout() {
        Cookie[] cookies = getRequest().getCookies();
        for (Cookie cookie : cookies) {
            if ("zId".equals(cookie.getName())) {
                cookie.setValue("");
                cookie.setMaxAge(Constants.getSessionTimeout().intValue());
                getResponse().addCookie(cookie);
            }
            if (Constants.ADMIN_TOKEN.equals(cookie.getName())) {
                cookie.setValue("");
                cookie.setMaxAge(Constants.getSessionTimeout().intValue());
                cookie.setPath("/");
                adminTokenService.setCookieDomain(getRequest(), cookie);
                getResponse().addCookie(cookie);
            }
        }
        redirect(LOGOUT_URI);
    }
}
