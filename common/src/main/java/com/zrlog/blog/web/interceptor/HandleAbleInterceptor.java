package com.zrlog.blog.web.interceptor;

import com.hibegin.http.server.api.HttpRequest;
import com.hibegin.http.server.api.Interceptor;

public interface HandleAbleInterceptor extends Interceptor {
    boolean isHandleAble(HttpRequest request);
}
