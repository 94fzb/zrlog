package com.fzb.blog.incp;

import com.jfinal.aop.Invocation;
import com.jfinal.aop.PrototypeInterceptor;
import com.jfinal.core.JFinal;
import com.jfinal.i18n.Res;
import com.jfinal.kit.PathKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public class MyI18NInterceptor extends PrototypeInterceptor {

    public static final String I18N = "i18n";
    private static final String I18N_FILE_NAME = "_i18nFileName";
    private static final Logger LOGGER = LoggerFactory.getLogger(InitDataInterceptor.class);
    private static final Map<String, Map<String, Object>> I18N_RES_MAP = new HashMap<String, Map<String, Object>>();
    private static final Set<String> loadSet = new HashSet<String>();

    static {
        loadI18N(PathKit.getRootClassPath());
    }

    private static boolean loadI18N(String path) {
        File[] files = new File(path).listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.getName().startsWith(I18N) && file.getName().endsWith(".properties")) {
                    String key = file.getName().replace(".properties", "");
                    Map<String, Object> map = I18N_RES_MAP.get(key);
                    if (map == null) {
                        map = new HashMap<String, Object>();
                        I18N_RES_MAP.put(key, map);
                    }
                    Properties properties = new Properties();
                    try {
                        properties.load(new FileInputStream(file));
                        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
                            map.put(entry.getKey().toString(), entry.getValue());
                        }
                    } catch (IOException e) {
                        LOGGER.error("load properties error", e);
                    }
                }
            }
        }
        return files != null;
    }

    public static void addToRequest(String path, HttpServletRequest request) {
        if (JFinal.me().getConstants().getDevMode() || !loadSet.contains(path)) {
            if (loadI18N(path)) {
                loadSet.add(path);
            }
        }
        String i18nFile;
        if (request.getAttribute(I18N_FILE_NAME) != null) {
            i18nFile = request.getAttribute(I18N_FILE_NAME).toString();
        } else {
            Res res = (Res) request.getAttribute("_res");
            //try get locale info for HTTP header
            String locale = null;
            if (request.getRequestURI().contains("/admin")) {
                Map<String, Object> webSite = (Map<String, Object>) JFinal.me().getServletContext().getAttribute("webSite");
                if (webSite != null && webSite.get("language") != null) {
                    String tmpLocale = (String) webSite.get("language");
                    locale = I18N_RES_MAP.get(I18N + "_" + tmpLocale) != null ? tmpLocale : null;
                }
            } else {
                if (request.getHeader("Accept-Language") != null) {
                    String tmpLocale = request.getHeader("Accept-Language").split(";")[0].replace("-", "_").split(",")[0];
                    locale = I18N_RES_MAP.get(I18N + "_" + tmpLocale) != null ? tmpLocale : null;
                }
            }

            if (locale == null) {
                locale = res.getResourceBundle().getLocale().toString();
            }
            i18nFile = I18N + "_" + locale;
            request.setAttribute(I18N_FILE_NAME, i18nFile);
        }
        Map<String, Object> i18nMap = I18N_RES_MAP.get(i18nFile);
        request.setAttribute("_res", i18nMap);
    }

    @Override
    public void doIntercept(Invocation inv) {
        addToRequest(PathKit.getRootClassPath(), inv.getController().getRequest());
        inv.invoke();
    }
}
