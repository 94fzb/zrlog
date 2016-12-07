package com.fzb.blog.web.incp;

import com.fzb.blog.common.Constants;
import com.fzb.blog.model.User;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import com.jfinal.core.JFinal;
import com.jfinal.kit.PathKit;
import com.jfinal.render.ViewType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

class AdminInterceptor implements Interceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdminInterceptor.class);

    @Override
    public void intercept(Invocation inv) {
        adminPermission(inv);
    }

    private void adminPermission(Invocation ai) {
        Controller controller = ai.getController();
        if (controller.getSession().getAttribute("user") != null) {
            try {
                User user = (User) controller.getSession().getAttribute("user");
                controller.setAttr("user", user);
                TemplateHelper.fullTemplateInfo(controller);
                AdminTokenThreadLocal.setUser(user);
                ai.invoke();
                if (!tryDoRender(ai, controller)) {
                    // 存在消息提示
                    if (controller.getRequest().getAttribute("message") != null) {
                        controller.render("/admin/message.jsp");
                    }
                }
            } catch (Exception e) {
                LOGGER.error("", e);
                controller.render(Constants.ADMIN_ERROR_PAGE);
            } finally {
                AdminTokenThreadLocal.remove();
            }
        } else if (ai.getActionKey().equals("/admin/login") || ai.getActionKey().equals("/api/admin/login")) {
            ai.invoke();
            tryDoRender(ai, controller);
        } else {
            HttpServletRequest request = ai.getController().getRequest();
            try {
                ai.getController().redirect(request.getContextPath()
                        + "/admin/login?redirectFrom="
                        + request.getRequestURL() + URLEncoder.encode(request.getQueryString() != null ? "?" + request.getQueryString() : "", "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                LOGGER.error("", e);
            }
        }
    }

    private boolean tryDoRender(Invocation ai, Controller controller) {
        Object returnValue = ai.getReturnValue();
        boolean rendered = false;
        if (returnValue != null) {
            if (ai.getActionKey().startsWith("/api/admin")) {
                controller.renderJson(ai.getReturnValue());
                rendered = true;
            } else if (ai.getActionKey().startsWith("/admin") && returnValue instanceof String) {
                if (JFinal.me().getConstants().getViewType() == ViewType.JSP) {
                    String templatePath = returnValue.toString() + ".jsp";
                    if (new File(PathKit.getWebRootPath() + templatePath).exists()) {
                        controller.render(templatePath);
                        rendered = true;
                    }
                }
            }
        }
        return rendered;
    }
}
