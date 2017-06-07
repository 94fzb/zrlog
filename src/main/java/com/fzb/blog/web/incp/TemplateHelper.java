package com.fzb.blog.web.incp;

import com.fzb.blog.common.BaseDataInitVO;
import com.fzb.blog.common.Constants;
import com.fzb.blog.model.*;
import com.fzb.blog.util.I18NUtil;
import com.fzb.blog.util.ZrlogUtil;
import com.fzb.blog.web.controller.BaseController;
import com.fzb.blog.web.util.WebTools;
import com.jfinal.core.Controller;
import com.jfinal.kit.PathKit;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class TemplateHelper {

    private static final Logger LOGGER = Logger.getLogger(TemplateHelper.class);

    public static void fullInfo(HttpServletRequest request, boolean staticHtml) {
        boolean staticBlog = ZrlogUtil.isStaticBlogPlugin(request);
        // 模板地址
        String suffix = "";
        if (staticBlog || staticHtml) {
            suffix = ".html";
        }
        request.setAttribute("staticBlog", staticBlog);
        request.setAttribute("suffix", suffix);

        BaseDataInitVO baseDataInitVO = cloneObject(request.getAttribute("init"));
        request.setAttribute("init", baseDataInitVO);
        Map webSite = baseDataInitVO.getWebSite();
        String baseUrl = setBaseUrl(request, staticBlog, webSite);
        request.setAttribute("webs", webSite);
        request.setAttribute("searchUrl", baseUrl + "post/search");
        String title = webSite.get("title") + " - " + webSite.get("second_title");
        if (request.getAttribute("log") != null) {
            title = ((Log) request.getAttribute("log")).get("title") + " - " + title;
        }
        request.setAttribute("title", title);
        Object data = null;
        if (request.getAttribute("data") != null) {
            data = request.getAttribute("data");
        } else if (request.getAttribute("log") != null) {
            data = request.getAttribute("log");
        }
        staticHtml(data, baseUrl, suffix);
        if (request.getAttribute("pager") != null && !((List<Map<String, Object>>) ((Map) request.getAttribute("pager")).get("pageList")).isEmpty()) {
            List<Map<String, Object>> pageList = ((List<Map<String, Object>>) ((Map) request.getAttribute("pager")).get("pageList"));
            for (Map<String, Object> pageMap : pageList) {
                pageMap.put("url", baseUrl + pageMap.get("url") + suffix);
            }
            Map<String, Object> pager = (Map<String, Object>) request.getAttribute("pager");
            pager.put("pageStartUrl", baseUrl + pager.get("pageStartUrl") + suffix);
            pager.put("pageEndUrl", baseUrl + pager.get("pageEndUrl") + suffix);
        }
        List<Tag> tags = baseDataInitVO.getTags();
        if (!tags.isEmpty()) {
            for (Tag tag : tags) {
                String tagUri = baseUrl + "post/tag/" + tag.get("text") + suffix;
                tag.put("url", tagUri);
            }
        }
        List<Type> types = baseDataInitVO.getTypes();
        if (!types.isEmpty()) {
            for (Type type : types) {
                String tagUri = baseUrl + "post/sort/" + type.get("alias") + suffix;
                type.put("url", tagUri);
            }
        }
        Map<String, Long> archiveMap = baseDataInitVO.getArchives();
        List<Archive> archiveList = new ArrayList<Archive>();
        for (Map.Entry<String, Long> entry : archiveMap.entrySet()) {
            Archive archive = new Archive();
            archive.setCount(entry.getValue());
            archive.setText(entry.getKey());
            String tagUri = baseUrl + "post/record/" + entry.getKey() + suffix;
            archive.setUrl(tagUri);
            archiveList.add(archive);
        }
        fullNavBar(request, suffix, baseDataInitVO, baseUrl);
        baseDataInitVO.setArchiveList(archiveList);
    }

    private static void fullNavBar(HttpServletRequest request, String suffix, BaseDataInitVO baseDataInitVO, String baseUrl) {
        List<LogNav> logNavs = baseDataInitVO.getLogNavs();
        if (!logNavs.isEmpty()) {
            for (LogNav logNav : logNavs) {
                String url = logNav.get("url").toString();
                if (url.startsWith("/")) {
                    if (suffix.length() > 0 && url.length() == 1) {
                        url = "";
                    } else {
                        url = url.substring(1, url.length());
                    }
                    if (url.startsWith("/post")) {
                        url += suffix;
                    }
                    url = baseUrl + url;
                    logNav.put("url", url);
                }
                String ignoreScheme = request.getRequestURL().toString().replace("https://", "http://");
                if (ignoreScheme.equals(url.replace("https://", "http://"))) {
                    logNav.put("current", true);
                } else {
                    logNav.put("current", false);
                }
            }
        }
    }

    public static String fullTemplateInfo(Controller controller) {
        if (controller instanceof BaseController) {
            BaseController baseController = ((BaseController) controller);
            String basePath = baseController.getTemplatePath();
            controller.getRequest().setAttribute("template", basePath);
            I18NUtil.addToRequest(PathKit.getWebRootPath() + basePath + "/language/", controller);
            baseController.fullTemplateSetting();
            TemplateHelper.fullInfo(controller.getRequest(), baseController.getStaticHtmlStatus());
            return basePath;
        }
        return Constants.DEFAULT_TEMPLATE_PATH;
    }

    private static String setBaseUrl(HttpServletRequest request, boolean staticBlog, Map webSite) {
        String templateUrl;
        String scheme = WebTools.getRealScheme(request);
        String baseUrl = scheme + "://" + request.getHeader("host") + request.getContextPath() + "/";
        String templatePath = request.getAttribute("template").toString();
        if (staticBlog) {
            baseUrl = request.getContextPath() + "/";
            templateUrl = request.getContextPath() + templatePath;
        } else {
            if (isCdnResourceAble(webSite, templatePath)) {
                templateUrl = scheme + "://" + webSite.get("staticResourceHost").toString() + request.getAttribute("template");
            } else {
                templateUrl = scheme + "://" + request.getHeader("host") + request.getContextPath() + request.getAttribute("template");
            }
        }
        request.setAttribute("url", templateUrl);
        request.setAttribute("templateUrl", templateUrl);
        request.setAttribute("rurl", baseUrl);
        request.setAttribute("baseUrl", baseUrl);
        return baseUrl;
    }

    private static boolean isCdnResourceAble(Map webSite, String templatePath) {
        Properties properties = new Properties();
        File file = new File(PathKit.getWebRootPath() + templatePath + "/template.properties");
        if (file.exists()) {
            FileInputStream fileInputStream = null;
            try {
                fileInputStream = new FileInputStream(file);
                properties.load(fileInputStream);
                if (properties.getProperty("staticResource") != null) {
                    return webSite.get("staticResourceHost") != null && !"".equals(webSite.get("staticResourceHost"))/* && !JFinal.me().getConstants().getDevMode()*/;
                }
            } catch (IOException e) {
                LOGGER.error("load properties error", e);
            } finally {
                if (fileInputStream != null) {
                    try {
                        fileInputStream.close();
                    } catch (IOException e) {
                        LOGGER.error("close inputStream error", e);
                    }
                }
            }
        }
        return false;
    }

    private static <T> T cloneObject(Object obj) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = null;
        ObjectInputStream objectInputStream = null;
        try {
            objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(obj);
            objectOutputStream.close();
            objectInputStream = new ObjectInputStream(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));
            T t = (T) objectInputStream.readObject();
            objectInputStream.close();
            return t;
        } catch (IOException | ClassNotFoundException e) {
            LOGGER.error(e);
        } finally {
            if (objectInputStream != null) {
                try {
                    objectInputStream.close();
                } catch (IOException e) {
                    LOGGER.error(e);
                }
            }
            if (objectOutputStream != null) {
                try {
                    objectOutputStream.close();
                } catch (IOException e) {
                    LOGGER.error(e);
                }
            }
        }
        return null;
    }

    private static void staticHtml(Object data, String baseUrl, String suffix) {
        if (data instanceof Log) {
            Log log = (Log) data;
            log.put("alias", log.get("alias") + suffix);
            log.put("url", baseUrl + "post/" + log.get("alias"));
            log.put("typeUrl", baseUrl + "post/sort/" + log.get("typeAlias") + suffix);
            Log lastLog = log.get("lastLog");
            Log nextLog = log.get("nextLog");
            nextLog.put("url", baseUrl + "post/" + nextLog.get("alias") + suffix);
            lastLog.put("url", baseUrl + "post/" + lastLog.get("alias") + suffix);
        } else if (data instanceof Map) {
            Map map = (Map) data;
            List<Log> logList = (List<Log>) map.get("rows");
            if (logList != null) {
                for (Log log : logList) {
                    log.put("url", baseUrl + "post/" + log.get("alias") + suffix);
                    log.put("typeUrl", baseUrl + "post/sort/" + log.get("typeAlias") + suffix);
                }
            }
        }
    }
}
