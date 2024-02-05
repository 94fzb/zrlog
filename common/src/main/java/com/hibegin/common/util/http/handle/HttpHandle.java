package com.hibegin.common.util.http.handle;

import java.io.InputStream;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public abstract class HttpHandle<T> {

    private T t;

    public Class<T> getClazz() {
        Type sType = getClass().getGenericSuperclass();
        Type[] generics = ((ParameterizedType) sType).getActualTypeArguments();

        Class<T> mTClass = (Class) generics[0];
        return mTClass;
    }

    public T getT() {
        return this.t;
    }

    public void setT(T t) {
        this.t = t;
    }

    public abstract boolean handle(HttpRequest request, HttpResponse<InputStream> response);
}
