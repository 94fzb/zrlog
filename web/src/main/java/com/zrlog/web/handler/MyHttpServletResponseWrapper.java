package com.zrlog.web.handler;

import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;
import java.io.PrintWriter;

public class MyHttpServletResponseWrapper extends HttpServletResponseWrapper {

    private ResponseRenderPrintWriter responseRenderPrintWriter;

    public MyHttpServletResponseWrapper(HttpServletResponse response,ResponseRenderPrintWriter responseRenderPrintWriter) {
        super(response);
        this.responseRenderPrintWriter = responseRenderPrintWriter;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        return responseRenderPrintWriter;
    }
}
