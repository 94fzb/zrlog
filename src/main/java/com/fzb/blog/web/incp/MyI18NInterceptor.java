package com.fzb.blog.web.incp;

import com.fzb.blog.util.I18NUtil;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.kit.PathKit;

public class MyI18NInterceptor implements Interceptor {

    @Override
    public void intercept(Invocation inv) {
        I18NUtil.addToRequest(PathKit.getRootClassPath(), inv.getController().getRequest());
        inv.invoke();
    }
}
