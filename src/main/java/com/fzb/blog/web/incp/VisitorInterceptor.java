package com.fzb.blog.web.incp;

import com.fzb.blog.web.config.ZrlogConfig;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.JFinal;
import com.jfinal.json.Json;
import com.jfinal.kit.PathKit;

import java.io.File;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

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

    private void visitorPermission(Invocation ai) {
        ai.invoke();
        String basePath = TemplateHelper.fullTemplateInfo(ai.getController());
        String path = "index.jsp";
        if (ai.getController().getAttr("log") != null) {
            ai.getController().setAttr("pageLevel", 1);
            path = "detail.jsp";
        } else if (ai.getController().getAttr("data") != null) {
            if (ai.getActionKey().equals("/") && new File(PathKit.getWebRootPath() + basePath + "/index.jsp").exists()) {
                ai.getController().setAttr("pageLevel", 2);
                path = "index.jsp";
            } else {
                ai.getController().setAttr("pageLevel", 1);
                path = "page.jsp";
            }
        } else {
            ai.getController().setAttr("pageLevel", 2);
        }
        boolean dev = JFinal.me().getConstants().getDevMode();
        ai.getController().setAttr("dev", dev);
        if (dev) {
            Map<String, Object> attrMap = new LinkedHashMap<String, Object>();
            Enumeration<String> enumerations = ai.getController().getAttrNames();
            while (enumerations.hasMoreElements()) {
                String key = enumerations.nextElement();
                attrMap.put(key, ai.getController().getAttr(key));
            }
            ai.getController().setAttr("requestScopeJsonString", Json.getJson().toJson(attrMap));
        }
        ai.getController().render(basePath + "/" + path);
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
