package com.hibegin.common.util.http.handle;


import java.io.InputStream;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class CloseResponseHandle extends HttpHandle<HttpResponse<InputStream>> {


    @Override
    public boolean handle(HttpRequest request, HttpResponse<InputStream> response) {
        setT(response);
        return true;
    }


}
