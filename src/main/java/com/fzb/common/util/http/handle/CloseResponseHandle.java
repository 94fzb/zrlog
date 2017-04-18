package com.fzb.common.util.http.handle;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpRequestBase;

import java.io.IOException;

public class CloseResponseHandle extends HttpHandle<CloseableHttpResponse> {

    private HttpResponse httpResponse;

    @Override
    public boolean handle(HttpRequestBase request, HttpResponse response) {
        setT((CloseableHttpResponse) response);
        this.httpResponse = response;
        return false;
    }

    public void close() throws IOException {
        if (httpResponse != null) {
            httpResponse.getEntity().getContent().close();
        }
    }
}
