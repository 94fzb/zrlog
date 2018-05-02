package com.zrlog.web.interceptor;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.JFinal;
import com.zrlog.common.Constants;
import com.zrlog.web.config.ZrLogConfig;
import com.zrlog.web.controller.BaseController;
import com.zrlog.web.util.WebTools;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 通过获取调用者IP地址进行黑名单的检查，主要用于防止恶意访问，可以在后台管理界面添加记录。
 */
public class BlackListInterceptor implements Interceptor {

    @Override
    public void intercept(Invocation invocation) {
        if (!ZrLogConfig.isInstalled() && "/".equals(invocation.getController().getRequest().getRequestURI())) {
            invocation.getController().render("/install/index" + ZrLogConfig.getTemplateExt());
        } else {
            if (invocation.getController() instanceof BaseController) {
                BaseController baseController = (BaseController) invocation.getController();
                String ipStr = (String) Constants.webSite.get("blackList");
                if (ipStr != null) {
                    Set<String> ipSet = new HashSet<>(Arrays.asList(ipStr.split(",")));
                    String requestIP = WebTools.getRealIp(baseController.getRequest());
                    if (ipSet.contains(requestIP)) {
                        baseController.render(JFinal.me().getConstants().getErrorView(403));
                    } else {
                        invocation.invoke();
                    }
                } else {
                    invocation.invoke();
                }
            } else {
                invocation.invoke();
            }
        }
    }
}
