package com.hibegin.common.util.http.handle;


import java.io.InputStream;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class CloseResponseHandle extends HttpHandle<HttpResponse<InputStream>> {

    private int statusCode;

    @Override
    public boolean handle(HttpRequest request, HttpResponse<InputStream> response) {
        this.statusCode = response.statusCode();
        setT(response);
        return true;
    }

    public int getStatusCode() {
        return statusCode;
    }
}
