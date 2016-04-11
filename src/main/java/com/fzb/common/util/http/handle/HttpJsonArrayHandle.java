package com.fzb.common.util.http.handle;

import com.fzb.common.util.IOUtil;
import flexjson.JSONDeserializer;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpRequestBase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HttpJsonArrayHandle<T> extends HttpHandle<List<T>> {
    @Override
    public boolean handle(HttpRequestBase request, HttpResponse response) {
        try {
            String jsonStr = IOUtil.getStringInputStream(response.getEntity().getContent());
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                setT(new JSONDeserializer<List<T>>().deserialize(jsonStr));
            } else {
                setT(new ArrayList<T>());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }
}
