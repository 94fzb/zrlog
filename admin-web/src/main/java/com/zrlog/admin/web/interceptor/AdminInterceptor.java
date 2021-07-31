package com.zrlog.admin.web.interceptor;

import com.hibegin.common.util.ExceptionUtils;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.ActionException;
import com.jfinal.core.Controller;
import com.jfinal.core.JFinal;
import com.zrlog.admin.business.rest.response.ExceptionResponse;
import com.zrlog.admin.web.annotation.RefreshCache;
import com.zrlog.admin.web.token.AdminTokenService;
import com.zrlog.admin.web.token.AdminTokenThreadLocal;
import com.zrlog.blog.web.util.WebTools;
import com.zrlog.business.cache.CacheService;
import com.zrlog.common.Constants;
import com.zrlog.common.exception.AbstractBusinessException;
import com.zrlog.common.vo.AdminTokenVO;
import com.zrlog.model.User;
import com.zrlog.util.RenderUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 负责全部后台请求的处理（/admin/plugins/*,/api/admin/plugins/* 除外）
 */
public class AdminInterceptor implements Interceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdminInterceptor.class);
    private final AdminTokenService adminTokenService = new AdminTokenService();
    private final CacheService cacheService = new CacheService();

    @Override
    public void intercept(Invocation inv) {
        adminPermission(inv);
    }

    /**
     * 为了规范代码，这里做了一点类是Spring的ResponseEntity的东西，及通过方法的返回值来判断是应该返回页面还会对应JSON数据
     * 具体方式看 AdminRouters，这里用到了 ThreadLocal
     */
    private void adminPermission(Invocation ai) {
        Controller controller = ai.getController();
        try {
            if (Constants.ADMIN_LOGIN_URI_PATH.equals(ai.getActionKey())) {
                AdminTokenVO adminTokenVO = adminTokenService.getAdminTokenVO(controller.getRequest());
                if (adminTokenVO != null) {
                    controller.redirect(Constants.ADMIN_URI_BASE_PATH + Constants.INDEX_URI_PATH);
                } else {
                    tryDoRender(ai);
                }
                return;
            }
            if ((Constants.ADMIN_URI_BASE_PATH + "/logout").equals(ai.getActionKey()) ||
                    ("/api" + Constants.ADMIN_LOGIN_URI_PATH).equals(ai.getActionKey())) {
                tryDoRender(ai);
                return;
            }
            AdminTokenVO adminTokenVO = adminTokenService.getAdminTokenVO(controller.getRequest());
            if (adminTokenVO == null) {
                WebTools.blockUnLoginRequestHandler(ai.getController().getRequest(), ai.getController().getResponse());
                return;
            }

            User user = new User().findById(adminTokenVO.getUserId());
            adminTokenService.setAdminToken(user, adminTokenVO.getSessionId(), adminTokenVO.getProtocol(), controller.getRequest(), controller.getResponse());
            tryDoRender(ai);
        } catch (AbstractBusinessException e) {
            ExceptionResponse response = new ExceptionResponse();
            response.setStack(ExceptionUtils.recordStackTraceMsg(e));
            response.setError(e.getError());
            response.setMessage(e.getMessage());
            controller.renderJson(response);
        } catch (ActionException e) {
            throw e;
        } catch (Exception e) {
            LOGGER.error("", e);
            if (ai.getActionKey().startsWith("/api")) {
                ExceptionResponse response = new ExceptionResponse();
                response.setStack(ExceptionUtils.recordStackTraceMsg(e));
                response.setError(9999);
                response.setMessage(e.getMessage());
                controller.renderJson(response);
            } else {
                ai.getController().redirect(Constants.ERROR_PAGE);
            }
        } finally {
            AdminTokenThreadLocal.remove();
        }
    }


    /**
     * 尝试通过Controller的放回值来进行数据的渲染
     */
    private void tryDoRender(Invocation ai) {
        ai.invoke();
        Controller controller = ai.getController();
        Object returnValue = ai.getReturnValue();
        RefreshCache annotation = ai.getMethod().getAnnotation(RefreshCache.class);
        if (annotation != null) {
            if (annotation.async()) {
                cacheService.refreshInitDataCacheAsync(controller.getRequest(), true);
            } else {
                cacheService.refreshInitDataCache(controller.getRequest(), true);
            }
            if (JFinal.me().getConstants().getDevMode()) {
                LOGGER.info("{} trigger refresh cache", controller.getRequest().getRequestURI());
            }
        }
        if (ai.getActionKey().startsWith("/api/admin")) {
            if (returnValue != null) {
                controller.renderJson(RenderUtils.tryWrapperToStandardResponse(returnValue));
            }
        }
    }
}
