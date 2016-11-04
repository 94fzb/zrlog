package com.fzb.blog.controller;

import com.jfinal.core.Controller;
import flexjson.JSONDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;

public class BaseController extends Controller {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseController.class);
    private static final String DEFAULT_TEMPLATE_PATH = "/include/templates/default";
    protected String templateConfigSuffix = "_setting";
    private String templatePath;
    private Integer rows;
    private Map<String, Object> webSite;

    public void setWebSite(Map<String, Object> webSite) {
        this.webSite = webSite;
        this.templatePath = webSite.get("template").toString();
        this.rows = Integer.parseInt(webSite.get("rows").toString());
    }

    public String getTemplatePath() {
        return this.templatePath == null ? getDefaultTemplatePath() : templatePath;
    }

    public Integer getDefaultRows() {
        return this.rows;
    }

    public Object getValueByKey(String key) {
        if (webSite.get(key) != null) {
            return webSite.get(key).toString();
        }
        return null;
    }

    public String getStrValueByKey(String key) {
        if (webSite.get(key) != null) {
            return webSite.get(key).toString();
        }
        return null;
    }

    public boolean isNotNullOrNotEmptyStr(Object... args) {
        for (Object arg : args) {
            if (arg == null || "".equals(arg)) {
                return false;
            }
        }
        return true;
    }

    public boolean getStaticHtmlStatus() {
        Object obj = getStrValueByKey("pseudo_staticStatus");
        return obj != null && "on".equals(obj.toString());
    }

    /**
     * 用于转化 GET 的中文乱码
     *
     * @param param
     * @return
     */
    public String convertRequestParam(String param) {
        if (param != null) {
            try {
                return URLDecoder.decode(new String(param.getBytes("ISO-8859-1")), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                LOGGER.error("request convert to UTF-8 error ", e);
            }
        }
        return "";
    }

    public void fullTemplateSetting(Object jsonStr) {
        if (isNotNullOrNotEmptyStr(jsonStr)) {
            Map<String, Object> res = getAttr("_res");
            res.putAll(new JSONDeserializer<Map<String, Object>>().deserialize(jsonStr.toString()));
            setAttr("_res", res);
        }
    }

    public void fullTemplateSetting() {
        Object jsonStr = webSite.get(getTemplatePath() + templateConfigSuffix);
        fullTemplateSetting(jsonStr);
    }

    public static String getDefaultTemplatePath() {
        return DEFAULT_TEMPLATE_PATH;
    }
}
