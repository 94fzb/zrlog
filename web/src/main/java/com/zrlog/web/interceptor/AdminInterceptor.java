package com.zrlog.web.interceptor;

import com.hibegin.common.util.ExceptionUtils;
import com.hibegin.common.util.IOUtil;
import com.hibegin.common.util.StringUtils;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import com.jfinal.core.JFinal;
import com.jfinal.kit.PathKit;
import com.jfinal.render.FreeMarkerRender;
import com.zrlog.common.Constants;
import com.zrlog.common.response.ExceptionResponse;
import com.zrlog.common.vo.AdminTokenVO;
import com.zrlog.model.User;
import com.zrlog.util.I18nUtil;
import com.zrlog.web.annotation.RefreshCache;
import com.zrlog.web.cache.CacheService;
import com.zrlog.web.controller.admin.page.AdminPageController;
import com.zrlog.web.handler.GlobalResourceHandler;
import com.zrlog.web.token.AdminTokenService;
import com.zrlog.web.token.AdminTokenThreadLocal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.FileInputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * 负责全部后台请求的处理（/admin/plugins/*,/api/admin/plugins/* 除外）
 */
class AdminInterceptor implements Interceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdminInterceptor.class);
    private AdminTokenService adminTokenService = new AdminTokenService();
    private CacheService cacheService = new CacheService();

    @Override
    public void intercept(Invocation inv) {
        adminPermission(inv);
    }

    /**
     * 为了规范代码，这里做了一点类是Spring的ResponseEntity的东西，及通过方法的返回值来判断是应该返回页面还会对应JSON数据
     * 具体方式看 AdminRouters，这里用到了 ThreadLocal
     *
     * @param ai
     */
    private void adminPermission(Invocation ai) {
        Controller controller = ai.getController();
        AdminTokenVO adminTokenVO = adminTokenService.getAdminTokenVO(controller.getRequest());
        if (adminTokenVO != null) {
            try {
                User user = new User().findById(adminTokenVO.getUserId());
                if (StringUtils.isEmpty(user.getStr("header"))) {
                    user.set("header", Constants.DEFAULT_HEADER);
                }
                controller.setAttr("user", user);
                controller.setAttr("protocol", adminTokenVO.getProtocol());
                TemplateHelper.fullTemplateInfo(controller, false);
                if (!"/admin/logout".equals(ai.getActionKey())) {
                    adminTokenService.setAdminToken(user, adminTokenVO.getSessionId(), adminTokenVO.getProtocol(), controller.getRequest(), controller.getResponse());
                }
                ai.invoke();
                // 存在消息提示
                if (controller.getAttr("message") != null) {
                    AdminPageController.initIndex(controller.getRequest());
                    controller.render(new FreeMarkerRender("/admin/index.ftl"));
                } else {
                    if (!tryDoRender(ai, controller)) {
                        controller.renderHtml(IOUtil.getStringInputStream(new FileInputStream(PathKit.getWebRootPath() + Constants.NOT_FOUND_PAGE)));
                    }
                }
            } catch (Exception e) {
                LOGGER.error("", e);
                exceptionHandler(ai, e);
            } finally {
                AdminTokenThreadLocal.remove();
            }
        } else if ("/admin/login".equals(ai.getActionKey()) || "/api/admin/login".equals(ai.getActionKey())) {
            ai.invoke();
            tryDoRender(ai, controller);
        } else {
            blockUnLoginRequestHandler(ai);
        }
    }

    private void exceptionHandler(Invocation ai, Exception e) {
        if (ai.getActionKey().startsWith("/api")) {
            ExceptionResponse exceptionResponse = new ExceptionResponse();
            exceptionResponse.setError(1);
            exceptionResponse.setMessage(e.getMessage());
            exceptionResponse.setStack(ExceptionUtils.recordStackTraceMsg(e));
            ai.getController().renderJson(exceptionResponse);
        } else {
            ai.getController().redirect(Constants.ERROR_PAGE);
        }
    }

    private void blockUnLoginRequestHandler(Invocation ai) {
        if (ai.getActionKey().startsWith("/api")) {
            ExceptionResponse exceptionResponse = new ExceptionResponse();
            exceptionResponse.setError(1);
            exceptionResponse.setMessage(I18nUtil.getStringFromRes("admin.session.timeout"));
            ai.getController().renderJson(exceptionResponse);
        } else {
            try {
                loginRender(ai.getController());
            } catch (Exception e) {
                LOGGER.error("", e);
            }
        }
    }

    /**
     * 在重定向到登陆页面时，同时携带了当前的请求路径，方便登陆成功后的跳转
     */
    private void loginRender(Controller controller) throws MalformedURLException {
        HttpServletRequest request = controller.getRequest();
        String url = controller.getRequest().getRequestURL().toString();
        URL tUrl = new URL(url);
        AdminPageController.previewField(controller);
        controller.getRequest().setAttribute("redirectFrom", tUrl.getPath() + (request.getQueryString() != null ? "?" + request.getQueryString() : ""));
        controller.render(new FreeMarkerRender("/admin/login.ftl"));

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
        if (ai.getMethod().getAnnotation(RefreshCache.class) != null) {
            cacheService.refreshInitDataCache(GlobalResourceHandler.CACHE_HTML_PATH, controller, true);
            if (JFinal.me().getConstants().getDevMode()) {
                LOGGER.info("{} trigger refresh cache", controller.getRequest().getRequestURI());
            }
        }
        boolean rendered = false;
        if (returnValue != null) {
            if (ai.getActionKey().startsWith("/api/admin")) {
                controller.renderJson((Object) ai.getReturnValue());
                rendered = true;
            } else if (ai.getActionKey().startsWith("/admin") && returnValue instanceof String) {
                //返回值，约定：admin 开头的不写模板类型，其他要写全
                if (!returnValue.toString().endsWith(".jsp") && returnValue.toString().startsWith("/admin")) {
                    String templatePath = returnValue.toString() + ".ftl";
                    if (AdminInterceptor.class.getResourceAsStream(Constants.FTL_VIEW_PATH + templatePath) != null) {
                        controller.render(new FreeMarkerRender(templatePath));
                        rendered = true;
                    } else {
                        rendered = false;
                    }
                } else {
                    controller.render(returnValue.toString());
                    rendered = true;
                }
            }
        } else {
            rendered = true;
        }
        return rendered;
    }
}
