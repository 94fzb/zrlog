package com.zrlog.util;

import com.hibegin.common.util.StringUtils;
import com.jfinal.core.Controller;
import com.jfinal.core.JFinal;
import com.zrlog.common.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.*;

/**
 * 多语言的工具类
 */
public class I18NUtil {

    private static final String I18N_FILE_NAME = "_i18nFileName";
    private static final Logger LOGGER = LoggerFactory.getLogger(I18NUtil.class);
    private static final Map<String, Map<String, Object>> I18N_RES_MAP = new HashMap<>();

    static {
        reloadSystemI18N();
    }

    private static void reloadSystemI18N() {
        loadI18N(I18NUtil.class.getResourceAsStream("/i18n_en_US.properties"), "i18n_en_US.properties");
        loadI18N(I18NUtil.class.getResourceAsStream("/i18n_zh_CN.properties"), "i18n_zh_CN.properties");
    }

    private static void loadI18N(InputStream inputStream, String name) {
        if (name.startsWith(Constants.I18N) && name.endsWith(".properties")) {
            String key = name.replace(".properties", "");
            Map<String, Object> map = I18N_RES_MAP.get(key);
            if (map == null) {
                map = new HashMap<>();
                I18N_RES_MAP.put(key, map);
            }
            Properties properties = new Properties();
            try {
                properties.load(inputStream);
                for (Map.Entry<Object, Object> entry : properties.entrySet()) {
                    map.put(entry.getKey().toString(), entry.getValue());
                }
            } catch (IOException e) {
                LOGGER.error("load properties error", e);
            } finally {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    LOGGER.error("", e);
                }
            }
        }
    }

    public static void addToRequest(String path, Controller controller) {
        if (JFinal.me().getConstants().getDevMode()) {
            reloadSystemI18N();
        }
        File[] propertiesFiles = new File(path).listFiles();
        if (propertiesFiles != null) {
            for (File propertiesFile : propertiesFiles) {
                try {
                    loadI18N(new FileInputStream(propertiesFile), propertiesFile.getName());
                } catch (FileNotFoundException e) {
                    //ignore
                }
            }
        }
        String i18nFile;
        String locale = null;
        HttpServletRequest request = controller.getRequest();
        if (request.getAttribute(I18N_FILE_NAME) != null) {
            i18nFile = request.getAttribute(I18N_FILE_NAME).toString();
        } else {
            //try get locale info for HTTP header
            if (request.getRequestURI().contains("/admin")) {
                Map<String, Object> webSite = (Map<String, Object>) JFinal.me().getServletContext().getAttribute("webSite");
                if (webSite != null && webSite.get("language") != null) {
                    String tmpLocale = (String) webSite.get("language");
                    locale = I18N_RES_MAP.get(Constants.I18N + "_" + tmpLocale) != null ? tmpLocale : null;
                }
            } else {
                if (request.getHeader("Accept-Language") != null) {
                    String tmpLocale = request.getHeader("Accept-Language").split(";")[0].replace("-", "_").split(",")[0];
                    locale = I18N_RES_MAP.get(Constants.I18N + "_" + tmpLocale) != null ? tmpLocale : null;
                }
            }

            if (locale == null) {
                locale = "zh_CN";
            }
            request.setAttribute("local", locale);
            if (locale.contains("_")) {
                request.setAttribute("lang", locale.substring(0, locale.indexOf('_')));
            }
            i18nFile = Constants.I18N + "_" + locale;
            request.setAttribute(I18N_FILE_NAME, i18nFile);
        }
        Map<String, Object> i18nMap = I18N_RES_MAP.get(i18nFile);
        if (StringUtils.isNotEmpty(locale) && !locale.startsWith("zh")) {
            Map<String, Object> zhI18nMap = I18N_RES_MAP.get(Constants.I18N + "_" + "zh_CN");
            for (Map.Entry<String, Object> entry : zhI18nMap.entrySet()) {
                if (!i18nMap.containsKey(entry.getKey())) {
                    i18nMap.put(entry.getKey(), entry.getValue());
                }
            }
        }
        controller.setAttr("_res", i18nMap);
    }

    public static String getStringFromRes(String key, HttpServletRequest request) {
        Object obj = ((Map) request.getAttribute("_res")).get(key);
        if (obj != null) {
            return obj.toString();
        }
        return "";
    }

    public static String getCurrentLocale() {
        Map<String, Object> webSite = (Map<String, Object>) JFinal.me().getServletContext().getAttribute("webSite");
        String locale;
        if (webSite != null && webSite.get("language") != null) {
            locale = (String) webSite.get("language");
        } else {
            locale = "zh_CN";
        }
        return locale;
    }
}
