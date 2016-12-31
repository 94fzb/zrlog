package com.fzb.blog.web.incp;

import com.fzb.blog.web.controller.BaseController;
import com.fzb.blog.web.util.WebTools;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.JFinal;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * 通过获取调用者IP地址进行黑名单的检查，主要用于防止恶意访问，可以在后台管理界面添加记录。
 */
public class BlackListInterceptor implements Interceptor {

    @Override
    public void intercept(Invocation invocation) {
        if (invocation.getController() instanceof BaseController) {
            BaseController baseController = (BaseController) invocation.getController();
            String ipStr = baseController.getStrValueByKey("blackList");
            if (ipStr != null) {
                Set<String> ipSet = new HashSet<String>(Arrays.asList(ipStr.split(",")));
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
