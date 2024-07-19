package com.hibegin.common.util.http.handle;

import com.hibegin.common.util.IOUtil;

import java.io.IOException;
import java.io.InputStream;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Objects;
import java.util.Optional;
import java.util.zip.GZIPInputStream;

public class HttpStringHandle extends HttpHandle<String> {

    private int statusCode;


    public int getStatusCode() {
        return statusCode;
    }

    @Override
    public boolean handle(HttpRequest request, HttpResponse<InputStream> response) {
        statusCode = response.statusCode();
        Optional<String> s = response.headers().firstValue("Content-Encoding");
        if (s.isPresent() && Objects.equals(s.get(), "gzip")) {
            try {
                setT(IOUtil.getStringInputStream(new GZIPInputStream(response.body())));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            setT(IOUtil.getStringInputStream(response.body()));
        }
        return false;
    }
}
