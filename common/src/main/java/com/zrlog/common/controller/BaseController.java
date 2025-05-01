package com.zrlog.common.controller;


import com.google.gson.Gson;
import com.hibegin.common.util.IOUtil;
import com.hibegin.common.util.StringUtils;
import com.hibegin.http.server.api.HttpRequest;
import com.hibegin.http.server.api.HttpResponse;
import com.hibegin.http.server.web.Controller;
import com.zrlog.common.Validator;
import com.zrlog.common.exception.ArgsException;
import com.zrlog.common.exception.MissingRequestBodyException;
import com.zrlog.util.I18nUtil;

import java.util.Objects;

public class BaseController extends Controller {

    public BaseController() {
    }

    public BaseController(HttpRequest request, HttpResponse response) {
        super(request, response);
    }

    public String getParamWithEmptyCheck(String paramName) {
        String value = request.getParaToStr(paramName);
        if (StringUtils.isEmpty(value)) {
            throw new ArgsException(paramName);
        }
        return value;
    }

    public <T extends Validator> T getRequestBodyWithNullCheck(Class<T> clazz) {
        if (Objects.isNull(request.getInputStream())) {
            throw new MissingRequestBodyException();
        }
        String body = IOUtil.getStringInputStream(request.getInputStream());
        if (StringUtils.isEmpty(body)) {
            throw new MissingRequestBodyException();
        }
        return convertWithValid(body, clazz);
    }

    static <T extends Validator> T convertWithValid(String jsonStr, Class<T> tClass) {
        T obj = new Gson().fromJson(jsonStr, tClass);
        if (Objects.isNull(obj)) {
            throw new NullPointerException(I18nUtil.getBackendStringFromRes("entryNull"));
        }
        obj.doValid();
        obj.doClean();
        return obj;
    }
}
