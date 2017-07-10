package com.fzb.blog.web.controller;

import com.fzb.blog.common.Constants;
import com.fzb.blog.common.request.PageableRequest;
import com.google.gson.Gson;
import com.jfinal.core.Controller;
import com.jfinal.kit.PathKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

/**
 * 提供一些基础的工具类，方便其子类调用
 */
public class BaseController extends Controller {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseController.class);
    protected String templateConfigSuffix = "_setting";
    private String templatePath;
    private Integer rows;
    private Map<String, Object> webSite = new HashMap<String, Object>();

    public void setWebSite(Map<String, Object> webSite) {
        this.webSite = webSite;
        this.templatePath = webSite.get("template").toString();
        this.rows = Integer.parseInt(webSite.get("rows").toString());
    }

    /**
     * 获取主题的相对于程序的路径，当Cookie中有值的情况下，优先使用Cookie里面的数据（仅当主题存在的情况下，否则返回默认的主题），
     *
     * @return
     */
    public String getTemplatePath() {
        this.templatePath = this.templatePath == null ? Constants.DEFAULT_TEMPLATE_PATH : templatePath;
        String previewTheme = getTemplatePathByCookie();
        if (previewTheme != null) {
            this.templatePath = previewTheme;
        }
        if (!new File(PathKit.getWebRootPath() + this.templatePath).exists()) {
            templatePath = Constants.DEFAULT_TEMPLATE_PATH;
        }
        return templatePath;
    }

    protected String getTemplatePathByCookie() {
        String previewTemplate = null;
        Cookie[] cookies = getRequest().getCookies();
        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("template") && cookie.getValue().startsWith(Constants.TEMPLATE_BASE_PATH)) {
                    previewTemplate = cookie.getValue();
                    break;
                }
            }
        }
        return previewTemplate;
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
            res.putAll(new Gson().fromJson(jsonStr.toString(), Map.class));
        }
    }

    public void fullTemplateSetting() {
        Object jsonStr = webSite.get(getTemplatePath() + templateConfigSuffix);
        fullTemplateSetting(jsonStr);
    }

    /**
     * 封装Jqgrid的分页参数
     *
     * @return
     */
    public PageableRequest getPageable() {
        PageableRequest pageableRequest = new PageableRequest();
        pageableRequest.setRows(getParaToInt("rows"));
        pageableRequest.setSort(getPara("sidx"));
        pageableRequest.setOrder(getPara("sord"));
        pageableRequest.setPage(getParaToInt("page"));
        return pageableRequest;
    }
}
