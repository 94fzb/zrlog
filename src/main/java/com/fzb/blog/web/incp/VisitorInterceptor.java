package com.fzb.blog.web.incp;

import com.fzb.blog.service.InstallService;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.kit.PathKit;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

class VisitorInterceptor implements Interceptor {

    @Override
    public void intercept(Invocation ai) {
        String actionKey = ai.getActionKey();
        if (actionKey.startsWith("/install")) {
            installPermission(ai);
        } else if (actionKey.startsWith("/api")) {
            apiPermission(ai);
        } else if (actionKey.startsWith("/")) {
            visitorPermission(ai);
        }
    }

    private void visitorPermission(Invocation ai) {
        ai.invoke();
        String basePath = TemplateHelper.fullTemplateInfo(ai.getController());
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
        if (!new InstallService(PathKit.getWebRootPath() + "/WEB-INF").checkInstall()) {
            ai.invoke();
        } else {
            ai.getController().getRequest().getSession();
            ai.getController().render("/install/forbidden.jsp");
        }
    }
}
