package com.fzb.blog.web.incp;

import com.fzb.blog.web.config.ZrlogConfig;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.Controller;
import com.jfinal.core.JFinal;
import com.jfinal.json.Json;
import com.jfinal.kit.PathKit;
import com.jfinal.render.ViewType;

import java.io.File;
import java.util.Enumeration;
import java.util.HashMap;
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
            installPermission(ai);
        } else {
            if (ZrlogConfig.isInstalled()) {
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
        String ext = "";
        if (JFinal.me().getConstants().getViewType() == ViewType.JSP) {
            ext = ".jsp";
        }
        String basePath = TemplateHelper.fullTemplateInfo(ai.getController());
        if (ai.getController().getAttr("log") != null) {
            ai.getController().setAttr("pageLevel", 1);
        } else if (ai.getController().getAttr("data") != null) {
            if (ai.getActionKey().equals("/") && new File(PathKit.getWebRootPath() + basePath + "/" + templateName + ext).exists()) {
                ai.getController().setAttr("pageLevel", 2);
            } else {
                templateName = "page";
                ai.getController().setAttr("pageLevel", 1);
            }
        } else {
            ai.getController().setAttr("pageLevel", 2);
        }
        fullDevData(ai.getController());
        ai.getController().render(basePath + "/" + templateName + ext);
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
            Map<String, Object> attrMap = new LinkedHashMap<String, Object>();
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
        if (!ZrlogConfig.isInstalled()) {
            ai.invoke();
        } else {
            ai.getController().getRequest().getSession();
            ai.getController().render("/install/forbidden.jsp");
        }
    }
}
