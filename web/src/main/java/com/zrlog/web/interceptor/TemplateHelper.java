package com.zrlog.web.interceptor;

import com.hibegin.common.util.BeanUtil;
import com.jfinal.core.Controller;
import com.jfinal.core.JFinal;
import com.jfinal.kit.PathKit;
import com.zrlog.common.Constants;
import com.zrlog.common.vo.OutlineVO;
import com.zrlog.model.*;
import com.zrlog.service.CommentService;
import com.zrlog.util.I18nUtil;
import com.zrlog.util.OutlineUtil;
import com.zrlog.util.ParseUtil;
import com.zrlog.util.ZrLogUtil;
import com.zrlog.web.controller.BaseController;
import com.zrlog.web.util.WebTools;
import org.apache.log4j.Logger;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class TemplateHelper {

    private static final Logger LOGGER = Logger.getLogger(TemplateHelper.class);

    private static void fullInfo(HttpServletRequest request, boolean staticHtml) {
        boolean staticBlog = ZrLogUtil.isStaticBlogPlugin(request);
        // 模板地址
        String suffix = "";
        if (staticBlog || staticHtml) {
            suffix = ".html";
        }
        request.setAttribute("staticBlog", staticBlog);
        request.setAttribute("suffix", suffix);

        BaseDataInitVO baseDataInitVO = BeanUtil.cloneObject(request.getAttribute("init"));
        request.setAttribute("init", baseDataInitVO);
        Map webSite = baseDataInitVO.getWebSite();
        String baseUrl = setBaseUrl(request, staticBlog, webSite);
        //过期
        request.setAttribute("webs", webSite);
        request.setAttribute("searchUrl", baseUrl + Constants.getArticleUri() + "search");
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
        staticHtml(data, baseUrl, suffix, Constants.getBooleanByFromWebSite("article_thumbnail_status"));
        if (request.getAttribute("pager") != null && !((List<Map<String, Object>>) ((Map) request.getAttribute("pager")).get("pageList")).isEmpty()) {
            List<Map<String, Object>> pageList = (List<Map<String, Object>>) ((Map) request.getAttribute("pager")).get("pageList");
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
                try {
                    String tagUri = baseUrl + Constants.getArticleUri() + "tag/" + URLEncoder.encode(tag.get("text"), "UTF-8") + suffix;
                    tag.put("url", tagUri);
                } catch (UnsupportedEncodingException e) {
                    LOGGER.error("", e);
                }
            }
        }
        List<Type> types = baseDataInitVO.getTypes();
        if (!types.isEmpty()) {
            for (Type type : types) {
                try {
                    String tagUri = baseUrl + Constants.getArticleUri() + "sort/" + URLEncoder.encode(type.get("alias"), "UTF-8") + suffix;
                    type.put("url", tagUri);
                } catch (UnsupportedEncodingException e) {
                    LOGGER.error("", e);
                }
            }
        }
        Map<String, Long> archiveMap = baseDataInitVO.getArchives();
        List<Archive> archiveList = new ArrayList<>();
        for (Map.Entry<String, Long> entry : archiveMap.entrySet()) {
            Archive archive = new Archive();
            archive.setCount(entry.getValue());
            archive.setText(entry.getKey());
            String tagUri = baseUrl + Constants.getArticleUri() + "record/" + entry.getKey() + suffix;
            archive.setUrl(tagUri);
            archiveList.add(archive);
        }
        fullNavBar(request, suffix, baseDataInitVO, baseUrl);
        baseDataInitVO.setArchiveList(archiveList);
    }

    private static void fullNavBar(HttpServletRequest request, String suffix, BaseDataInitVO baseDataInitVO, String baseUrl) {
        List<LogNav> logNavList = baseDataInitVO.getLogNavs();
        if (!logNavList.isEmpty()) {
            for (LogNav logNav : logNavList) {
                String url = logNav.get("url").toString();
                if ("/".equals(url) && ("/all-1".equals(request.getRequestURI()) || (Constants.getArticleUri() + "all-1").equals(request.getRequestURI()))) {
                    logNav.put("current", true);
                    continue;
                } else if (url.startsWith("/")) {
                    if (suffix.length() > 0 && url.length() == 1) {
                        url = "";
                    } else {
                        url = url.substring(1);
                    }
                    if (url.startsWith("/" + Constants.getArticleUri())) {
                        url += suffix;
                    }
                    url = baseUrl + url;
                    logNav.put("url", url);
                }
                if (ignoreScheme(request.getRequestURL().toString()).equals(ignoreScheme(url))) {
                    logNav.put("current", true);
                } else {
                    logNav.put("current", false);
                }
            }
        }
    }

    private static String ignoreScheme(String url) {
        if (url.startsWith("http://")) {
            return url.substring("http:".length());
        } else if (url.startsWith("https://")) {
            return url.substring("https:".length());
        }
        return url;
    }

    public static String fullTemplateInfo(Controller controller, boolean reload) {
        if (controller instanceof BaseController) {
            BaseController baseController = (BaseController) controller;
            String basePath = baseController.getTemplatePath();
            controller.getRequest().setAttribute("template", basePath);
            I18nUtil.addToRequest(PathKit.getWebRootPath() + basePath + "/language/", controller.getRequest(), JFinal.me().getConstants().getDevMode(), reload);
            baseController.fullTemplateSetting();
            TemplateHelper.fullInfo(controller.getRequest(), Constants.isStaticHtmlStatus());
            return basePath;
        }
        return Constants.DEFAULT_TEMPLATE_PATH;
    }

    public static String setBaseUrl(HttpServletRequest request, boolean staticBlog, Map webSite) {
        String templateUrl;
        String baseUrl = WebTools.getHomeUrl(request);
        String templatePath = request.getAttribute("template").toString();
        if (staticBlog) {
            baseUrl = request.getContextPath() + "/";
            templateUrl = request.getContextPath() + templatePath;
        } else {
            if (isCdnResourceAble(webSite, templatePath)) {
                templateUrl = "//" + webSite.get("staticResourceHost").toString() + request.getAttribute("template");
                request.setAttribute("staticResourceBaseUrl", "//" + webSite.get("staticResourceHost").toString() + request.getContextPath() + "/");
            } else {
                templateUrl = "//" + request.getHeader("host") + request.getContextPath() + request.getAttribute("template");
            }
        }
        request.setAttribute("url", templateUrl);
        request.setAttribute("templateUrl", templateUrl);
        request.setAttribute("rurl", baseUrl);
        request.setAttribute("baseUrl", baseUrl);
        request.setAttribute("host", request.getHeader("host"));
        return baseUrl;
    }

    private static boolean isCdnResourceAble(Map webSite, String templatePath) {
        Properties properties = new Properties();
        File file = new File(PathKit.getWebRootPath() + templatePath + "/template.properties");
        if (file.exists()) {
            try (FileInputStream fileInputStream = new FileInputStream(file)) {
                properties.load(fileInputStream);
                if (properties.getProperty("staticResource") != null) {
                    return webSite.get("staticResourceHost") != null && !"".equals(webSite.get("staticResourceHost"))
                            /* && !JFinal.me().getConstants().getDevMode()*/;
                }
            } catch (IOException e) {
                LOGGER.error("load properties error", e);
            }
        }
        return false;
    }

    private static void staticHtml(Object data, String baseUrl, String suffix, boolean thumbnailEnableArticle) {
        if (data instanceof Log) {
            fillArticleInfo((Log) data, baseUrl, suffix);
        } else if (data instanceof Map) {
            Map map = (Map) data;
            List<Log> logList = (List<Log>) map.get("rows");
            if (logList != null) {
                for (Log log : logList) {
                    if (!thumbnailEnableArticle) {
                        log.put("thumbnail", null);
                    } else if (log.get("thumbnail") != null) {
                        log.put("thumbnailAlt", ParseUtil.removeHtmlElement(log.get("title")));
                    }
                    log.put("url", baseUrl + Constants.getArticleUri() + log.get("alias") + suffix);
                    log.put("typeUrl", baseUrl + Constants.getArticleUri() + "sort/" + log.get("typeAlias") + suffix);
                }
            }
        }
    }

    public static String getTemplatePathByCookie(Cookie[] cookies) {
        String previewTemplate = null;
        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                if ("template".equals(cookie.getName()) && cookie.getValue().startsWith(Constants.TEMPLATE_BASE_PATH)) {
                    previewTemplate = cookie.getValue();
                    break;
                }
            }
        }
        return previewTemplate;
    }

    public static void fillArticleInfo(Log data, String baseUrl, String suffix) {
        data.put("alias", data.get("alias") + suffix);
        data.put("url", baseUrl + Constants.getArticleUri() + data.get("alias"));
        data.put("noSchemeUrl", baseUrl.substring(2) + Constants.getArticleUri() + data.get("alias"));
        data.put("typeUrl", baseUrl + Constants.getArticleUri() + "sort/" + data.get("typeAlias") + suffix);
        Log lastLog = data.get("lastLog");
        Log nextLog = data.get("nextLog");
        nextLog.put("url", baseUrl + Constants.getArticleUri() + nextLog.get("alias") + suffix);
        lastLog.put("url", baseUrl + Constants.getArticleUri() + lastLog.get("alias") + suffix);
        //没有使用md的toc目录的文章才尝试使用系统提取的目录
        if (data.getStr("markdown") != null && !data.getStr("markdown").toLowerCase().contains("[toc]")
                && !data.getStr("markdown").toLowerCase().contains("[tocm]")) {
            //最基础的实现方式，若需要更强大的实现方式建议使用JavaScript完成（页面输入toc对象）
            OutlineVO outlineVO = OutlineUtil.extractOutline(data.getStr("content"));
            data.put("tocHtml", OutlineUtil.buildTocHtml(outlineVO, ""));
            data.put("toc", outlineVO);
        }
        if (!new CommentService().isAllowComment()) {
            data.set("canComment", false);
        }
    }
}
