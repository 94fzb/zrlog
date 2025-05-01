package com.zrlog.common.vo;

import java.util.HashMap;
import java.util.Map;

public class I18nVO {

    private Map<String, Map<String, Object>> blog = new HashMap<>();
    private Map<String, Map<String, Object>> backend = new HashMap<>();
    private Map<String, Map<String, Object>> admin = new HashMap<>();
    private String locale;

    public Map<String, Map<String, Object>> getBlog() {
        return blog;
    }

    public void setBlog(Map<String, Map<String, Object>> blog) {
        this.blog = blog;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String local) {
        this.locale = local;
    }

    public Map<String, Map<String, Object>> getBackend() {
        return backend;
    }

    public void setBackend(Map<String, Map<String, Object>> backend) {
        this.backend = backend;
    }

    public Map<String, Map<String, Object>> getAdmin() {
        return admin;
    }

    public void setAdmin(Map<String, Map<String, Object>> admin) {
        this.admin = admin;
    }
}
