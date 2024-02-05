package com.zrlog.business.service;

import com.google.gson.Gson;
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
import com.zrlog.common.vo.OutlineVO;
import com.zrlog.common.vo.TemplateVO;
import com.zrlog.data.dto.PageData;
import com.zrlog.util.I18nUtil;
import com.zrlog.util.OutlineUtil;
import com.zrlog.util.ParseUtil;
import com.zrlog.util.ZrLogUtil;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TemplateHelper {

    private static final Logger LOGGER = LoggerUtil.getLogger(TemplateHelper.class);

    public static String getSuffix(HttpRequest request){
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

        BaseDataInitVO baseDataInitVO = BeanUtil.cloneObject(request.getAttr().get("init"));
        request.getAttr().put("init", baseDataInitVO);
        Map<String, Object> webSite = baseDataInitVO.getWebSite();
        String baseUrl = setBaseUrl(request, staticBlog, webSite);
        //过期
        request.getAttr().put("webs", webSite);
        String title = webSite.get("title") + " - " + webSite.get("second_title");
        if (request.getAttr().get("log") != null) {
            title = ((Map<String,Object>) request.getAttr().get("log")).get("title") + " - " + title;
        }
        request.getAttr().put("title", title);

        staticHtml(request, suffix, Constants.getBooleanByFromWebSite("article_thumbnail_status"));
        PagerVO pager = (PagerVO) request.getAttr().get("pager");
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
        return "".equals(uri) || "/".equals(uri) || "/all-1".equals(uri) || "/all".equals(uri) || ("/" + Constants.getArticleUri() + "all").equals(uri) || ("/" + Constants.getArticleUri() + "all-1").equals(uri);
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

    private static String getNavUrl(HttpRequest request, String suffix, String url) {
        if ("/".equals(url)) {
            return WebTools.getHomeUrlWithHost(request);
        }
        //文章页
        else if (url.startsWith("/")) {
            String nUrl = WebTools.getHomeUrlWithHost(request) + url.substring(1);
            if (!nUrl.endsWith(suffix)) {
                return nUrl + suffix;
            }
            return nUrl;
        }
        return url;
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
        request.getAttr().put("host", request.getHeader("host"));
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
                    log.put("url",
                            WebTools.getHomeUrl(request) + Constants.getArticleUri() + log.get("alias") + suffix);
                    log.put("typeUrl", WebTools.getHomeUrl(request) + Constants.getArticleUri() + "sort/" + log.get(
                            "typeAlias") + suffix);
                }
            }
        } else if (request.getAttr().get("log") != null) {
            fillArticleInfo((Map<String, Object>) request.getAttr().get("log"), request, suffix);
        }
    }

    private static void fillArticleInfo(Map<String, Object> log, HttpRequest request, String suffix) {
        log.put("alias", log.get("alias") + suffix);
        log.put("canComment", Objects.equals(log.get("canComment"), true) && Constants.isAllowComment());
        log.put("url", WebTools.getHomeUrl(request) + Constants.getArticleUri() + log.get("alias"));
        log.put("noSchemeUrl", WebTools.getHomeUrlWithHost(request) + Constants.getArticleUri() + log.get("alias"));
        log.put("typeUrl",
                WebTools.getHomeUrl(request) + Constants.getArticleUri() + "sort/" + log.get("typeAlias") + suffix);
        Map<String, Object> lastLog = (Map<String, Object>) log.get("lastLog");
        Map<String, Object> nextLog = (Map<String, Object>) log.get("nextLog");
        nextLog.put("url", WebTools.getHomeUrl(request) + Constants.getArticleUri() + nextLog.get("alias") + suffix);
        lastLog.put("url", WebTools.getHomeUrl(request) + Constants.getArticleUri() + lastLog.get("alias") + suffix);

        //没有使用md的toc目录的文章才尝试使用系统提取的目录
        if (log.get("markdown") != null && !log.get("markdown").toString().toLowerCase().contains("[toc]") && !log.get("markdown").toString().toLowerCase().contains("[tocm]")) {
            //最基础的实现方式，若需要更强大的实现方式建议使用JavaScript完成（页面输入toc对象）
            OutlineVO outlineVO = OutlineUtil.extractOutline((String) log.get("content"));
            if (!outlineVO.isEmpty()) {
                log.put("tocHtml", OutlineUtil.buildTocHtml(outlineVO, ""));
            }
            log.put("toc", outlineVO);
        }
    }

    /**
     * 获取主题的相对于程序的路径，当Cookie中有值的情况下，优先使用Cookie里面的数据（仅当主题存在的情况下，否则返回默认的主题），
     */
    private static String getTemplatePath(HttpRequest request) {
        String templatePath = Constants.WEB_SITE.get("template").toString();
        templatePath = templatePath == null ? Constants.DEFAULT_TEMPLATE_PATH : templatePath;
        String previewTheme = getTemplatePathByCookie(request.getCookies());
        if (previewTheme != null) {
            templatePath = previewTheme;
        }
        if (!new File(PathUtil.getStaticPath() + templatePath).exists()) {
            templatePath = Constants.DEFAULT_TEMPLATE_PATH;
        }
        return templatePath;
    }

    public static void fullTemplateInfo(HttpRequest request) {
        String basePath = getTemplatePath(request);
        request.getAttr().put("template", basePath);
        I18nUtil.addToRequest(PathUtil.getStaticPath() + basePath + "/language/", request, false);
        String jsonStr = (String) Constants.WEB_SITE.get(getTemplatePath(request) + Constants.TEMPLATE_CONFIG_SUFFIX);
        if (StringUtils.isNotEmpty(jsonStr)) {
            Map<String, Object> res = (Map<String, Object>) request.getAttr().get("_res");
            res.putAll(new Gson().fromJson(jsonStr, Map.class));
        }
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

    public static TemplateVO getTemplateVO(File file) {
        String templatePath = file.toString().substring(PathUtil.getStaticPath().length() - 1).replace("\\", "/");
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
                            images[i] = templatePath + "/" + image;
                        }
                    }
                    if (images.length > 0) {
                        templateVO.setPreviewImage(images[0]);
                    }
                    templateVO.setPreviewImages(Arrays.asList(images));
                }
            } catch (IOException e) {
                //LOGGER.log(Level.SEVERE,"", e);
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
                new File(PathUtil.getStaticPath() + templatePath + "/setting/index" + ZrLogUtil.getViewExt(templateVO.getViewType()));
        templateVO.setConfigAble(settingFile.exists());
        templateVO.setTemplate(templatePath);
        return templateVO;
    }
}
