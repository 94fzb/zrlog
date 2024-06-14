package com.zrlog.util;

import com.hibegin.common.util.BeanUtil;
import com.hibegin.common.util.LoggerUtil;
import com.hibegin.common.util.StringUtils;
import com.hibegin.http.server.api.HttpRequest;
import com.hibegin.http.server.util.PathUtil;
import com.zrlog.common.Constants;
import com.zrlog.common.exception.NotImplementException;
import com.zrlog.common.vo.I18nVO;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 多语言的工具类
 */
public class I18nUtil {

    public static final ThreadLocal<I18nVO> threadLocal = new ThreadLocal<>();
    private static final Logger LOGGER = LoggerUtil.getLogger(I18nUtil.class);
    private static final I18nVO i18nVOCache = new I18nVO();
    private static final String I18N_INSTALL_KEY = "install";
    private static final String I18N_BLOG_KEY = "blog";
    private static final String I18N_ADMIN_KEY = "admin";
    private static final String I18N_BACKEND_KEY = "backend";
    private static final String DEFAULT_LANG = "zh_CN";

    static {
        reloadSystemI18N();
    }

    private static void reloadSystemI18N() {
        loadI18N(I18nUtil.class.getResourceAsStream("/i18n/admin_en_US.properties"), "admin_en_US.properties", I18N_ADMIN_KEY);
        loadI18N(I18nUtil.class.getResourceAsStream("/i18n/admin_zh_CN.properties"), "admin_zh_CN.properties", I18N_ADMIN_KEY);
        loadI18N(I18nUtil.class.getResourceAsStream("/i18n/blog_en_US.properties"), "blog_en_US.properties", I18N_BLOG_KEY);
        loadI18N(I18nUtil.class.getResourceAsStream("/i18n/blog_zh_CN.properties"), "blog_zh_CN.properties", I18N_BLOG_KEY);
        loadI18N(I18nUtil.class.getResourceAsStream("/i18n/install_en_US.properties"), "install_en_US.properties", I18N_INSTALL_KEY);
        loadI18N(I18nUtil.class.getResourceAsStream("/i18n/install_zh_CN.properties"), "install_zh_CN.properties", I18N_INSTALL_KEY);
        loadI18N(I18nUtil.class.getResourceAsStream("/i18n/backend_en_US.properties"), "backend_en_US.properties", I18N_BACKEND_KEY);
        loadI18N(I18nUtil.class.getResourceAsStream("/i18n/backend_zh_CN.properties"), "backend_zh_CN.properties", I18N_BACKEND_KEY);
    }

    public static I18nVO getI18nVOCache() {
        return i18nVOCache;
    }

    public static void removeI18n() {
        threadLocal.remove();
    }

