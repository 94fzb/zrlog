package com.zrlog.blog.web.interceptor;

import com.hibegin.http.server.api.HandleAbleInterceptor;
import com.hibegin.http.server.api.HttpRequest;
import com.hibegin.http.server.api.HttpResponse;
import com.hibegin.http.server.web.MethodInterceptor;
import com.zrlog.business.exception.InstalledException;
import com.zrlog.business.util.InstallUtils;

import java.util.Objects;

public class BlogInstallInterceptor implements HandleAbleInterceptor {
    @Override
    public boolean isHandleAble(HttpRequest request) {
        return Objects.equals(request.getUri(), "/install") || request.getUri().startsWith("/api/install/");
    }

    @Override
    public boolean doInterceptor(HttpRequest request, HttpResponse response) throws Exception {
        String target = request.getUri();
        if (target.startsWith("/api/install/") && InstallUtils.isInstalled()) {
            throw new InstalledException();
        }
        new MethodInterceptor().doInterceptor(request, response);
        return false;
    }
}
