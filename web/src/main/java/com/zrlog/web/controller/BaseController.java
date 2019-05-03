package com.zrlog.web.controller;

import com.google.gson.Gson;
import com.jfinal.core.Controller;
import com.jfinal.kit.PathKit;
import com.zrlog.common.Constants;
import com.zrlog.common.request.PageableRequest;
import com.zrlog.web.interceptor.TemplateHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * 提供一些基础的工具类，方便其子类调用
 */
public class BaseController extends Controller {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseController.class);

    /**
     * 获取主题的相对于程序的路径，当Cookie中有值的情况下，优先使用Cookie里面的数据（仅当主题存在的情况下，否则返回默认的主题），
     *
     * @return
     */
    public String getTemplatePath() {
        String templatePath = Constants.WEB_SITE.get("template").toString();
        templatePath = templatePath == null ? Constants.DEFAULT_TEMPLATE_PATH : templatePath;
        String previewTheme = TemplateHelper.getTemplatePathByCookie(getRequest().getCookies());
        if (previewTheme != null) {
            templatePath = previewTheme;
        }
        if (!new File(PathKit.getWebRootPath() + templatePath).exists()) {
            templatePath = Constants.DEFAULT_TEMPLATE_PATH;
        }
        return templatePath;
    }

    public Integer getDefaultRows() {
        return Integer.valueOf(Constants.WEB_SITE.get("rows").toString());
    }

    public boolean isNotNullOrNotEmptyStr(Object... args) {
        for (Object arg : args) {
            if (arg == null || "".equals(arg)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 用于转化 GET 的中文乱码
     *
     * @param param
     * @return
     */
    public String convertRequestParam(String param) {
        if (param != null) {
            //如果可以正常读取到中文的情况，直接跳过转换
            if(containsHanScript(param)){
                return param;
            }
            try {
                return URLDecoder.decode(new String(param.getBytes(StandardCharsets.ISO_8859_1)), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                LOGGER.error("request convert to UTF-8 error ", e);
            }
        }
        return "";
    }

    private static boolean containsHanScript(String s) {
        return s.codePoints().anyMatch(
                codepoint ->
                        Character.UnicodeScript.of(codepoint) == Character.UnicodeScript.HAN);
    }

    public void fullTemplateSetting(Object jsonStr) {
        if (isNotNullOrNotEmptyStr(jsonStr)) {
            Map<String, Object> res = getAttr("_res");
            res.putAll(new Gson().fromJson(jsonStr.toString(), Map.class));
        }
    }

    public void fullTemplateSetting() {
        Object jsonStr = Constants.WEB_SITE.get(getTemplatePath() + Constants.TEMPLATE_CONFIG_SUFFIX);
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
