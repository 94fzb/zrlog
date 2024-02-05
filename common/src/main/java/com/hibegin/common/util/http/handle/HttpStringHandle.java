package com.hibegin.common.util.http.handle;

import com.hibegin.common.util.IOUtil;

import java.io.InputStream;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class HttpStringHandle extends HttpHandle<String> {

    private int statusCode;


    public int getStatusCode() {
        return statusCode;
    }

    @Override
    public boolean handle(HttpRequest request, HttpResponse<InputStream> response) {
        setT(IOUtil.getStringInputStream(response.body()));
        return false;
    }
}
