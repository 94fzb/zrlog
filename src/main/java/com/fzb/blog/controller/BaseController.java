package com.fzb.blog.controller;

import com.fzb.blog.common.BaseDataInitVO;
import com.fzb.blog.model.*;
import com.jfinal.aop.Before;
import com.jfinal.core.Controller;
import com.jfinal.core.JFinal;
import com.jfinal.plugin.ehcache.CacheInterceptor;
import com.jfinal.plugin.ehcache.CacheKit;
import com.jfinal.plugin.ehcache.CacheName;
import flexjson.JSONDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class BaseController extends Controller {

    private static final Logger LOGGER = LoggerFactory.getLogger(BaseController.class);
    private static final String DEFAULT_TEMPLATE_PATH = "/include/templates/default";
    protected String templateConfigSuffix = "_setting";
    private String templatePath;
    private Integer rows;
    private Map<String, Object> webSite;

    @Before({CacheInterceptor.class})
    @CacheName("/post/initData")
    public void initData() {
        BaseDataInitVO init = CacheKit.get("/post/initData", "initDataV1");
        if (init == null) {
            init = new BaseDataInitVO();
            Map<String, Object> website = WebSite.dao.getWebSite();
            //兼容早期模板判断方式
            website.put("user_comment_pluginStatus", "on".equals(website.get("duoshuo_status")));
            init.setWebSite(website);
            init.setLinks(Link.dao.queryAll());
            init.setTypes(Type.dao.queryAll());
            init.setLogNavs(LogNav.dao.queryAll());
            init.setPlugins(Plugin.dao.queryAll());
            init.setArchives(Log.dao.getArchives());
            init.setTags(Tag.dao.queryAll());
            init.setHotLogs((List<Log>) Log.dao.getLogsByPage(1, 6).get("rows"));
            List<Type> types = init.getTypes();
            Map<Map<String, Object>, List<Log>> indexHotLog = new LinkedHashMap<Map<String, Object>, List<Log>>();
            for (Type type : types) {
                Map<String, Object> typeMap = new TreeMap<String, Object>();
                typeMap.put("typeName", type.getStr("typeName"));
                typeMap.put("alias", type.getStr("alias"));
                indexHotLog.put(typeMap, (List<Log>) Log.dao.getLogsBySort(1, 6, type.getStr("alias")).get("rows"));
            }
            init.setIndexHotLogs(indexHotLog);
            CacheKit.put("/post/initData", "initDataV1", init);
            JFinal.me().getServletContext().setAttribute("webSite", website);
        }
        setAttr("init", init);
        this.webSite = init.getWebSite();
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
