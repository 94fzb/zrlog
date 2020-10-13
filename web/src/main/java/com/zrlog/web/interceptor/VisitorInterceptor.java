package com.zrlog.web.interceptor;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import com.jfinal.core.JFinal;
import com.jfinal.json.Json;
import com.jfinal.kit.PathKit;
import com.zrlog.common.rest.response.ApiStandardResponse;
import com.zrlog.common.rest.response.StandardResponse;
import com.zrlog.common.vo.TemplateVO;
import com.zrlog.business.service.TemplateService;
import com.zrlog.util.ZrLogUtil;
import com.zrlog.web.config.ZrLogConfig;
import com.zrlog.web.handler.GlobalResourceHandler;
import com.zrlog.web.render.BlogFrontendFreeMarkerRender;

import java.io.File;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 目前这里分为3种情况，
 * 1. /post / 前台页面，扩展其他页面请使用 /** 这样的。
 * 2. /install 用于安装向导的路由
 * 3. /api 用于将文章相关的数据数据转化JSON数据
 */
class VisitorInterceptor implements Interceptor {

    @Override
    public void intercept(Invocation ai) {
        String actionKey = ai.getActionKey();
        if (actionKey.startsWith("/install")) {
            ai.invoke();
        } else if (actionKey.startsWith("/api/public")) {
            ai.invoke();
            ai.getController().renderJson(RenderUtils.tryWrapperToStandardResponse(ai.getReturnValue()));
        } else if (actionKey.startsWith("/api/install")) {
            if (ZrLogConfig.isInstalled()) {
                StandardResponse standardResponse = new StandardResponse();
                standardResponse.setError(1);
                standardResponse.setMessage((String) ((Map) ai.getController().getAttr("_res")).get("installed"));
                ai.getController().renderJson(standardResponse);
            } else {
                ai.invoke();
                ai.getController().renderJson(RenderUtils.tryWrapperToStandardResponse(ai.getReturnValue()));
            }
        } else {
            if (ZrLogConfig.isInstalled()) {
                if (actionKey.startsWith("/api")) {
                    apiPermission(ai);
                } else if (actionKey.startsWith("/")) {
                    visitorPermission(ai);
                }
            }
        }
    }

    /**
     * 查询出来的文章数据存放在request域里面，通过判断Key，选择对应需要渲染的模板文件
     *
     * @param ai
     */
    private void visitorPermission(Invocation ai) {
        ai.invoke();
        String templateName = ai.getReturnValue();
        if (templateName == null) {
            return;
        }
        GlobalResourceHandler.printUserTime("Template before");
        String templatePath = TemplateHelper.fullTemplateInfo(ai.getController(), true);
        GlobalResourceHandler.printUserTime("Template after");
        TemplateVO templateVO = new TemplateService().getTemplateVO(JFinal.me().getContextPath(), new File(PathKit.getWebRootPath() + templatePath));
        String ext = ZrLogUtil.getViewExt(templateVO.getViewType());
        if (ai.getController().getAttr("log") != null) {
            ai.getController().setAttr("pageLevel", 1);
        } else if (ai.getController().getAttr("data") != null) {
            if ("/".equals(ai.getActionKey()) && new File(PathKit.getWebRootPath() + templatePath + "/" + templateName + ext).exists()) {
                ai.getController().setAttr("pageLevel", 2);
            } else {
                templateName = "page";
                ai.getController().setAttr("pageLevel", 1);
            }
        } else {
            ai.getController().setAttr("pageLevel", 2);
        }
        fullDevData(ai.getController());
        String viewPath = templatePath + "/" + templateName + ext;
        if (ext.equals(".ftl")) {
            BlogFrontendFreeMarkerRender render = new BlogFrontendFreeMarkerRender(viewPath);
            render.setContext(ai.getController().getRequest(), ai.getController().getResponse());
            ai.getController().render(render);
        } else {
            ai.getController().render(viewPath);
        }

    }

    /**
     * 方便开发环境使用，将Servlet的Request域的数据转化JSON字符串，配合dev.jsp使用，定制主题更加方便
     *
     * @param controller
     */
    private void fullDevData(Controller controller) {
        boolean dev = JFinal.me().getConstants().getDevMode();
        controller.setAttr("dev", dev);
        if (dev) {
            Map<String, Object> attrMap = new LinkedHashMap<>();
            Enumeration<String> enumerations = controller.getAttrNames();
            while (enumerations.hasMoreElements()) {
                String key = enumerations.nextElement();
                attrMap.put(key, controller.getAttr(key));
            }
            controller.setAttr("requestScopeJsonString", Json.getJson().toJson(attrMap));
        }
    }

    private void apiPermission(Invocation ai) {
        ai.invoke();
        ApiStandardResponse apiStandardResponse = new ApiStandardResponse();
        if (ai.getController().getAttr("log") != null) {
            apiStandardResponse.setData(ai.getController().getAttr("log"));
        } else if (ai.getController().getAttr("data") != null) {
            apiStandardResponse.setData(ai.getController().getAttr("data"));
        } else {
            apiStandardResponse.setData(ai.getReturnValue());
        }
        ai.getController().renderJson(apiStandardResponse);
    }
}
