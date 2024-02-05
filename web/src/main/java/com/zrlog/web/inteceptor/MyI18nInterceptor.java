package com.zrlog.web.inteceptor;

import com.hibegin.http.server.api.HttpRequest;
import com.hibegin.http.server.api.HttpResponse;
import com.hibegin.http.server.api.Interceptor;
import com.zrlog.util.I18nUtil;

/**
 * 多语言（国际化）相关的配置，这里并没有直接继承至提供的I18N方案
 */
public class MyI18nInterceptor implements Interceptor {

    @Override
    public boolean doInterceptor(HttpRequest request, HttpResponse response) {
        I18nUtil.addToRequest(null, request, false);
        return true;
    }
}
