package com.fzb.blog.incp;

import com.fzb.blog.controller.BaseController;
import com.fzb.blog.controller.ManageController;
import com.fzb.blog.util.InstallUtil;
import com.jfinal.aop.Invocation;
import com.jfinal.aop.PrototypeInterceptor;
import com.jfinal.kit.PathKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zhengchangchun 登陆拦截器,负责权限控制
 */
public class LoginInterceptor extends PrototypeInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginInterceptor.class);

    private void visitorPermission(Invocation ai) {
        ai.invoke();
        String basePath = getBaseTemplatePath(ai);
        MyI18NInterceptor.addToRequest(PathKit.getWebRootPath() + basePath + "/language/", ai.getController().getRequest());
        if (ai.getController() instanceof BaseController) {
            ((BaseController) ai.getController()).fullTemplateSetting();
        }
        if (ai.getController().getAttr("log") != null) {
            ai.getController().setAttr("pageLevel", 1);
            ai.getController().render(basePath + "/detail.jsp");
        } else if (ai.getController().getAttr("data") != null) {
            if (ai.getActionKey().equals("/") && new File(PathKit.getWebRootPath() + basePath + "/index.jsp").exists()) {
                ai.getController().setAttr("pageLevel", 2);
                ai.getController().render(basePath + "/index.jsp");
            } else {
                ai.getController().setAttr("pageLevel", 1);
                ai.getController().render(basePath + "/page.jsp");
            }
        } else {
            ai.getController().setAttr("pageLevel", 2);
            ai.getController().render(basePath + "/index.jsp");
        }
    }

    private void apiPermission(Invocation ai) {
        ai.invoke();
        if (ai.getController().getAttr("log") != null) {
            ai.getController().renderJson(ai.getController().getAttr("log"));
        } else if (ai.getController().getAttr("data") != null) {
            ai.getController().renderJson(ai.getController().getAttr("data"));
        } else {
            Map<String, Object> error = new HashMap<String, Object>();
            error.put("status", 500);
            error.put("message", "unSupport");
            ai.getController().renderJson(error);
        }
    }

    private void installPermission(Invocation ai) {
        if (!new InstallUtil(PathKit.getWebRootPath() + "/WEB-INF")
                .checkInstall()) {
            ai.invoke();
        } else {
            ai.getController().getRequest().getSession();
            ai.getController().render("/install/forbidden.jsp");
        }
    }

    private void adminPermission(Invocation ai) {
        if (ai.getController().getSession().getAttribute("user") != null) {
            ai.getController().setAttr("user", ai.getController().getSession().getAttribute("user"));
            getBaseTemplatePath(ai);
            try {
                ai.invoke();
            } catch (Exception e) {
                e.printStackTrace();
                ((ManageController) ai.getController()).renderInternalServerErrorPage();
            }
            // 存在消息提示
            if (ai.getController().getRequest().getAttribute("message") != null) {
                ai.getController().render("/admin/message.jsp");
            }
        } else if (ai.getMethodName().equals("login")) {
            ai.invoke();
        } else {
            HttpServletRequest request = ai.getController().getRequest();
            ai.getController().redirect(request.getContextPath()
                    + "/admin/login?redirectFrom="
                    + request.getRequestURL() + (request.getQueryString() != null ? "?" + request.getQueryString() : ""));
        }
    }

    private String getBaseTemplatePath(Invocation ai) {
        String basePath = ((BaseController) ai.getController()).getTemplatePath();
        Cookie[] cookies = ai.getController().getRequest().getCookies();
        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("template") && cookie.getValue().startsWith("/include/templates/")) {
                    basePath = cookie.getValue();
                    break;
                }
            }
        }
        if (!new File(PathKit.getWebRootPath() + basePath).exists()) {
            basePath = BaseController.getDefaultTemplatePath();
        }
        ai.getController().getRequest().setAttribute("template", basePath);
        return basePath;
    }

    @Override
    public void doIntercept(Invocation invocation) {
        try {
            if (invocation.getActionKey().startsWith("/post")
                    || invocation.getActionKey().equals("/")) {
                visitorPermission(invocation);
            } else if (invocation.getActionKey().startsWith("/api")) {
                apiPermission(invocation);
            } else if (invocation.getActionKey().startsWith("/admin")) {
                adminPermission(invocation);
            } else if (invocation.getActionKey().startsWith("/install")) {
                installPermission(invocation);
            } else {
                // 其他未知情况也放行
                invocation.invoke();
            }
        } catch (Exception e) {
            LOGGER.error("permission interceptor exception ", e);
        }
    }
}
