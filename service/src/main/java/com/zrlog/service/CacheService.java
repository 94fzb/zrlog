package com.zrlog.service;

import com.hibegin.common.util.FileUtils;
import com.jfinal.core.Controller;
import com.jfinal.core.JFinal;
import com.jfinal.kit.PathKit;
import com.zrlog.common.Constants;
import com.zrlog.model.*;

import java.io.File;
import java.util.*;

/**
 * 对缓存数据的操作
 */
public class CacheService {

    private static Map<String, String> cacheFileMap = new HashMap<>();

    public void refreshInitDataCache(String cachePath, Controller baseController, boolean cleanAble) {
        if (cleanAble) {
            clearCache();
            FileUtils.deleteFile(cachePath);
            Tag.dao.refreshTag();
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
            Map<String, Object> website = WebSite.dao.getWebSite();
            //兼容早期模板判断方式
            website.put("user_comment_pluginStatus", "on".equals(website.get("duoshuo_status")));

            BaseDataInitVO.Statistics statistics = new BaseDataInitVO.Statistics();
            statistics.setTotalArticleSize(Log.dao.count());
            cacheInit.setStatistics(statistics);
            cacheInit.setWebSite(website);
            cacheInit.setLinks(Link.dao.find());
            cacheInit.setTypes(Type.dao.find());
            statistics.setTotalTypeSize(cacheInit.getTypes().size());
            cacheInit.setLogNavs(LogNav.dao.find());
            cacheInit.setPlugins(Plugin.dao.find());
            cacheInit.setArchives(Log.dao.getArchives());
            cacheInit.setTags(Tag.dao.find());
            statistics.setTotalTagSize(cacheInit.getTags().size());
            List<Type> types = cacheInit.getTypes();
            cacheInit.setHotLogs((List<Log>) Log.dao.find(1, 6).get("rows"));
            Map<Map<String, Object>, List<Log>> indexHotLog = new LinkedHashMap<>();
            for (Type type : types) {
                Map<String, Object> typeMap = new TreeMap<>();
                typeMap.put("typeName", type.getStr("typeName"));
                typeMap.put("alias", type.getStr("alias"));
                indexHotLog.put(typeMap, (List<Log>) Log.dao.findByTypeAlias(1, 6, type.getStr("alias")).get("rows"));
            }
            cacheInit.setIndexHotLogs(indexHotLog);
            //存放公共数据到ServletContext
            JFinal.me().getServletContext().setAttribute("WEB_SITE", website);
            JFinal.me().getServletContext().setAttribute(Constants.CACHE_KEY, cacheInit);
            List<File> staticFiles = new ArrayList<>();
            FileUtils.getAllFiles(PathKit.getWebRootPath(), staticFiles);
            for (File file : staticFiles) {
                String uri = file.toString().substring(PathKit.getWebRootPath().length());
                cacheFileMap.put(uri, file.lastModified() + "");
            }
        }
        if (baseController != null) {
            baseController.setAttr("init", cacheInit);
            baseController.setAttr("website", cacheInit.getWebSite());
            //默认开启文章封面
            cacheInit.getWebSite().putIfAbsent("article_thumbnail_status", "1");
            Constants.WEB_SITE.clear();
            Constants.WEB_SITE.putAll(cacheInit.getWebSite());
        }
    }

    public static String getFileFlag(String uri) {
        if (JFinal.me().getConstants().getDevMode()) {
            return new File(PathKit.getWebRootPath() + uri).lastModified() + "";
        }
        return cacheFileMap.get(uri);
    }
}
