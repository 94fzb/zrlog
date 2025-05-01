package com.zrlog.web.inteceptor;

import com.hibegin.http.server.api.HttpRequest;
import com.hibegin.http.server.api.HttpResponse;
import com.hibegin.http.server.api.Interceptor;
import com.hibegin.http.server.execption.NotFindResourceException;

public class DefaultInterceptor implements Interceptor {
    @Override
    public boolean doInterceptor(HttpRequest request, HttpResponse response) {
        if (request.getUri().startsWith("/api")) {
            throw new NotFindResourceException("Not find resource");
        }
        response.renderCode(404);
        return true;
    }
}
