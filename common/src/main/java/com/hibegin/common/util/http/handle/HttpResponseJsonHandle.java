package com.hibegin.common.util.http.handle;

import com.google.gson.Gson;
import com.hibegin.common.util.IOUtil;

import java.io.InputStream;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class HttpResponseJsonHandle<T> extends HttpHandle<T> {

    private final Class<T> clazz;

    public HttpResponseJsonHandle(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public boolean handle(HttpRequest request, HttpResponse<InputStream> response) {
        String jsonStr = IOUtil.getStringInputStream(response.body());
        if (response.statusCode() == 200) {
            setT(new Gson().fromJson(jsonStr, clazz));
        } else {
            setT(null);
        }
        return true;
    }
}
