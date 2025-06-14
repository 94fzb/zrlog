package com.hibegin.http.server.impl;


import com.hibegin.http.server.api.HttpRequest;
import com.hibegin.http.server.config.ResponseConfig;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class SwsHttpServletResponseWrapper extends SimpleHttpResponse {

    private final HttpServletResponse rawServletResponse;

    public SwsHttpServletResponseWrapper(HttpRequest request, ResponseConfig responseConfig, HttpServletResponse rawServletResponse) {
        super(request, responseConfig);
        this.rawServletResponse = rawServletResponse;
    }

    @Override
    protected void putHeader(String key, String value) {
        if (Objects.equals("Transfer-Encoding", key)) {
            return;
        }
        if (Objects.equals(key, "Server")) {
            return;
        }
        rawServletResponse.addHeader(key, value);
    }

    @Override
    protected byte[] toChunked(byte[] inputBytes) {
        return inputBytes;
    }

    @Override
    protected void removeHeader(String key) {
        if (Objects.equals(key, "Content-Length")) {
            rawServletResponse.setHeader("Content-Length", null);
        }
    }

    @Override
    protected byte[] wrapperBaseResponseHeader(int statusCode) {
        rawServletResponse.setStatus(statusCode);
        return super.wrapperBaseResponseHeader(statusCode);
    }

    @Override
    public Map<String, String> getHeader() {
        Collection<String> headerNames = rawServletResponse.getHeaderNames();
        Map<String, String> headerMap = new LinkedHashMap<>();
        for (String headerName : headerNames) {
            headerMap.put(headerName, rawServletResponse.getHeader(headerName));
        }
        return headerMap;
    }

    @Override
    public void redirect(String url) {
        if (url.startsWith("/")) {
            super.redirect(request.getContextPath() + url);
        } else {
            super.redirect(url);
        }
    }

    @Override
    protected void send(byte[] bytes, boolean body, boolean close) {
        if (body) {
            try {
                rawServletResponse.getOutputStream().write(bytes);
                if (close) {
                    rawServletResponse.getOutputStream().close();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
