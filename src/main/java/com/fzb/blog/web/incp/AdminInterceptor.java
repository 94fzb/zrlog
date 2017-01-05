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

/**
 * 负责全部后台请求的处理（/admin/plugins/*除外），目前还是使用的Session的方式保存用户是否登陆，及管理员登陆成功后将管理员数据存放在Session
 * 中用于标示权限。
 */
class AdminInterceptor implements Interceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdminInterceptor.class);

    @Override
    public void intercept(Invocation inv) {
        adminPermission(inv);
    }

    /**
     * 为了规范代码，这里做了一点类是Spring的ResponseEntity的东西，及通过方法的返回值来判断是应该返回页面还会对应JSON数据
     * 预定方式看 AdminRouters
     * 这里用到了 ThreadLocal，后期会替换掉Session的方式。
     *
     * @param ai
     */
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
                if (JFinal.me().getConstants().getViewType() == ViewType.JSP) {
                    controller.render(Constants.ADMIN_ERROR_PAGE + ".jsp");
                }
            } finally {
                AdminTokenThreadLocal.remove();
            }
        } else if (ai.getActionKey().equals("/admin/login") || ai.getActionKey().equals("/api/admin/login")) {
            ai.invoke();
            tryDoRender(ai, controller);
        } else {
            //在重定向到登陆页面时，同时携带了当前的请求路径，方便登陆成功后的跳转
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

    /**
     * 尝试通过Controller的放回值来进行数据的渲染
     *
     * @param ai
     * @param controller
     * @return true 表示已经渲染数据了，false 表示并未按照约定编写，及没有进行渲染
     */
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
