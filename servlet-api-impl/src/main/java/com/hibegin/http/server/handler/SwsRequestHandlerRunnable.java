package com.hibegin.http.server.handler;

import com.hibegin.http.server.api.HttpRequest;
import com.hibegin.http.server.api.HttpResponse;
import jakarta.servlet.http.HttpServletResponse;

public class SwsRequestHandlerRunnable extends HttpRequestHandlerRunnable {

    private final HttpServletResponse servletResponse;

    public SwsRequestHandlerRunnable(HttpRequest request,
                                     HttpResponse response,
                                     HttpServletResponse servletResponse) {
        super(request, response);
        this.servletResponse = servletResponse;
    }

    @Override
    protected boolean isOpen() {
        return true;
    }
}
