package com.hibegin.common.util.http.handle;

import com.google.gson.Gson;
import com.hibegin.common.util.IOUtil;

import java.io.InputStream;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class HttpJsonArrayHandle<T> extends HttpHandle<List<T>> {


    @Override
    public boolean handle(HttpRequest request, HttpResponse<InputStream> response) {
        String jsonStr = IOUtil.getStringInputStream(response.body());
        if (response.statusCode() == 200) {
            setT(new Gson().fromJson(jsonStr, List.class));
        } else {
            setT(new ArrayList<>());
        }
        return true;
    }
}
