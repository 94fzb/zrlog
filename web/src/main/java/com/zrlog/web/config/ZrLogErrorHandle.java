package com.zrlog.web.config;

import com.google.gson.Gson;
import com.hibegin.common.util.LoggerUtil;
import com.hibegin.http.server.api.HttpErrorHandle;
import com.hibegin.http.server.api.HttpRequest;
import com.hibegin.http.server.api.HttpResponse;
import com.hibegin.http.server.util.PathUtil;
import com.zrlog.common.exception.AbstractBusinessException;
import com.zrlog.common.rest.response.ApiStandardResponse;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 全局异常处理
 */
public class ZrLogErrorHandle implements HttpErrorHandle {

    private static final Logger LOGGER = LoggerUtil.getLogger(ZrLogErrorHandle.class);

    private final int httpStatueCode;

    public ZrLogErrorHandle(int httpStatueCode) {
        this.httpStatueCode = httpStatueCode;
    }

    /**
     * @param request
     * @param response
     * @param e
     */
    @Override
    public void doHandle(HttpRequest request, HttpResponse response, Throwable e) {
        LOGGER.log(Level.SEVERE, "handle error", e);
        if (request.getUri().startsWith("/api")) {
            if (e instanceof AbstractBusinessException ee) {
                ApiStandardResponse error = new ApiStandardResponse();
                error.setError(ee.getError());
                error.setMessage(ee.getMessage());
                response.write(new ByteArrayInputStream(new Gson().toJson(error).getBytes()), 200);
            } else {
                ApiStandardResponse error = new ApiStandardResponse();
                error.setError(9999);
                error.setMessage(e.getMessage());
                response.write(new ByteArrayInputStream(new Gson().toJson(error).getBytes()), 200);
            }
            return;
        }
        InputStream inputStream = PathUtil.getConfInputStream("/error/" + httpStatueCode + ".html");
        if (Objects.isNull(inputStream)) {
            inputStream = PathUtil.getConfInputStream("/error/500.html");
        }
        if (Objects.isNull(inputStream)) {
            response.renderCode(500);
            return;
        }
        response.write(inputStream, httpStatueCode);
    }
}