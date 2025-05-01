package com.zrlog.model;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hibegin.common.dao.DAO;
import com.hibegin.common.dao.DataSourceWrapper;
import com.hibegin.common.util.BeanUtil;
import com.hibegin.common.util.IOUtil;
import com.hibegin.common.util.StringUtils;
import com.zrlog.common.Constants;
import com.zrlog.data.dto.FaviconBase64DTO;

import java.sql.SQLException;
import java.util.*;

/**
 * 存放全局的设置，比如网站标题，关键字，插件，主题的配置信息等，当字典表处理即可，对应数据库的website表
 */
public class WebSite extends DAO {

    private static final List<String> websitePublicQueryKeys;
    private static final String TEMPLATE_CONFIG_SUFFIX = "_setting";


    static {
        String[] list = new Gson().fromJson(IOUtil.getStringInputStream(WebSite.class.getResourceAsStream("/conf/website-key-public.json")), String[].class);
        websitePublicQueryKeys = Arrays.asList(list);
    }

    public static void main(String[] args) {
        System.out.println(websitePublicQueryKeys);
    }

    public WebSite() {
        this.tableName = "website";
        this.pk = "siteId";
    }

    public WebSite(DataSourceWrapper dataSource) {
        super(dataSource);
        this.tableName = "website";
        this.pk = "siteId";
    }

    public Map<String, Object> getPublicWebSite() {
        return getWebSiteByNameIn(websitePublicQueryKeys);
    }

    public Map<String, Object> getWebSiteByNameIn(List<String> names) {
        Map<String, Object> webSites = new HashMap<>();
        List<Map<String, Object>> lw;
        StringJoiner sj = new StringJoiner(",");
        Object[] params = new String[names.size()];
        for (int i = 0; i < params.length; i++) {
            sj.add("?");
            params[i] = names.get(i);
        }
        try {
            lw = queryListWithParams("select name,remark,value from " + tableName + " where name in " + "(" + sj + ")", params);
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
            return execute("update " + tableName + " set value=? where name=?", value, name);
        } else {
            return execute("insert INTO  " + tableName + " (`value`,`name`) VALUES (?,?)", value, name);
        }
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

    public String getPublicStringValueByName(String name) {
        if (!websitePublicQueryKeys.contains(name)) {
            throw new ArithmeticException(name);
        }
       return getStringValueByName(name);
    }

    public static void clearTemplateConfigMap() {
        Constants.zrLogConfig.getTemplateConfigCacheMap().clear();
    }

    public Map<String, Object> getTemplateConfigMapWithCache(String templateName) {
        return Constants.zrLogConfig.getTemplateConfigCacheMap().computeIfAbsent(templateName, (k) -> {
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

    public FaviconBase64DTO faviconBase64DTO() {
        Map<String, Object> dataMap = getWebSiteByNameIn(Arrays.asList("favicon_ico_base64", "favicon_png_pwa_192_base64", "favicon_png_pwa_512_base64", "generator_html_status"));
        return BeanUtil.convert(dataMap, FaviconBase64DTO.class);
    }
}
