package com.zrlog.blog.web.interceptor;

import com.hibegin.http.server.api.HandleAbleInterceptor;
import com.hibegin.http.server.api.HttpRequest;
import com.hibegin.http.server.api.HttpResponse;
import com.hibegin.http.server.web.MethodInterceptor;
import com.zrlog.business.exception.MissingInstallException;
import com.zrlog.common.Constants;

import java.util.Objects;

public class BlogApiInterceptor implements HandleAbleInterceptor {
    @Override
    public boolean isHandleAble(HttpRequest request) {
        return Objects.equals("/api", request.getUri()) || request.getUri().startsWith("/api/");
    }

    @Override
    public boolean doInterceptor(HttpRequest request, HttpResponse response) throws Exception {
        if (!Constants.zrLogConfig.isInstalled()) {
            throw new MissingInstallException();
        }
        new MethodInterceptor().doInterceptor(request, response);
        return false;
    }
}