    private static void loadI18N(InputStream inputStream, String name, String resourceName) {
        if (Objects.isNull(inputStream)) {
            return;
        }
        if (!name.endsWith(".properties")) {
            return;
        }
        Map<String, Map<String, Object>> resMap = switch (resourceName) {
            case I18N_BLOG_KEY -> i18nVOCache.getBlog();
            case I18N_ADMIN_KEY -> i18nVOCache.getAdmin();
            case I18N_INSTALL_KEY -> i18nVOCache.getInstall();
            case I18N_BACKEND_KEY -> i18nVOCache.getBackend();
            default -> throw new NotImplementException();
        };
        try {
            String key = name.replace(".properties", "").replace("i18n_", "").replace(resourceName + "_", "");
            Map<String, Object> map = resMap.computeIfAbsent(key, k -> new HashMap<>());
            Properties properties = new Properties();

            properties.load(inputStream);
            for (Map.Entry<Object, Object> entry : properties.entrySet()) {
                map.put(entry.getKey().toString(), entry.getValue());
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "load properties error", e);
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "", e);
            }
        }
    }

    public static void addToRequest(String templatePath, HttpRequest request) {
        if (Constants.devEnabled) {
            reloadSystemI18N();
        }
        if (templatePath != null) {
            if (Objects.equals(templatePath, Constants.DEFAULT_TEMPLATE_PATH)) {
                File enUSPropertiesFile = new File(templatePath + "/language/i18n_en_US.properties");
                File zhCNPropertiesFile = new File(templatePath + "/language/i18n_zh_CN.properties");
                loadI18N(I18nUtil.class.getResourceAsStream(enUSPropertiesFile.toString().replace("\\", "/")), enUSPropertiesFile.getName(), I18N_BLOG_KEY);
                loadI18N(I18nUtil.class.getResourceAsStream(zhCNPropertiesFile.toString().replace("\\", "/")), zhCNPropertiesFile.getName(), I18N_BLOG_KEY);
            } else {
                String filePath = PathUtil.getStaticPath() + templatePath + "/language/";
                File[] propertiesFiles = new File(filePath).listFiles();
                if (propertiesFiles != null) {
                    for (File propertiesFile : propertiesFiles) {
                        try {
                            loadI18N(new FileInputStream(propertiesFile), propertiesFile.getName(), I18N_BLOG_KEY);
                        } catch (FileNotFoundException e) {
                            //ignore
                        }
                    }
                }
            }
        }
        String locale = null;
        if (Objects.nonNull(request)) {
            if (request.getUri().contains(Constants.ADMIN_URI_BASE_PATH + "/") || request.getUri().contains("/api" + Constants.ADMIN_URI_BASE_PATH + "/")) {
                locale = (String) Constants.WEB_SITE.get("language");
            } else {
                String referer = request.getHeader("referer");
                if (StringUtils.isNotEmpty(referer) && referer.contains(Constants.ADMIN_URI_BASE_PATH + "/")) {
                    locale = (String) Constants.WEB_SITE.get("language");
                } else {
                    //try get locale info from HTTP header
                    locale = getAcceptLocal(request);
                }
            }
        }
        if (locale == null) {
            locale = "zh_CN";
        }

        I18nVO i18nVO = BeanUtil.convert(i18nVOCache, I18nVO.class);
        Map<String, Object> blogI18n = new HashMap<>(i18nVO.getBlog().get(locale));
        if (StringUtils.isNotEmpty(locale) && !locale.startsWith("zh")) {
            Map<String, Object> zhI18nMap = i18nVO.getBlog().get(DEFAULT_LANG);
            for (Map.Entry<String, Object> entry : zhI18nMap.entrySet()) {
                if (!blogI18n.containsKey(entry.getKey())) {
                    blogI18n.put(entry.getKey(), entry.getValue());
                }
            }
        }
        blogI18n.put("_locale", locale);
        if (Objects.nonNull(request)) {
            request.getAttr().put("local", locale);
            String lang = locale;
            if (locale.contains("_")) {
                lang = locale.substring(0, locale.indexOf('_'));
            }
            request.getAttr().put("lang", lang);
            request.getAttr().put("_res", blogI18n);
        }
        i18nVO.setLocale(locale);
        threadLocal.set(i18nVO);
    }

    public static String getAcceptLocal(HttpRequest request) {
        String lang = request.getHeader("Accept-Language");
        if (Objects.nonNull(lang) && lang.startsWith("en")) {
            return "en_US";
        }
        return DEFAULT_LANG;
    }

    public static Map<String, Object> getBackend() {
        return threadLocal.get().getBackend().get(threadLocal.get().getLocale());
    }

    public static Map<String, Map<String, Object>> getAdmin() {
        if (Objects.isNull(threadLocal.get())) {
            return new HashMap<>();
        }
        Map<String, Map<String, Object>> admin = threadLocal.get().getAdmin();
        if (Objects.isNull(admin)) {
            return new HashMap<>();
        }
        return admin;
    }

    public static String getBlogStringFromRes(String key) {
        if (Objects.isNull(threadLocal.get())) {
            return "";
        }
        Object obj = threadLocal.get().getBlog().get(threadLocal.get().getLocale()).get(key);
        if (obj != null) {
            return obj.toString();
        }
        return "";
    }

    public static String getAdminStringFromRes(String key) {
        return Objects.requireNonNullElse(getAdmin().get(threadLocal.get().getLocale()).get(key), "").toString();
    }

    public static String getBackendStringFromRes(String key) {
        if (Objects.isNull(threadLocal.get())) {
            return "";
        }
        Object obj = threadLocal.get().getBackend().get(threadLocal.get().getLocale()).get(key);
        if (obj != null) {
            return obj.toString();
        }
        return "";
    }

    public static Map<String, Map<String, Object>> getInstall() {
        if (Objects.isNull(threadLocal.get())) {
            return new HashMap<>();
        }
        Map<String, Map<String, Object>> install = threadLocal.get().getInstall();
        if (Objects.isNull(install)) {
            return new HashMap<>();
        }
        return install;
    }

    public static Map<String, Map<String, Object>> getBlog() {
        if (Objects.isNull(threadLocal.get())) {
            return new HashMap<>();
        }
        Map<String, Map<String, Object>> install = threadLocal.get().getBlog();
        if (Objects.isNull(install)) {
            return new HashMap<>();
        }
        return install;
    }

    public static String getInstallStringFromRes(String key) {
        if (Objects.isNull(threadLocal.get())) {
            return "";
        }
        Object obj = threadLocal.get().getInstall().get(threadLocal.get().getLocale()).get(key);
        if (obj != null) {
            return obj.toString();
        }
        return "";
    }

    public static String getCurrentLocale() {
        String locale = null;
        if (threadLocal.get() != null) {
            locale = threadLocal.get().getLocale();
        } else {
            if (Constants.WEB_SITE.get("language") != null) {
                locale = (String) Constants.WEB_SITE.get("language");
            }
        }
        if (StringUtils.isNotEmpty(locale)) {
            return locale;
        }
        return DEFAULT_LANG;
    }
}
