package com.zrlog.web.interceptor;

import com.jfinal.core.Controller;
import com.jfinal.core.JFinal;
import com.jfinal.kit.PathKit;
import com.zrlog.common.Constants;
import com.zrlog.common.vo.OutlineVO;
import com.zrlog.model.*;
import com.zrlog.util.*;
import com.zrlog.web.controller.BaseController;
import com.zrlog.web.util.WebTools;
import org.apache.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class TemplateHelper {

    private static final Logger LOGGER = Logger.getLogger(TemplateHelper.class);

    /**
     * 根据文件后缀 查找符合要求文件列表
     *
     * @param path
     * @param prefix
     */
    private static void fillFileInfo(String path, List<String> fileList, String... prefix) {
        File[] files = new File(path).listFiles();

        assert files != null;
        for (File file : files) {
            if (file.isDirectory() && new File(file.getAbsolutePath()).listFiles() != null) {
                fillFileInfo(file.getAbsolutePath(), fileList, prefix);
            } else {
                for (String pre : prefix) {
                    if (file.getAbsoluteFile().toString().endsWith(pre)) {
                        fileList.add(file.getAbsoluteFile().toString());
                    }
                }
            }
        }
    }

    public static List<String> getFiles(String path) {
        List<String> fileList = new ArrayList<>();
        fillFileInfo(PathKit.getWebRootPath() + path, fileList, ".jsp", ".js", ".css", ".html");
        String webPath = JFinal.me().getServletContext().getRealPath("/");
        List<String> strFile = new ArrayList<>();
        for (String aFileList : fileList) {
            strFile.add(aFileList.substring(webPath.length() - 1).replace('\\', '/'));
        }
        return strFile;
    }

    public static void fullInfo(HttpServletRequest request, boolean staticHtml) {
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
        boolean thumbnailEnableArticle = true;
        Object tT = webSite.get("article_thumbnail_status");
        if (tT != null && tT instanceof Boolean) {
            thumbnailEnableArticle = (Boolean) tT;
        }
        staticHtml(data, baseUrl, suffix, thumbnailEnableArticle);
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
        List<Archive> archiveList = new ArrayList<>();
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
        List<LogNav> logNavList = baseDataInitVO.getLogNavs();
        if (!logNavList.isEmpty()) {
            for (LogNav logNav : logNavList) {
                String url = logNav.get("url").toString();
                if ("/".equals(url) && ("/all-1".equals(request.getRequestURI()) || "/post/all-1".equals(request.getRequestURI()))) {
                    logNav.put("current", true);
                    continue;
                } else if (url.startsWith("/")) {
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
            BaseController baseController = (BaseController) controller;
            String basePath = baseController.getTemplatePath();
            controller.getRequest().setAttribute("template", basePath);
            I18NUtil.addToRequest(PathKit.getWebRootPath() + basePath + "/language/", controller);
            baseController.fullTemplateSetting();
            TemplateHelper.fullInfo(controller.getRequest(), baseController.isStaticHtmlStatus());
            return basePath;
        }
        return Constants.DEFAULT_TEMPLATE_PATH;
    }

    public static String setBaseUrl(HttpServletRequest request, boolean staticBlog, Map webSite) {
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
            try (FileInputStream fileInputStream = new FileInputStream(file)) {
                properties.load(fileInputStream);
                if (properties.getProperty("staticResource") != null) {
                    return webSite.get("staticResourceHost") != null && !"".equals(webSite.get("staticResourceHost"))/* && !JFinal.me().getConstants().getDevMode()*/;
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
                        log.put("thumbnailAlt", ParseUtil.removeHtmlElement((String) log.get("title")));
                    }
                    log.put("url", baseUrl + "post/" + log.get("alias") + suffix);
                    log.put("typeUrl", baseUrl + "post/sort/" + log.get("typeAlias") + suffix);
                }
            }
        }
    }

    public static void fillArticleInfo(Log data, String baseUrl, String suffix) {
        data.put("alias", data.get("alias") + suffix);
        data.put("url", baseUrl + "post/" + data.get("alias"));
        data.put("typeUrl", baseUrl + "post/sort/" + data.get("typeAlias") + suffix);
        Log lastLog = data.get("lastLog");
        Log nextLog = data.get("nextLog");
        nextLog.put("url", baseUrl + "post/" + nextLog.get("alias") + suffix);
        lastLog.put("url", baseUrl + "post/" + lastLog.get("alias") + suffix);
        String mdContent = data.getStr("mdContent").toLowerCase();
        //没有使用md的toc目录的文章才尝试使用系统提取的目录
        if (!mdContent.contains("[toc]") && !mdContent.contains("[tocm]")) {
            //最基础的实现方式，若需要更强大的实现方式建议使用JavaScript完成（页面输入toc对象）
            OutlineVO outlineVO = OutlineUtil.extractOutline(data.getStr("content"));
            data.put("tocHtml", OutlineUtil.buildTocHtml(outlineVO, ""));
            data.put("toc", outlineVO);
        }
    }
}
