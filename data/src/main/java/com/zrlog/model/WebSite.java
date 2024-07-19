package com.zrlog.model;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hibegin.common.util.BooleanUtils;
import com.hibegin.common.util.IOUtil;
import com.hibegin.common.util.LoggerUtil;
import com.hibegin.common.util.StringUtils;
import com.hibegin.dao.DAO;
import com.zrlog.common.Constants;
import com.zrlog.data.dto.FaviconBase64DTO;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 存放全局的设置，比如网站标题，关键字，插件，主题的配置信息等，当字典表处理即可，对应数据库的website表
 */
public class WebSite extends DAO {

    private static final String websiteQueryKeys;
    private static final String TEMPLATE_CONFIG_SUFFIX = "_setting";
    private static final Map<String, Map<String, Object>> templateConfigCacheMap = new ConcurrentHashMap<>();


    static {
        StringJoiner sj = new StringJoiner("\",\"");
        String[] list = new Gson().fromJson(IOUtil.getStringInputStream(WebSite.class.getResourceAsStream("/conf/website-key-public.json")), String[].class);
        for (String key : list) {
            sj.add(key);
        }
        websiteQueryKeys = "(\"" + sj + "\")";
    }

    public static void main(String[] args) {
        System.out.println(websiteQueryKeys);
    }

    public WebSite() {
        this.tableName = "website";
        this.pk = "siteId";
    }

    public Map<String, Object> getWebSite() {
        Map<String, Object> webSites = new HashMap<>();
        List<Map<String, Object>> lw;
        try {
            lw = queryListWithParams("select name,remark,value from " + tableName + " where name in " + websiteQueryKeys);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        for (Map<String, Object> webSite : lw) {
            webSites.put((String) webSite.get("name"), webSite.get("value"));
            webSites.put(webSite.get("name") + "Remark", webSite.get("remark"));
        }
        if (Objects.isNull(webSites.get("changyan_status"))) {
            webSites.put("changyan_status", "off");
        }
        return webSites;
    }

    public boolean updateByKV(String name, Object value) throws SQLException {
        if (queryFirstObj("select siteId from " + tableName + " where name=?", name) != null) {
            execute("update " + tableName + " set value=? where name=?", value, name);
        } else {
            execute("insert " + tableName + "(`value`,`name`) value(?,?)", value, name);
        }
        return true;
    }

    public String getStringValueByName(String name) {
        try {
            Object value = queryFirstObj("select value from " + tableName + " where name=?", name);
            if (Objects.isNull(value)) {
                return "";
            }
            return value.toString();
        } catch (Exception e) {
            //ignore，比如在未安装时，会有该异常但是不影响逻辑
        }
        return "";
    }

    public static void clearTemplateConfigMap() {
        templateConfigCacheMap.clear();
    }

    public Map<String, Object> getTemplateConfigMapWithCache(String templateName) {
        return templateConfigCacheMap.computeIfAbsent(templateName, (k) -> {
            String dbJsonStr = new WebSite().getStringValueByName(k + TEMPLATE_CONFIG_SUFFIX);
            if (StringUtils.isNotEmpty(dbJsonStr)) {
                return new Gson().fromJson(dbJsonStr, Map.class);
            }
            return new HashMap<>();
        });
    }

    public Map<String, Object> updateTemplateConfigMap(String templateName, Map<String, Object> settingMap) throws SQLException {
        new WebSite().updateByKV(templateName + TEMPLATE_CONFIG_SUFFIX, new GsonBuilder().serializeNulls().create().toJson(settingMap));
        return settingMap;
    }


    public boolean getBoolValueByName(String name) throws SQLException {
        Map<String, Object> webSite = queryFirstWithParams("select value from website where name=?", name);
        if (Objects.isNull(webSite)) {
            return false;
        }
        //数据库varchar导致这里使用1进行比较
        return Constants.websiteValueIsTrue(webSite.get("value"));
    }

    public FaviconBase64DTO faviconBase64DTO() {
        FaviconBase64DTO faviconBase64DTO = new FaviconBase64DTO();
        faviconBase64DTO.setFavicon_ico_base64(getStringValueByName("favicon_ico_base64"));
        faviconBase64DTO.setFavicon_png_pwa_192_base64(getStringValueByName("favicon_png_pwa_192_base64"));
        faviconBase64DTO.setFavicon_png_pwa_512_base64(getStringValueByName("favicon_png_pwa_512_base64"));
        try {
            faviconBase64DTO.setGenerator_html_status(getBoolValueByName("generator_html_status"));
        } catch (SQLException e) {
            LoggerUtil.getLogger(WebSite.class).warning("Load html status error " + e.getMessage());
            faviconBase64DTO.setGenerator_html_status(false);
        }
        return faviconBase64DTO;
    }
}
