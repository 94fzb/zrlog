package com.fzb.common.util.http.handle;

import com.fzb.common.util.IOUtil;
import flexjson.JSONDeserializer;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpRequestBase;

import java.io.IOException;
import java.util.Map;

public class HttpJsonHandle extends HttpHandle<Map> {
    @Override
    public boolean handle(HttpRequestBase request, HttpResponse response) {
        try {
            String jsonStr = IOUtil.getStringInputStream(response.getEntity().getContent());
            setT(new JSONDeserializer<Map>().deserialize(jsonStr));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }
}
