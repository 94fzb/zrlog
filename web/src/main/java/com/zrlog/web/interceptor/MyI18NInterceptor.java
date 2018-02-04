package com.zrlog.web.interceptor;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.kit.PathKit;
import com.zrlog.util.I18NUtil;

/**
 * 多语言（国际化）相关的配置，这里并没有直接继承至JFinal提供的I18N方案
 */
public class MyI18NInterceptor implements Interceptor {

    @Override
    public void intercept(Invocation inv) {
        I18NUtil.addToRequest(PathKit.getRootClassPath(), inv.getController());
        inv.invoke();
    }
}
