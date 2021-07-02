package com.zrlog.blog.web.handler;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

public class MyHttpServletResponseWrapper extends HttpServletResponseWrapper {

    private final ResponseRenderPrintWriter responseRenderPrintWriter;

    public MyHttpServletResponseWrapper(HttpServletResponse response, ResponseRenderPrintWriter responseRenderPrintWriter) {
        super(response);
        this.responseRenderPrintWriter = responseRenderPrintWriter;
    }

    @Override
    public ResponseRenderPrintWriter getWriter() {
        return responseRenderPrintWriter;
    }

}
