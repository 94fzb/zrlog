package com.zrlog.business.cache;

import com.hibegin.common.util.FileUtils;
import com.hibegin.common.util.IOUtil;
import com.jfinal.core.JFinal;
import com.jfinal.kit.PathKit;
import com.zrlog.business.cache.vo.BaseDataInitVO;
import com.zrlog.common.Constants;
import com.zrlog.common.rest.request.PageRequest;
import com.zrlog.model.*;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 对缓存数据的操作
 */
public class CacheService {

    private static final String CACHE_HTML_PATH = PathKit.getWebRootPath() + "/_cache/";

    private static final Map<String, String> cacheFileMap = new HashMap<>();

    private static final ReentrantLock lock = new ReentrantLock();

    private static BaseDataInitVO cacheInit;

    public static String getFileFlag(String uri) {
        if (JFinal.me().getConstants().getDevMode()) {
            return new File(PathKit.getWebRootPath() + uri).lastModified() + "";
        }
        return cacheFileMap.get(uri);
    }

    public File loadHtmlFile(String cacheKey) {
        return new File(CACHE_HTML_PATH + cacheKey);
    }

    /**
     * 将一个网页转化对应文件，用于静态化文章页
     */
    public void saveResponseBodyToHtml(File file, String copy) {
        if (copy == null) {
            return;
        }
        byte[] bytes = copy.getBytes(StandardCharsets.UTF_8);
        FileUtils.tryResizeDiskSpace(CACHE_HTML_PATH + Constants.getArticleUri(), bytes.length,
                Constants.getMaxCacheHtmlSize());
        if (!file.exists()) {
            file.getParentFile().mkdirs();
        }
        IOUtil.writeBytesToFile(bytes, file);
    }

    private Map<String, String> getCacheFileMap() {
        //cache fileMap
        List<File> staticFiles = new ArrayList<>();
        FileUtils.getAllFiles(PathKit.getWebRootPath(), staticFiles);
        Map<String, String> cacheMap = new HashMap<>();
        for (File file : staticFiles) {
            String uri = file.toString().substring(PathKit.getWebRootPath().length());
            if (!uri.startsWith("/assets") && !uri.startsWith("/include") && !Objects.equals("/favicon.ico", uri)) {
                continue;
            }
            if (uri.endsWith(".jsp")) {
                continue;
            }
            cacheMap.put(uri.substring(1), file.lastModified() + "");
        }
        return cacheMap;
    }

    public void refreshInitDataCacheAsync(HttpServletRequest servletRequest, boolean cleanAble) {
        CompletableFuture.runAsync(() -> {
            refreshInitDataCache(servletRequest, cleanAble);
        });
    }

    public void refreshInitDataCache(HttpServletRequest servletRequest, boolean cleanAble) {
        if (cleanAble || cacheInit == null) {
            long start = System.currentTimeMillis();
            lock.lock();
            try {
                cacheInit = getCacheInit();
                FileUtils.deleteFile(CACHE_HTML_PATH);
                new Tag().refreshTag();
                //缓存静态资源map
                Map<String, String> tempMap = getCacheFileMap();
                cacheFileMap.clear();
                //重新填充Map
                cacheFileMap.putAll(tempMap);
            } finally {
                lock.unlock();
                LoggerFactory.getLogger(CacheService.class).info("refreshInitDataCache used time {}ms",
                        (System.currentTimeMillis() - start));
            }
        }
        if (servletRequest != null) {
            servletRequest.setAttribute("init", cacheInit);
            servletRequest.setAttribute("website", cacheInit.getWebSite());
            //存放公共数据到ServletContext
            servletRequest.getServletContext().setAttribute("WEB_SITE", Constants.WEB_SITE);
        }
        Constants.WEB_SITE.clear();
        Constants.WEB_SITE.putAll(cacheInit.getWebSite());

    }

    private BaseDataInitVO getCacheInit() {
        BaseDataInitVO cacheInit = new BaseDataInitVO();
        Map<String, Object> website = new WebSite().getWebSite();
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
        cacheInit.setHotLogs(new Log().visitorFind(new PageRequest(1, 6), "").getRows());
        Map<Map<String, Object>, List<Log>> indexHotLog = new LinkedHashMap<>();
        for (Type type : types) {
            Map<String, Object> typeMap = new TreeMap<>();
            typeMap.put("typeName", type.getStr("typeName"));
            typeMap.put("alias", type.getStr("alias"));
            indexHotLog.put(typeMap, new Log().findByTypeAlias(1, 6, type.getStr("alias")).getRows());
        }
        cacheInit.setIndexHotLogs(indexHotLog);

        if (cacheInit.getTags() == null || cacheInit.getTags().isEmpty()) {
            cacheInit.getPlugins().stream().filter(e -> e.get("pluginName").equals("tags")).findFirst().ifPresent(e -> {
                cacheInit.getPlugins().remove(e);
            });
        }
        if (cacheInit.getArchives() == null || cacheInit.getArchives().isEmpty()) {
            cacheInit.getPlugins().stream().filter(e -> e.get("pluginName").equals("archives")).findFirst().ifPresent(e -> {
                cacheInit.getPlugins().remove(e);
            });
        }
        if (cacheInit.getTypes() == null || cacheInit.getTypes().isEmpty()) {
            cacheInit.getPlugins().stream().filter(e -> e.get("pluginName").equals("types")).findFirst().ifPresent(e -> {
                cacheInit.getPlugins().remove(e);
            });
        }
        if (cacheInit.getLinks() == null || cacheInit.getLinks().isEmpty()) {
            cacheInit.getPlugins().stream().filter(e -> e.get("pluginName").equals("links")).findFirst().ifPresent(e -> {
                cacheInit.getPlugins().remove(e);
            });
        }
        //默认开启文章封面
        cacheInit.getWebSite().putIfAbsent("article_thumbnail_status", "1");
        return cacheInit;
    }

}
