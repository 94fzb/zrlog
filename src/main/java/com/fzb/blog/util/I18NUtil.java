package com.fzb.blog.util;

import com.fzb.blog.common.Constants;
import com.fzb.blog.web.incp.InitDataInterceptor;
import com.jfinal.core.Controller;
import com.jfinal.core.JFinal;
import com.jfinal.kit.PathKit;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

/**
 * 多语言的工具类
 */
public class I18NUtil {

    private static final String I18N_FILE_NAME = "_i18nFileName";
    private static final Logger LOGGER = LoggerFactory.getLogger(InitDataInterceptor.class);
    private static final Map<String, Map<String, Object>> I18N_RES_MAP = new HashMap<>();
    private static final Set<String> loadSet = new HashSet<>();

    static {
        loadI18N(PathKit.getRootClassPath());
    }

    private static boolean loadI18N(String path) {
        File[] files = new File(path).listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.getName().startsWith(Constants.I18N) && file.getName().endsWith(".properties")) {
                    String key = file.getName().replace(".properties", "");
                    Map<String, Object> map = I18N_RES_MAP.get(key);
                    if (map == null) {
                        map = new HashMap<>();
                        I18N_RES_MAP.put(key, map);
                    }
                    Properties properties = new Properties();
                    FileInputStream in = null;
                    try {
                        in = new FileInputStream(file);
                        properties.load(in);
                        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
                            map.put(entry.getKey().toString(), entry.getValue());
                        }
                    } catch (IOException e) {
                        LOGGER.error("load properties error", e);
                    } finally {
                        if (in != null) {
                            try {
                                in.close();
                            } catch (IOException e) {
                                LOGGER.error("close stream error", e);
                            }
                        }
                    }
                }
            }
        }
        return files != null;
    }

    public static void addToRequest(String path, Controller controller) {
        if (JFinal.me().getConstants().getDevMode() || !loadSet.contains(path)) {
            if (loadI18N(path)) {
                loadSet.add(path);
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
                request.setAttribute("lang", locale.substring(0, locale.indexOf("_")));
            }
            i18nFile = Constants.I18N + "_" + locale;
            request.setAttribute(I18N_FILE_NAME, i18nFile);
        }
        Map<String, Object> i18nMap = I18N_RES_MAP.get(i18nFile);
        if (!StringUtils.isBlank(locale)) {
            if (!locale.startsWith("zh")) {
                Map<String, Object> zhI18nMap = I18N_RES_MAP.get(Constants.I18N + "_" + "zh_CN");
                for (Map.Entry<String, Object> entry : zhI18nMap.entrySet()) {
                    if (!i18nMap.containsKey(entry.getKey())) {
                        i18nMap.put(entry.getKey(), entry.getValue());
                    }
                }
            }
        }
        controller.setAttr("_res", i18nMap);
    }

    public static String getStringFromRes(String key, HttpServletRequest request) {
        return ((Map) request.getAttribute("_res")).get(key).toString();
    }
}
