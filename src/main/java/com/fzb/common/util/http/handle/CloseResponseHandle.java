package com.fzb.common.util.http.handle;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;

public class CloseResponseHandle extends HttpHandle<CloseableHttpResponse> {

    @Override
    public boolean handle(HttpRequestBase request, HttpResponse response) {
        setT((CloseableHttpResponse) response);
        return false;
    }
}
