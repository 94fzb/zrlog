package com.zrlog.business.cache;

import com.hibegin.common.util.FileUtils;
import com.jfinal.core.Controller;
import com.jfinal.core.JFinal;
import com.jfinal.kit.PathKit;
import com.zrlog.business.cache.vo.BaseDataInitVO;
import com.zrlog.business.rest.response.ArticleGlobalResponse;
import com.zrlog.common.Constants;
import com.zrlog.common.rest.request.PageRequest;
import com.zrlog.model.*;

import java.io.File;
import java.util.*;

/**
 * 对缓存数据的操作
 */
public class CacheService {

    public static final String CACHE_HTML_PATH = PathKit.getWebRootPath() + "/_cache/";
    private static final Map<String, String> cacheFileMap = new HashMap<>();

    public void refreshInitDataCache(String cachePath, Controller baseController, boolean cleanAble) {
        if (cleanAble) {
            clearCache();
            FileUtils.deleteFile(cachePath);
            new Tag().refreshTag();
        }
        initCache(baseController);
    }

    private void clearCache() {
        JFinal.me().getServletContext().removeAttribute(Constants.CACHE_KEY);
    }

    private void initCache(Controller baseController) {
        BaseDataInitVO cacheInit = (BaseDataInitVO) JFinal.me().getServletContext().getAttribute(Constants.CACHE_KEY);
        if (cacheInit == null) {
            cacheInit = new BaseDataInitVO();
            Map<String, Object> website = new WebSite().getWebSite();
            //兼容早期模板判断方式
            website.put("user_comment_pluginStatus", Constants.getBooleanByFromWebSite("duoshuo_status"));

            BaseDataInitVO.Statistics statistics = new BaseDataInitVO.Statistics();
            statistics.setTotalArticleSize(new Log().count());
            cacheInit.setStatistics(statistics);
            cacheInit.setWebSite(website);
            cacheInit.setLinks(new Link().findAll());
            cacheInit.setTypes(new Type().findAll());
            statistics.setTotalTypeSize(cacheInit.getTypes().size());
            cacheInit.setLogNavs(new LogNav().findAll());
            cacheInit.setPlugins(new Plugin().findAll());
            cacheInit.setArchives(new Log().getArchives());
            cacheInit.setTags(new Tag().findAll());
            statistics.setTotalTagSize(cacheInit.getTags().size());
            List<Type> types = cacheInit.getTypes();
            cacheInit.setHotLogs(new Log().adminFind(new PageRequest(1, 6)).getRows());
            Map<Map<String, Object>, List<Log>> indexHotLog = new LinkedHashMap<>();
            for (Type type : types) {
                Map<String, Object> typeMap = new TreeMap<>();
                typeMap.put("typeName", type.getStr("typeName"));
                typeMap.put("alias", type.getStr("alias"));
                indexHotLog.put(typeMap, new Log().findByTypeAlias(1, 6, type.getStr("alias")).getRows());
            }
            cacheInit.setIndexHotLogs(indexHotLog);
            //存放公共数据到ServletContext
            JFinal.me().getServletContext().setAttribute("WEB_SITE", website);
            JFinal.me().getServletContext().setAttribute(Constants.CACHE_KEY, cacheInit);
            List<File> staticFiles = new ArrayList<>();
            FileUtils.getAllFiles(PathKit.getWebRootPath(), staticFiles);
            for (File file : staticFiles) {
                String uri = file.toString().substring(PathKit.getWebRootPath().length());
                if (!uri.startsWith("/assets") &&
                        !uri.startsWith("/include") &&
                        !Objects.equals("/favicon.ico", uri)) {
                    continue;
                }
                if (uri.endsWith(".jsp")) {
                    continue;
                }
                cacheFileMap.put(uri.substring(1), file.lastModified() + "");
            }
        }
        if (baseController != null) {
            final BaseDataInitVO cacheInitFile = cacheInit;
            if (cacheInit.getTags() == null || cacheInit.getTags().isEmpty()) {
                cacheInit.getPlugins().stream().filter(e -> e.get("pluginName").equals("tags")).findFirst().ifPresent(e -> {
                    cacheInitFile.getPlugins().remove(e);
                });
            }
            if (cacheInit.getArchives() == null || cacheInit.getArchives().isEmpty()) {
                cacheInit.getPlugins().stream().filter(e -> e.get("pluginName").equals("archives")).findFirst().ifPresent(e -> {
                    cacheInitFile.getPlugins().remove(e);
                });
            }
            if (cacheInit.getTypes() == null || cacheInit.getTypes().isEmpty()) {
                cacheInit.getPlugins().stream().filter(e -> e.get("pluginName").equals("types")).findFirst().ifPresent(e -> {
                    cacheInitFile.getPlugins().remove(e);
                });
            }
            if (cacheInit.getLinks() == null || cacheInit.getLinks().isEmpty()) {
                cacheInit.getPlugins().stream().filter(e -> e.get("pluginName").equals("links")).findFirst().ifPresent(e -> {
                    cacheInitFile.getPlugins().remove(e);
                });
            }
            baseController.setAttr("init", cacheInitFile);
            baseController.setAttr("website", cacheInitFile.getWebSite());
            //默认开启文章封面
            cacheInitFile.getWebSite().putIfAbsent("article_thumbnail_status", "1");
            Constants.WEB_SITE.clear();
            Constants.WEB_SITE.putAll(cacheInitFile.getWebSite());
        }
    }

    public static String getFileFlag(String uri) {
        if (JFinal.me().getConstants().getDevMode()) {
            return new File(PathKit.getWebRootPath() + uri).lastModified() + "";
        }
        return cacheFileMap.get(uri);
    }

    public ArticleGlobalResponse global() {
        ArticleGlobalResponse response = new ArticleGlobalResponse();
        response.setTags(new Tag().findAll());
        response.setTypes(new Type().findAll());
        return response;
    }
}
