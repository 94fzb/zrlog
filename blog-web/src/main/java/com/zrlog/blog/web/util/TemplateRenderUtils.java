package com.zrlog.blog.web.util;

import com.hibegin.common.dao.ResultValueConvertUtils;
import com.hibegin.common.dao.dto.PageData;
import com.hibegin.common.util.BeanUtil;
import com.hibegin.common.util.LoggerUtil;
import com.hibegin.common.util.StringUtils;
import com.hibegin.common.util.UrlEncodeUtils;
import com.hibegin.http.server.api.HttpRequest;
import com.hibegin.http.server.util.PathUtil;
import com.zrlog.blog.web.interceptor.TemplateUtils;
import com.zrlog.business.cache.vo.Archive;
import com.zrlog.business.cache.vo.BaseDataInitVO;
import com.zrlog.business.service.TemplateHelper;
import com.zrlog.common.Constants;
import com.zrlog.model.WebSite;
import com.zrlog.util.I18nUtil;
import com.zrlog.util.ParseUtil;
import com.zrlog.util.ZrLogUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TemplateRenderUtils {

    private static final Logger LOGGER = LoggerUtil.getLogger(TemplateHelper.class);

    public static void fullTemplateInfo(HttpRequest request) {
        String templatePath = TemplateUtils.getTemplatePath(request);
        request.getAttr().put("template", templatePath);
        I18nUtil.addToRequest(templatePath, request);
        Map<String, Object> res = (Map<String, Object>) request.getAttr().get("_res");
        res.putAll(new WebSite().getTemplateConfigMapWithCache(templatePath));
        fullInfo(request);
    }

    public static boolean isArrangeable(HttpRequest request) {
        return Objects.nonNull(request.getAttr().get("arrangePlugin"));
    }

    private static void fullInfo(HttpRequest request) {
        boolean staticBlog = Constants.zrLogConfig.isStaticPluginRequest(request);
        String suffix = Constants.getSuffix(request);
        request.getAttr().put("staticBlog", staticBlog);
        request.getAttr().put("suffix", suffix);

        BaseDataInitVO baseDataInitVO = BeanUtil.cloneObject((BaseDataInitVO) request.getAttr().get("init"));
        request.getAttr().put("init", baseDataInitVO);
        Map<String, Object> webSite = baseDataInitVO.getWebSite();
        setUrlInfo(request, staticBlog, webSite);
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
                pageMap.setUrl(WebTools.buildEncodedUrl(request, Constants.getArticleUri() + pageMap.getUrl()) + suffix);
            }

            pager.setPageStartUrl(WebTools.buildEncodedUrl(request, Constants.getArticleUri() + pager.getPageStartUrl() + suffix));
            pager.setPageEndUrl(WebTools.buildEncodedUrl(request, Constants.getArticleUri() + pager.getPageEndUrl() + suffix));
        }
        fillTags(suffix, request, baseDataInitVO.getTags());
        fillType(suffix, baseDataInitVO.getTypes(), request);
        fullNavBar(request, suffix, baseDataInitVO);
        baseDataInitVO.setArchiveList(getConvertedArchives(suffix, request, baseDataInitVO.getArchives()));
    }

    private static boolean isHomePage(HttpRequest request) {
        String uri = request.getUri().replace(".html", "");
        return uri.isEmpty() || "/".equals(uri) || "/all-1".equals(uri) || "/all".equals(uri) || ("/" + Constants.getArticleUri() + "all").equals(uri) || ("/" + Constants.getArticleUri() + "all-1").equals(uri);
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

    private static void setUrlInfo(HttpRequest request, boolean staticBlog, Map<String, Object> webSite) {
        String templateUrl;
        String templatePath = request.getAttr().get("template").toString();
        if (staticBlog) {
            templateUrl = templatePath;
        } else {
            if (isCdnResourceAble(webSite, templatePath)) {
                templateUrl = "//" + webSite.get("staticResourceHost").toString() + request.getAttr().get("template");
                request.getAttr().put("staticResourceBaseUrl",
                        "//" + webSite.get("staticResourceHost").toString() + "/");
            } else {
                templateUrl = (String) request.getAttr().get("template");
            }
        }
        String baseUrl = WebTools.getHomeUrl(request);
        request.getAttr().put("url", WebTools.buildEncodedUrl(request, templateUrl));
        request.getAttr().put("templateUrl", WebTools.buildEncodedUrl(request, templateUrl));
        request.getAttr().put("rurl", baseUrl);
        request.getAttr().put("baseUrl", baseUrl);
        request.getAttr().put("host", ZrLogUtil.getBlogHost(request));
        request.getAttr().put("searchUrl", WebTools.buildEncodedUrl(request, Constants.getArticleUri() + "search"));
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
        String webSiteTitle = Constants.getStringByFromWebSite("title", "");
        String webSiteSecondTitle = Constants.getStringByFromWebSite("second_title", "");
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
                    log.put("url", WebTools.buildEncodedUrl(request, Constants.getArticleUri() + log.get("alias") + suffix));
                    log.put("typeUrl", WebTools.buildEncodedUrl(request, Constants.getArticleUri() + "sort/" + log.get("typeAlias") + suffix));
                    log.put("releaseTime", ResultValueConvertUtils.formatDate(log.get("releaseTime"), "yyyy-MM-dd"));
                    if (Objects.isNull(log.get("digest"))) {
                        log.put("digest", "");
                    }
                    if (Objects.isNull(log.get("content"))) {
                        log.put("content", "");
                    }
                }
            }
            request.getAttr().put("keywords", Constants.getStringByFromWebSite("keywords"));
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
                request.getAttr().put("keywords", Constants.getStringByFromWebSite("keywords", ""));
            }
        }
        if (StringUtils.isNotEmpty(webSiteTitle)) {
            sj.add(webSiteTitle);
        }
        if (StringUtils.isNotEmpty(webSiteSecondTitle)) {
            sj.add(webSiteSecondTitle);
        }
        request.getAttr().put("description", Constants.getStringByFromWebSite("description", ""));
        request.getAttr().put("title", sj.toString());
    }

    private static void fillArticleInfo(Map<String, Object> log, HttpRequest request, String suffix) {
        String aliasUrl = UrlEncodeUtils.encodeUrl((String) log.get("alias")) + suffix;
        log.put("alias", aliasUrl);
        log.put("url", WebTools.buildEncodedUrl(request, Constants.getArticleUri() + aliasUrl));
        log.put("noSchemeUrl", ZrLogUtil.getHomeUrlWithHost(request) + aliasUrl);
        log.put("typeUrl", WebTools.buildEncodedUrl(request, Constants.getArticleUri() + "sort/" + log.get("typeAlias") + suffix));
        Map<String, Object> lastLog = (Map<String, Object>) log.get("lastLog");
        Map<String, Object> nextLog = (Map<String, Object>) log.get("nextLog");
        nextLog.put("url", WebTools.buildEncodedUrl(request, Constants.getArticleUri() + nextLog.get("alias") + suffix));
        lastLog.put("url", WebTools.buildEncodedUrl(request, Constants.getArticleUri() + lastLog.get("alias") + suffix));

        //没有使用md的toc目录的文章才尝试使用系统提取的目录
        if (log.get("markdown") != null && !log.get("markdown").toString().toLowerCase().contains("[toc]") && !log.get("markdown").toString().toLowerCase().contains("[tocm]")) {
            //最基础的实现方式，若需要更强大的实现方式建议使用JavaScript完成（页面输入toc对象）
            OutlineVO outlineVO = OutlineUtil.extractOutline((String) log.get("content"));
            if (!outlineVO.isEmpty()) {
                log.put("tocHtml", OutlineUtil.buildTocHtml(outlineVO, ""));
            }
            log.put("toc", outlineVO);
        }
        tryEnableArrangePlugin((String) log.get("arrange_plugin"), request);
        if (Objects.isNull(log.get("content"))) {
            log.put("content", "");
        }
    }

    private static void tryEnableArrangePlugin(String pluginName, HttpRequest request) {
        if (Objects.nonNull(pluginName) && StringUtils.isNotEmpty(pluginName)) {
            request.getAttr().put("arrangePlugin", pluginName);
        }
    }

    private static void fullNavBar(HttpRequest request, String suffix, BaseDataInitVO baseDataInitVO) {
        List<Map<String, Object>> logNavList = baseDataInitVO.getLogNavs();
        for (Map<String, Object> logNav : logNavList) {
            String url = logNav.get("url").toString();
            boolean current;
            if ("/".equals(url) && isHomePage(request)) {
                current = true;
            } else if (url.startsWith("/")) {
                url = TemplateHelper.getNavUrl(request, suffix, url);
                logNav.put("url", url);
                current = ignoreScheme(request.getUrl(), suffix).equals(ignoreScheme(url, suffix));
            } else {
                current = ignoreScheme(request.getUrl(), suffix).equals(ignoreScheme(url, suffix));
            }
            logNav.put("current", current);
        }
    }

    private static List<Archive> getConvertedArchives(String suffix, HttpRequest request, Map<String, Long> archiveMap) {
        List<Archive> archives = new ArrayList<>();
        for (Map.Entry<String, Long> entry : archiveMap.entrySet()) {
            Archive archive = new Archive();
            archive.setCount(entry.getValue());
            archive.setText(entry.getKey());
            String tagUri = WebTools.buildEncodedUrl(request, Constants.getArticleUri() + "record/" + entry.getKey() + suffix);
            archive.setUrl(tagUri);
            archives.add(archive);
        }
        return archives;
    }

    private static void fillTags(String suffix, HttpRequest request, List<Map<String, Object>> tags) {
        for (Map<String, Object> tag : tags) {
            tag.put("url", WebTools.buildEncodedUrl(request, Constants.getArticleUri() + "tag/" + tag.get("text") + suffix));
        }
    }

    private static void fillType(String suffix, List<Map<String, Object>> types, HttpRequest request) {
        for (Map<String, Object> type : types) {
            String typeUri = "/" + Constants.getArticleUri() + "sort/" + type.get("alias");
            if (request.getUri().startsWith(typeUri)) {
                tryEnableArrangePlugin((String) type.get("arrange_plugin"), request);
            }
            type.put("url", WebTools.buildEncodedUrl(request, typeUri + suffix));
        }
    }

}
