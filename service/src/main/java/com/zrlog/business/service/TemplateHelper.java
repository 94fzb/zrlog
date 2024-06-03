package com.zrlog.business.service;

import com.hibegin.common.util.BeanUtil;
import com.hibegin.common.util.LoggerUtil;
import com.hibegin.common.util.StringUtils;
import com.hibegin.http.server.api.HttpRequest;
import com.hibegin.http.server.util.PathUtil;
import com.hibegin.http.server.web.cookie.Cookie;
import com.zrlog.blog.web.util.WebTools;
import com.zrlog.business.cache.vo.Archive;
import com.zrlog.business.cache.vo.BaseDataInitVO;
import com.zrlog.business.util.PagerVO;
import com.zrlog.common.Constants;
import com.zrlog.common.type.RunMode;
import com.zrlog.common.vo.OutlineVO;
import com.zrlog.data.dto.PageData;
import com.zrlog.model.WebSite;
import com.zrlog.util.I18nUtil;
import com.zrlog.util.OutlineUtil;
import com.zrlog.util.ParseUtil;
import com.zrlog.util.ZrLogUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TemplateHelper {

    private static final Logger LOGGER = LoggerUtil.getLogger(TemplateHelper.class);


    public static String getSuffix(HttpRequest request) {
        if (ZrLogUtil.isStaticBlogPlugin(request) || Constants.isStaticHtmlStatus()) {
            return ".html";
        }
        return "";
    }

    private static void fullInfo(HttpRequest request) {
        boolean staticBlog = ZrLogUtil.isStaticBlogPlugin(request);
        String suffix = getSuffix(request);
        request.getAttr().put("staticBlog", staticBlog);
        request.getAttr().put("suffix", suffix);

        BaseDataInitVO baseDataInitVO = BeanUtil.cloneObject((BaseDataInitVO) request.getAttr().get("init"));
        request.getAttr().put("init", baseDataInitVO);
        Map<String, Object> webSite = baseDataInitVO.getWebSite();
        String baseUrl = setBaseUrl(request, staticBlog, webSite);
        //过期
        request.getAttr().put("webs", webSite);
        String title = webSite.get("title") + " - " + webSite.get("second_title");
        if (request.getAttr().get("log") != null) {
            title = ((Map<String, Object>) request.getAttr().get("log")).get("title") + " - " + title;
        }
        request.getAttr().put("title", title);

        staticHtml(request, suffix, Constants.getBooleanByFromWebSite("article_thumbnail_status"));
        PagerVO pager = (PagerVO) request.getAttr().get("pager");
        if (pager != null && !pager.getPageList().isEmpty()) {
            List<PagerVO.PageEntry> pageList = pager.getPageList();
            for (PagerVO.PageEntry pageMap : pageList) {
                pageMap.setUrl(baseUrl + WebTools.encodeUrl(pageMap.getUrl()) + suffix);
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

    private static void fillTags(String suffix, String baseUrl, List<Map<String, Object>> tags) {
        for (Map<String, Object> tag : tags) {
            String tagUri = baseUrl + Constants.getArticleUri() + "tag/" + URLEncoder.encode((String) tag.get("text"), StandardCharsets.UTF_8) + suffix;
            tag.put("url", tagUri);
        }
    }

    private static void fillType(String suffix, String baseUrl, List<Map<String, Object>> types) {
        for (Map<String, Object> type : types) {
            String tagUri = baseUrl + Constants.getArticleUri() + "sort/" + URLEncoder.encode((String) type.get("alias"),
                    StandardCharsets.UTF_8) + suffix;
            type.put("url", tagUri);
        }
    }

    private static boolean isHomePage(HttpRequest request) {
        String uri = request.getUri().replace(".html", "");
        return uri.isEmpty() || "/".equals(uri) || "/all-1".equals(uri) || "/all".equals(uri) || ("/" + Constants.getArticleUri() + "all").equals(uri) || ("/" + Constants.getArticleUri() + "all-1").equals(uri);
    }

    private static void fullNavBar(HttpRequest request, String suffix, BaseDataInitVO baseDataInitVO) {
        List<Map<String, Object>> logNavList = baseDataInitVO.getLogNavs();
        for (Map<String, Object> logNav : logNavList) {
            String url = logNav.get("url").toString();
            boolean current;
            if ("/".equals(url) && isHomePage(request)) {
                current = true;
            } else if (url.startsWith("/")) {
                url = getNavUrl(request, suffix, url);
                logNav.put("url", url);
                current = ignoreScheme(request.getUrl(), suffix).equals(ignoreScheme(url, suffix));
            } else {
                current = ignoreScheme(request.getUrl(), suffix).equals(ignoreScheme(url, suffix));
            }
            logNav.put("current", current);
        }
    }

    public static String getNavUrl(HttpRequest request, String suffix, String url) {
        if ("/".equals(url)) {
            return ZrLogUtil.getHomeUrlWithHost(request);
        }
        //文章页
        if (url.startsWith("/")) {
            String nUrl = ZrLogUtil.getHomeUrlWithHost(request) + url.substring(1);
            if (Objects.nonNull(suffix) && !suffix.trim().isEmpty() && nUrl.endsWith(suffix)) {
                return nUrl;
            }
            if (Objects.equals("/admin/login", url)) {
                return nUrl;
            }
            return nUrl + suffix;
        }
        return url;
    }

    private static String ignoreScheme(String url, String suffix) {
        if (suffix != null && !suffix.isEmpty() && url.endsWith(suffix)) {
            url = url.substring(0, url.length() - suffix.length());
        }
        if (url.startsWith("http://")) {
            return url.substring("http:".length());
        } else if (url.startsWith("https://")) {
            return url.substring("https:".length());
        }
        return url;
    }

    public static String setBaseUrl(HttpRequest request, boolean staticBlog, Map<String, Object> webSite) {
        String templateUrl;
        String baseUrl = WebTools.getHomeUrl(request);
        String templatePath = request.getAttr().get("template").toString();
        if (staticBlog) {
            baseUrl = "/";
            templateUrl = templatePath;
        } else {
            if (isCdnResourceAble(webSite, templatePath)) {
                templateUrl = "//" + webSite.get("staticResourceHost").toString() + request.getAttr().get("template");
                request.getAttr().put("staticResourceBaseUrl",
                        "//" + webSite.get("staticResourceHost").toString() + "/");
            } else {
                templateUrl = (String) request.getAttr().get("template");
                baseUrl = WebTools.getHomeUrl(request);
            }
        }
        request.getAttr().put("url", templateUrl);
        request.getAttr().put("templateUrl", templateUrl);
        request.getAttr().put("rurl", baseUrl);
        request.getAttr().put("baseUrl", baseUrl);
        request.getAttr().put("host", ZrLogUtil.getBlogHost(request));
        request.getAttr().put("searchUrl", baseUrl + Constants.getArticleUri() + "search");
        return baseUrl;
    }

    private static boolean isCdnResourceAble(Map<String, Object> webSite, String templatePath) {
        Properties properties = new Properties();
        File file = new File(PathUtil.getStaticPath() + templatePath + "/template.properties");
        if (file.exists()) {
            try (FileInputStream fileInputStream = new FileInputStream(file)) {
                properties.load(fileInputStream);
                if (properties.getProperty("staticResource") != null) {
                    return webSite.get("staticResourceHost") != null && !"".equals(webSite.get("staticResourceHost"));
                }
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "load properties error", e);
            }
        }
        return false;
    }

    private static void staticHtml(HttpRequest request, String suffix, boolean thumbnailEnableArticle) {
        String webSiteTitle = (String) Constants.WEB_SITE.get("title");
        String webSiteSecondTitle = (String) Constants.WEB_SITE.get("second_title");
        StringJoiner sj = new StringJoiner(" - ");
        if (request.getAttr().get("data") != null) {
            PageData<Map<String, Object>> map = (PageData) request.getAttr().get("data");
            List<Map<String, Object>> logList = map.getRows();
            if (logList != null) {
                for (Map<String, Object> log : logList) {
                    if (thumbnailEnableArticle && StringUtils.isNotEmpty((String) log.get("thumbnail"))) {
                        log.put("thumbnailAlt", ParseUtil.removeHtmlElement((String) log.get("title")));
                    } else {
                        log.put("thumbnailAlt", null);
                        log.put("thumbnail", null);
                    }
                    log.put("canComment", Objects.equals(log.get("canComment"), true) && Constants.isAllowComment());
                    log.put("url", WebTools.getHomeUrl(request) + Constants.getArticleUri() + URLEncoder.encode((String) log.get("alias"), StandardCharsets.UTF_8) + suffix);
                    log.put("typeUrl", WebTools.getHomeUrl(request) + Constants.getArticleUri() + "sort/" + URLEncoder.encode((String) log.get("typeAlias"), StandardCharsets.UTF_8) + suffix);
                    if (Objects.isNull(log.get("digest"))) {
                        log.put("digest", "");
                    }
                    if (Objects.isNull(log.get("content"))) {
                        log.put("content", "");
                    }
                }
            }
            request.getAttr().put("keywords", Constants.WEB_SITE.get("keywords"));
        } else if (request.getAttr().get("log") != null) {
            Map<String, Object> objectMap = (Map<String, Object>) request.getAttr().get("log");
            fillArticleInfo(objectMap, request, suffix);
            String articleTitle = (String) objectMap.get("title");
            if (StringUtils.isNotEmpty(articleTitle)) {
                sj.add(articleTitle);
            }
            String keywords = (String) objectMap.get("keywords");
            if (StringUtils.isNotEmpty(keywords)) {
                request.getAttr().put("keywords", keywords);
            } else {
                request.getAttr().put("keywords", Objects.requireNonNullElse(Constants.WEB_SITE.get("keywords"), ""));
            }
        }
        if (StringUtils.isNotEmpty(webSiteTitle)) {
            sj.add(webSiteTitle);
        }
        if (StringUtils.isNotEmpty(webSiteSecondTitle)) {
            sj.add(webSiteSecondTitle);
        }
        request.getAttr().put("description", Objects.requireNonNullElse(Constants.WEB_SITE.get("description"), ""));
        request.getAttr().put("title", sj.toString());
    }

    private static void fillArticleInfo(Map<String, Object> log, HttpRequest request, String suffix) {
        String aliasUrl = URLEncoder.encode((String) log.get("alias"), StandardCharsets.UTF_8) + suffix;
        log.put("alias", aliasUrl);
        log.put("canComment", Objects.equals(log.get("canComment"), true) && Constants.isAllowComment());
        log.put("url", WebTools.getHomeUrl(request) + Constants.getArticleUri() + aliasUrl);
        log.put("noSchemeUrl", ZrLogUtil.getHomeUrlWithHost(request) + aliasUrl);
        log.put("typeUrl", WebTools.getHomeUrl(request) + Constants.getArticleUri() + "sort/" + URLEncoder.encode((String) log.get("typeAlias"), StandardCharsets.UTF_8) + suffix);
        Map<String, Object> lastLog = (Map<String, Object>) log.get("lastLog");
        Map<String, Object> nextLog = (Map<String, Object>) log.get("nextLog");
        nextLog.put("url", WebTools.getHomeUrl(request) + Constants.getArticleUri() + URLEncoder.encode((String) nextLog.get("alias"), StandardCharsets.UTF_8) + suffix);
        lastLog.put("url", WebTools.getHomeUrl(request) + Constants.getArticleUri() + URLEncoder.encode((String) lastLog.get("alias"), StandardCharsets.UTF_8) + suffix);

        //没有使用md的toc目录的文章才尝试使用系统提取的目录
        if (log.get("markdown") != null && !log.get("markdown").toString().toLowerCase().contains("[toc]") && !log.get("markdown").toString().toLowerCase().contains("[tocm]")) {
            //最基础的实现方式，若需要更强大的实现方式建议使用JavaScript完成（页面输入toc对象）
            OutlineVO outlineVO = OutlineUtil.extractOutline((String) log.get("content"));
            if (!outlineVO.isEmpty()) {
                log.put("tocHtml", OutlineUtil.buildTocHtml(outlineVO, ""));
            }
            log.put("toc", outlineVO);
        }
        if (Objects.isNull(log.get("content"))) {
            log.put("content", "");
        }
    }

    /**
     * 获取主题的相对于程序的路径，当Cookie中有值的情况下，优先使用Cookie里面的数据（仅当主题存在的情况下，否则返回默认的主题），
     */
    public static String getTemplatePath(HttpRequest request) {
        String templatePath = Objects.requireNonNullElse((String) Constants.WEB_SITE.get("template"), Constants.DEFAULT_TEMPLATE_PATH);
        if (Objects.nonNull(request)) {
            String previewTheme = getTemplatePathByCookie(request.getCookies());
            if (previewTheme != null) {
                if (new File(PathUtil.getStaticPath() + templatePath).exists()) {
                    return previewTheme;
                }
                return Constants.DEFAULT_TEMPLATE_PATH;
            }
        }
        return templatePath;
    }

    public static void fullTemplateInfo(HttpRequest request) {
        String templatePath = getTemplatePath(request);
        request.getAttr().put("template", templatePath);
        I18nUtil.addToRequest(templatePath, request);
        Map<String, Object> res = (Map<String, Object>) request.getAttr().get("_res");
        res.putAll(new WebSite().getTemplateConfigMapWithCache(templatePath));
        fullInfo(request);
    }

    public static String getTemplatePathByCookie(Cookie[] cookies) {
        String previewTemplate = null;
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("template".equals(cookie.getName()) && cookie.getValue().startsWith(Constants.TEMPLATE_BASE_PATH)) {
                    previewTemplate = cookie.getValue();
                    break;
                }
            }
        }
        return previewTemplate;
    }


}
