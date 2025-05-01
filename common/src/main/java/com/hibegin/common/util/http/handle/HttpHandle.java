package com.hibegin.common.util.http.handle;

import java.io.InputStream;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public abstract class HttpHandle<T> {

    private T t;

    public T getT() {
        return this.t;
    }

    public void setT(T t) {
        this.t = t;
    }

    public abstract boolean handle(HttpRequest request, HttpResponse<InputStream> response);
}
