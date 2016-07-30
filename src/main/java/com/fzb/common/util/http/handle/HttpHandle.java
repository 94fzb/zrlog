package com.fzb.common.util.http.handle;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpRequestBase;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class HttpHandle<T> {
    protected HttpRequest request;
    protected HttpResponse response;
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

    /**
     * 处理各种Http响应信息
     *
     * @param request
     * @param response
     * @return 根据返回值确定是否关闭流
     */
    public abstract boolean handle(HttpRequestBase request, HttpResponse response);
}
