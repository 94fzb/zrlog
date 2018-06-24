package com.zrlog.web.interceptor;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.jfinal.core.JFinal;
import com.jfinal.kit.PathKit;
import com.zrlog.common.Constants;
import com.zrlog.util.I18NUtil;

/**
 * 多语言（国际化）相关的配置，这里并没有直接继承至JFinal提供的I18N方案
 */
public class MyI18NInterceptor implements Interceptor {

    @Override
    public void intercept(Invocation inv) {
        if (Constants.IN_JAR) {
            I18NUtil.addToRequest(null, inv.getController().getRequest(), JFinal.me().getConstants().getDevMode(), false);
        } else {
            I18NUtil.addToRequest(PathKit.getRootClassPath(), inv.getController().getRequest(), JFinal.me().getConstants().getDevMode(), false);
        }
        inv.invoke();
    }
}
