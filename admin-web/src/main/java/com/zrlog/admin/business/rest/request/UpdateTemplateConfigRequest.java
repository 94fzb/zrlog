package com.zrlog.admin.business.rest.request;

import com.zrlog.common.Validator;

import java.util.LinkedHashMap;

public class UpdateTemplateConfigRequest extends LinkedHashMap<String, Object> implements Validator {
    @Override
    public void doValid() {

    }

    public String getTemplate() {
        return (String) get("template");
    }
}
