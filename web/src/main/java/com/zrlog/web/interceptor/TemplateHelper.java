package com.zrlog.web.interceptor;

import com.hibegin.common.util.BeanUtil;
import com.hibegin.common.util.StringUtils;
import com.jfinal.core.Controller;
import com.jfinal.core.JFinal;
import com.jfinal.kit.PathKit;
import com.zrlog.common.Constants;
import com.zrlog.common.vo.OutlineVO;
import com.zrlog.model.Log;
import com.zrlog.model.LogNav;
import com.zrlog.model.Tag;
import com.zrlog.model.Type;
import com.zrlog.service.CommentService;
import com.zrlog.util.*;
import com.zrlog.web.cache.vo.Archive;
import com.zrlog.web.cache.vo.BaseDataInitVO;
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
        staticHtml(data, request, suffix, Constants.getBooleanByFromWebSite("article_thumbnail_status"));
        PagerVO pager = (PagerVO) request.getAttribute("pager");
        if (pager != null && !pager.getPageList().isEmpty()) {
            List<PagerVO.PageEntry> pageList = pager.getPageList();
            for (PagerVO.PageEntry pageMap : pageList) {
                pageMap.setUrl(baseUrl + pageMap.getUrl() + suffix);
            }

            pager.setPageStartUrl(baseUrl + pager.getPageStartUrl() + suffix);
            pager.setPageEndUrl(baseUrl + pager.getPageEndUrl() + suffix);
        }
        fillTags(suffix, baseUrl, baseDataInitVO.getTags());
        fillType(suffix, baseUrl, baseDataInitVO.getTypes());
        fullNavBar(request, suffix, baseDataInitVO);
        baseDataInitVO.setArchiveList(getConvertedArchives(suffix, baseUrl, baseDataInitVO.getArchives()));
    }

    private static List<Archive> getConvertedArchives(String suffix, String baseUrl, Map<String, Long> archiveMap) {
        List<Archive> archives = new ArrayList<>();
        for (Map.Entry<String, Long> entry : archiveMap.entrySet()) {
            Archive archive = new Archive();
            archive.setCount(entry.getValue());
            archive.setText(entry.getKey());
            String tagUri = baseUrl + Constants.getArticleUri() + "record/" + entry.getKey() + suffix;
            archive.setUrl(tagUri);
            archives.add(archive);
        }
        return archives;
    }

    private static void fillTags(String suffix, String baseUrl, List<Tag> tags) {
        for (Tag tag : tags) {
            try {
                String tagUri = baseUrl + Constants.getArticleUri() + "tag/" + URLEncoder.encode(tag.get("text"), "UTF-8") + suffix;
                tag.put("url", tagUri);
            } catch (UnsupportedEncodingException e) {
                LOGGER.error("", e);
            }
        }
    }

    private static void fillType(String suffix, String baseUrl, List<Type> types) {
        for (Type type : types) {
            try {
                String tagUri = baseUrl + Constants.getArticleUri() + "sort/" + URLEncoder.encode(type.get("alias"), "UTF-8") + suffix;
                type.put("url", tagUri);
            } catch (UnsupportedEncodingException e) {
                LOGGER.error("", e);
            }
        }
    }

    private static void fullNavBar(HttpServletRequest request, String suffix, BaseDataInitVO baseDataInitVO) {
        List<LogNav> logNavList = baseDataInitVO.getLogNavs();
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
                url = WebTools.getHomeUrlWithHost(request) + url;
                logNav.put("url", url);
            }
            logNav.put("current", ignoreScheme(request.getRequestURL().toString()).equals(ignoreScheme(url)));
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

    static String fullTemplateInfo(Controller controller, boolean reload) {
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

    private static String setBaseUrl(HttpServletRequest request, boolean staticBlog, Map webSite) {
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
                templateUrl = request.getContextPath() + request.getAttribute("template");
                baseUrl = WebTools.getHomeUrl(request);
            }
        }
        request.setAttribute("url", templateUrl);
        request.setAttribute("templateUrl", templateUrl);
        request.setAttribute("rurl", baseUrl);
        request.setAttribute("baseUrl", baseUrl);
        request.setAttribute("host", request.getHeader("host"));
        request.setAttribute("searchUrl", baseUrl + Constants.getArticleUri() + "search");
        return baseUrl;
    }

    private static boolean isCdnResourceAble(Map webSite, String templatePath) {
        Properties properties = new Properties();
        File file = new File(PathKit.getWebRootPath() + templatePath + "/template.properties");
        if (file.exists()) {
            try (FileInputStream fileInputStream = new FileInputStream(file)) {
                properties.load(fileInputStream);
                if (properties.getProperty("staticResource") != null) {
                    return webSite.get("staticResourceHost") != null && !"".equals(webSite.get("staticResourceHost"));
                }
            } catch (IOException e) {
                LOGGER.error("load properties error", e);
            }
        }
        return false;
    }

    private static void staticHtml(Object data, HttpServletRequest request, String suffix, boolean thumbnailEnableArticle) {
        if (data instanceof Log) {
            fillArticleInfo((Log) data, request, suffix);
        } else if (data instanceof Map) {
            Map map = (Map) data;
            List<Log> logList = (List<Log>) map.get("rows");
            if (logList != null) {
                for (Log log : logList) {
                    if (thumbnailEnableArticle && StringUtils.isNotEmpty(log.get("thumbnail"))) {
                        log.put("thumbnailAlt", ParseUtil.removeHtmlElement(log.get("title")));
                    } else {
                        log.put("thumbnailAlt", null);
                        log.put("thumbnail", null);
                    }
                    log.put("url", WebTools.getHomeUrl(request) + Constants.getArticleUri() + log.get("alias") + suffix);
                    log.put("typeUrl", WebTools.getHomeUrl(request) + Constants.getArticleUri() + "sort/" + log.get("typeAlias") + suffix);
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

    public static void fillArticleInfo(Log data, HttpServletRequest request, String suffix) {
        data.put("alias", data.get("alias") + suffix);
        data.put("url", WebTools.getHomeUrl(request) + Constants.getArticleUri() + data.get("alias"));
        data.put("noSchemeUrl", WebTools.getHomeUrlWithHost(request) + Constants.getArticleUri() + data.get("alias"));
        data.put("typeUrl", WebTools.getHomeUrl(request) + Constants.getArticleUri() + "sort/" + data.get("typeAlias") + suffix);
        Log lastLog = data.get("lastLog");
        Log nextLog = data.get("nextLog");
        nextLog.put("url", WebTools.getHomeUrl(request) + Constants.getArticleUri() + nextLog.get("alias") + suffix);
        lastLog.put("url", WebTools.getHomeUrl(request) + Constants.getArticleUri() + lastLog.get("alias") + suffix);
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
