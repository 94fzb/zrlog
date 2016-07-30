package com.fzb.common.util.http.handle;

import com.fzb.common.util.IOUtil;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpRequestBase;

import java.io.IOException;

public class HttpStringHandle extends HttpHandle<String> {
    @Override
    public boolean handle(HttpRequestBase request, HttpResponse response) {
        try {
            setT(IOUtil.getStringInputStream(response.getEntity().getContent()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }
}
