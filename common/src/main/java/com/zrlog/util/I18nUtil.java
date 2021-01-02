package com.zrlog.util;

import com.hibegin.common.util.StringUtils;
import com.zrlog.common.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.StringJoiner;

/**
 * 多语言的工具类
 */
public class I18nUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(I18nUtil.class);
    private static final Map<String, Map<String, Object>> I18N_RES_MAP = new HashMap<>();
    public static final ThreadLocal<Map<String, Object>> threadLocal = new ThreadLocal<>();

    static {
        reloadSystemI18N();
    }

    private static void reloadSystemI18N() {
        loadI18N(I18nUtil.class.getResourceAsStream("/i18n_en_US.properties"), "i18n_en_US.properties");
        loadI18N(I18nUtil.class.getResourceAsStream("/i18n_zh_CN.properties"), "i18n_zh_CN.properties");
    }

    public static void removeI18n() {
        threadLocal.remove();
    }

    private static void loadI18N(InputStream inputStream, String name) {
        if (name.startsWith(Constants.I18N) && name.endsWith(".properties")) {
            try {
                String key = name.replace(".properties", "");
                Map<String, Object> map = I18N_RES_MAP.computeIfAbsent(key, k -> new HashMap<>());
                Properties properties = new Properties();

                properties.load(inputStream);
                for (Map.Entry<Object, Object> entry : properties.entrySet()) {
                    map.put(entry.getKey().toString(), entry.getValue());
                }
            } catch (Exception e) {
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

    public static void addToRequest(String path, HttpServletRequest request, boolean devMode) {
        if (devMode) {
            reloadSystemI18N();
        }
        if (path != null) {
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
        }
        String locale;
        if (request.getRequestURI().contains(Constants.ADMIN_URI_BASE_PATH + "/") || request.getRequestURI().contains("/api" + Constants.ADMIN_URI_BASE_PATH + "/")) {
            locale = (String) Constants.WEB_SITE.get("language");
        } else {
            String referer = request.getHeader("referer");
            if (StringUtils.isNotEmpty(referer) && referer.contains(Constants.ADMIN_URI_BASE_PATH + "/")) {
                locale = (String) Constants.WEB_SITE.get("language");
            } else {
                //try get locale info for HTTP header
                locale = getAcceptLanguage(request);
            }
        }
        if (locale == null) {
            locale = "zh_CN";
        }
        if (locale.contains("_")) {
            request.setAttribute("lang", locale.substring(0, locale.indexOf('_')));
        }
        String i18nFile = Constants.I18N + "_" + locale;
        Map<String, Object> i18nMap = I18N_RES_MAP.get(i18nFile);
        if (StringUtils.isNotEmpty(locale) && !locale.startsWith("zh")) {
            Map<String, Object> zhI18nMap = I18N_RES_MAP.get(Constants.I18N + "_" + "zh_CN");
            for (Map.Entry<String, Object> entry : zhI18nMap.entrySet()) {
                if (!i18nMap.containsKey(entry.getKey())) {
                    i18nMap.put(entry.getKey(), entry.getValue());
                }
            }
        }
        i18nMap.put("_locale", locale);
        request.setAttribute("local", locale);
        request.setAttribute("_res", i18nMap);
        threadLocal.set(i18nMap);
    }

    public static String getAcceptLanguage(HttpServletRequest request) {
        String locale = null;
        try {
            if (request.getHeader("Accept-Language") != null) {
                locale = request.getHeader("Accept-Language").split(";")[0].replace("-", "_").split(",")[0];
                StringJoiner sj = new StringJoiner("_");
                String[] arr = locale.split("_");
                sj.add(arr[0]);
                sj.add(arr[1].toUpperCase());
                locale = sj.toString();
            }
        } catch (Exception e) {
            //ignore 非法HTTP请求头
        }
        if (StringUtils.isEmpty(locale)) {
            locale = "zh_CN";
        }
        return locale;
    }

    public static String getStringFromRes(String key) {
        Object obj = threadLocal.get().get(key);
        if (obj != null) {
            return obj.toString();
        }
        return "";
    }

    public static String getCurrentLocale() {
        String locale = null;
        if (threadLocal.get() != null) {
            locale = (String) threadLocal.get().get("_locale");
        } else {
            if (Constants.WEB_SITE.get("language") != null) {
                locale = (String) Constants.WEB_SITE.get("language");
            }
        }
        if (StringUtils.isEmpty(locale)) {
            locale = "zh_CN";
        }
        return locale;
    }
}
