package com.zrlog.business.service;

import com.google.gson.Gson;
import com.hibegin.common.util.BeanUtil;
import com.hibegin.common.util.StringUtils;
import com.jfinal.core.JFinal;
import com.jfinal.kit.PathKit;
import com.zrlog.blog.web.util.WebTools;
import com.zrlog.business.cache.vo.Archive;
import com.zrlog.business.cache.vo.BaseDataInitVO;
import com.zrlog.business.util.PagerVO;
import com.zrlog.common.Constants;
import com.zrlog.common.vo.OutlineVO;
import com.zrlog.common.vo.TemplateVO;
import com.zrlog.data.dto.PageData;
import com.zrlog.model.Log;
import com.zrlog.model.LogNav;
import com.zrlog.model.Tag;
import com.zrlog.model.Type;
import com.zrlog.util.I18nUtil;
import com.zrlog.util.OutlineUtil;
import com.zrlog.util.ParseUtil;
import com.zrlog.util.ZrLogUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.URLEncoder;
import java.util.*;

public class TemplateHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(TemplateHelper.class);

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
        Map<String, Object> webSite = baseDataInitVO.getWebSite();
        String baseUrl = setBaseUrl(request, staticBlog, webSite);
        //过期
        request.setAttribute("webs", webSite);
        String title = webSite.get("title") + " - " + webSite.get("second_title");
        if (request.getAttribute("log") != null) {
            title = ((Log) request.getAttribute("log")).get("title") + " - " + title;
        }
        request.setAttribute("title", title);

        staticHtml(request, suffix, Constants.getBooleanByFromWebSite("article_thumbnail_status"));
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
                String tagUri = baseUrl + Constants.getArticleUri() + "tag/" + URLEncoder.encode(tag.get("text"),
                        "UTF-8") + suffix;
                tag.put("url", tagUri);
            } catch (UnsupportedEncodingException e) {
                LOGGER.error("", e);
            }
        }
    }

    private static void fillType(String suffix, String baseUrl, List<Type> types) {
        for (Type type : types) {
            try {
                String tagUri = baseUrl + Constants.getArticleUri() + "sort/" + URLEncoder.encode(type.get("alias"),
                        "UTF-8") + suffix;
                type.put("url", tagUri);
            } catch (UnsupportedEncodingException e) {
                LOGGER.error("", e);
            }
        }
    }

    private static boolean isHomePage(HttpServletRequest request) {
        String uri = request.getRequestURI().replace(".html", "");
        return "".equals(uri) || "/".equals(uri) || "/all-1".equals(uri) || "/all".equals(uri) || ("/" + Constants.getArticleUri() + "all").equals(uri) || ("/" + Constants.getArticleUri() + "all-1").equals(uri);
    }

    private static void fullNavBar(HttpServletRequest request, String suffix, BaseDataInitVO baseDataInitVO) {
        List<LogNav> logNavList = baseDataInitVO.getLogNavs();
        for (LogNav logNav : logNavList) {
            String url = logNav.get("url").toString();
            boolean current;
            if ("/".equals(url) && isHomePage(request)) {
                current = true;
            } else if (url.startsWith("/")) {
                if ("/".equals(url)) {
                    url = WebTools.getHomeUrlWithHost(request);
                }
                //文章页
                else if (url.startsWith("/" + Constants.getArticleUri())) {
                    url = WebTools.getHomeUrlWithHost(request) + url.substring(1);
                    if (!url.endsWith(suffix)) {
                        url = url + suffix;
                    }
                }
                logNav.put("url", url);
                current = ignoreScheme(request.getRequestURL().toString(), suffix).equals(ignoreScheme(url, suffix));
            } else {
                current = ignoreScheme(request.getRequestURL().toString(), suffix).equals(ignoreScheme(url, suffix));
            }
            logNav.put("current", current);
        }
    }

    private static String ignoreScheme(String url, String suffix) {
        if (suffix != null && suffix.length() > 0 && url.endsWith(suffix)) {
            url = url.substring(0, url.length() - suffix.length());
        }
        if (url.startsWith("http://")) {
            return url.substring("http:".length());
        } else if (url.startsWith("https://")) {
            return url.substring("https:".length());
        }
        return url;
    }

    public static String setBaseUrl(HttpServletRequest request, boolean staticBlog, Map<String, Object> webSite) {
        String templateUrl;
        String baseUrl = WebTools.getHomeUrl(request);
        String templatePath = request.getAttribute("template").toString();
        if (staticBlog) {
            baseUrl = request.getContextPath() + "/";
            templateUrl = request.getContextPath() + templatePath;
        } else {
            if (isCdnResourceAble(webSite, templatePath)) {
                templateUrl = "//" + webSite.get("staticResourceHost").toString() + request.getAttribute("template");
                request.setAttribute("staticResourceBaseUrl",
                        "//" + webSite.get("staticResourceHost").toString() + request.getContextPath() + "/");
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

    private static boolean isCdnResourceAble(Map<String, Object> webSite, String templatePath) {
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

    private static void staticHtml(HttpServletRequest request, String suffix, boolean thumbnailEnableArticle) {
        if (request.getAttribute("data") != null) {
            PageData<Log> map = (PageData) request.getAttribute("data");
            List<Log> logList = map.getRows();
            if (logList != null) {
                for (Log log : logList) {
                    if (thumbnailEnableArticle && StringUtils.isNotEmpty(log.get("thumbnail"))) {
                        log.put("thumbnailAlt", ParseUtil.removeHtmlElement(log.get("title")));
                    } else {
                        log.put("thumbnailAlt", null);
                        log.put("thumbnail", null);
                    }
                    log.put("url",
                            WebTools.getHomeUrl(request) + Constants.getArticleUri() + log.get("alias") + suffix);
                    log.put("typeUrl", WebTools.getHomeUrl(request) + Constants.getArticleUri() + "sort/" + log.get(
                            "typeAlias") + suffix);
                }
            }
        } else if (request.getAttribute("log") != null) {
            fillArticleInfo((Log) request.getAttribute("log"), request, suffix);
        }
    }

    public static void fillArticleInfo(Log data, HttpServletRequest request, String suffix) {
        data.put("alias", data.get("alias") + suffix);
        data.put("url", WebTools.getHomeUrl(request) + Constants.getArticleUri() + data.get("alias"));
        data.put("noSchemeUrl", WebTools.getHomeUrlWithHost(request) + Constants.getArticleUri() + data.get("alias"));
        data.put("typeUrl",
                WebTools.getHomeUrl(request) + Constants.getArticleUri() + "sort/" + data.get("typeAlias") + suffix);
        Log lastLog = data.get("lastLog");
        Log nextLog = data.get("nextLog");
        nextLog.put("url", WebTools.getHomeUrl(request) + Constants.getArticleUri() + nextLog.get("alias") + suffix);
        lastLog.put("url", WebTools.getHomeUrl(request) + Constants.getArticleUri() + lastLog.get("alias") + suffix);
        //没有使用md的toc目录的文章才尝试使用系统提取的目录
        if (data.getStr("markdown") != null && !data.getStr("markdown").toLowerCase().contains("[toc]") && !data.getStr("markdown").toLowerCase().contains("[tocm]")) {
            //最基础的实现方式，若需要更强大的实现方式建议使用JavaScript完成（页面输入toc对象）
            OutlineVO outlineVO = OutlineUtil.extractOutline(data.getStr("content"));
            if (outlineVO.size() > 0) {
                data.put("tocHtml", OutlineUtil.buildTocHtml(outlineVO, ""));
            }
            data.put("toc", outlineVO);
        }
        //系统关闭评论
        if (!Constants.isAllowComment()) {
            data.set("canComment", false);
        }
    }

    /**
     * 获取主题的相对于程序的路径，当Cookie中有值的情况下，优先使用Cookie里面的数据（仅当主题存在的情况下，否则返回默认的主题），
     */
    private static String getTemplatePath(HttpServletRequest request) {
        String templatePath = Constants.WEB_SITE.get("template").toString();
        templatePath = templatePath == null ? Constants.DEFAULT_TEMPLATE_PATH : templatePath;
        String previewTheme = getTemplatePathByCookie(request.getCookies());
        if (previewTheme != null) {
            templatePath = previewTheme;
        }
        if (!new File(PathKit.getWebRootPath() + templatePath).exists()) {
            templatePath = Constants.DEFAULT_TEMPLATE_PATH;
        }
        return templatePath;
    }

    public static String fullTemplateInfo(HttpServletRequest request) {
        String basePath = getTemplatePath(request);
        request.setAttribute("template", basePath);
        I18nUtil.addToRequest(PathKit.getWebRootPath() + basePath + "/language/", request,
                JFinal.me().getConstants().getDevMode());
        String jsonStr = (String) Constants.WEB_SITE.get(getTemplatePath(request) + Constants.TEMPLATE_CONFIG_SUFFIX);
        if (StringUtils.isNotEmpty(jsonStr)) {
            Map<String, Object> res = (Map<String, Object>) request.getAttribute("_res");
            res.putAll(new Gson().fromJson(jsonStr, Map.class));
        }
        fullInfo(request, Constants.isStaticHtmlStatus());
        return basePath;
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

    public static TemplateVO getTemplateVO(String contextPath, File file) {
        String templatePath = file.toString().substring(PathKit.getWebRootPath().length()).replace("\\", "/");
        TemplateVO templateVO = new TemplateVO();
        File templateInfo = new File(file + "/template.properties");
        if (templateInfo.exists()) {
            Properties properties = new Properties();
            try (InputStream in = new FileInputStream(templateInfo)) {
                properties.load(in);
                templateVO.setAuthor(properties.getProperty("author"));
                templateVO.setName(properties.getProperty("name"));
                templateVO.setDigest(properties.getProperty("digest"));
                templateVO.setVersion(properties.getProperty("version"));
                templateVO.setUrl(properties.getProperty("url"));
                templateVO.setViewType(properties.getProperty("viewType"));
                if (properties.get("previewImages") != null) {
                    String[] images = properties.get("previewImages").toString().split(",");
                    for (int i = 0; i < images.length; i++) {
                        String image = images[i];
                        if (!image.startsWith("https://") && !image.startsWith("http://")) {
                            images[i] = contextPath + templatePath + "/" + image;
                        }
                    }
                    if (images.length > 0) {
                        templateVO.setPreviewImage(images[0]);
                    }
                    templateVO.setPreviewImages(Arrays.asList(images));
                }
            } catch (IOException e) {
                //LOGGER.error("", e);
            }
        } else {
            templateVO.setAuthor("");
            templateVO.setName(templatePath.substring(Constants.TEMPLATE_BASE_PATH.length()));
            templateVO.setUrl("");
            templateVO.setViewType("jsp");
            templateVO.setVersion("");
        }
        if (templateVO.getPreviewImages() == null || templateVO.getPreviewImages().isEmpty()) {
            templateVO.setPreviewImages(Collections.singletonList("assets/images/template-default-preview.jpg"));
        }
        if (StringUtils.isEmpty(templateVO.getDigest())) {
            templateVO.setDigest(I18nUtil.getBlogStringFromRes("noIntroduction"));
        }
        File settingFile =
                new File(PathKit.getWebRootPath() + templatePath + "/setting/index" + ZrLogUtil.getViewExt(templateVO.getViewType()));
        templateVO.setConfigAble(settingFile.exists());
        templateVO.setTemplate(templatePath);
        return templateVO;
    }
}
