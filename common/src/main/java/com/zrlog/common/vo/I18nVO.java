package com.zrlog.common.vo;

import java.util.HashMap;
import java.util.Map;

public class I18nVO {

    private Map<String, Map<String, Object>> blog = new HashMap<>();
    private Map<String, Map<String, Object>> install = new HashMap<>();
    private String locale;

    public Map<String, Map<String, Object>> getBlog() {
        return blog;
    }

    public void setBlog(Map<String, Map<String, Object>> blog) {
        this.blog = blog;
    }

    public Map<String, Map<String, Object>> getInstall() {
        return install;
    }

    public void setInstall(Map<String, Map<String, Object>> install) {
        this.install = install;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String local) {
        this.locale = local;
    }
}
