package com.zrlog.business.cache;

import com.hibegin.common.util.FileUtils;
import com.hibegin.common.util.IOUtil;
import com.hibegin.common.util.LoggerUtil;
import com.hibegin.http.server.api.HttpRequest;
import com.hibegin.http.server.util.PathUtil;
import com.zrlog.business.cache.vo.BaseDataInitVO;
import com.zrlog.business.plugin.StaticHtmlPlugin;
import com.zrlog.business.plugin.TemplateDownloadPlugin;
import com.zrlog.common.Constants;
import com.zrlog.common.rest.request.PageRequest;
import com.zrlog.model.*;
import com.zrlog.plugin.IPlugin;
import com.zrlog.util.I18nUtil;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

/**
 * 对缓存数据的操作
 */
public class CacheService {

    private static final String CACHE_HTML_PATH = PathUtil.getCachePath();
    private static final Logger LOGGER = LoggerUtil.getLogger(CacheService.class);

    private static final Map<String, String> cacheFileMap = new HashMap<>();

    public boolean isCacheableByRequest(HttpRequest request) {
        //disable html client cache
        if (request.getUri().endsWith(".html")) {
            return false;
        }
        return cacheFileMap.containsKey(request.getUri().substring(1));
    }

    private static final ReentrantLock lock = new ReentrantLock();

    private static BaseDataInitVO cacheInit;

    public static String getFileFlag(String uri) {
        return cacheFileMap.get(uri);
    }

    public File loadCacheFile(HttpRequest request) {
        String lang = I18nUtil.getAcceptLocal(request);
        String cacheKey = request.getUri();
        if (Objects.equals(cacheKey, "/")) {
            cacheKey = "/index.html";
        }
        return new File(CACHE_HTML_PATH + lang + "/" + cacheKey);
    }

    /**
     * 将一个网页转化对应文件，用于静态化文章页
     */
    public File saveResponseBodyToHtml(HttpRequest httpRequest, String copy) {
        if (copy == null) {
            return null;
        }
        byte[] bytes = copy.getBytes(StandardCharsets.UTF_8);
        FileUtils.tryResizeDiskSpace(CACHE_HTML_PATH, bytes.length,
                Constants.getMaxCacheHtmlSize());
        File htmlFile = loadCacheFile(httpRequest);
        if (!htmlFile.exists()) {
            htmlFile.getParentFile().mkdirs();
        }
        IOUtil.writeBytesToFile(bytes, htmlFile);
        return htmlFile;
    }

    private Map<String, String> getCacheFileMap() {
        //cache fileMap
        List<File> staticFiles = new ArrayList<>();
        FileUtils.getAllFiles(PathUtil.getStaticPath(), staticFiles);
        Map<String, String> cacheMap = new HashMap<>();
        List<String> cacheableFileExts = Arrays.asList(".css", ".js", ".png", ".jpg", ".png", ".webp", ".ico");
        for (File file : staticFiles) {
            String uri = file.toString().substring(PathUtil.getStaticPath().length());
            if (!uri.startsWith("include/") && !Objects.equals("favicon.ico", uri)) {
                continue;
            }
            if (cacheableFileExts.stream().noneMatch(e -> uri.toLowerCase().endsWith(e))) {
                continue;
            }
            cacheMap.put(uri, file.lastModified() + "");
        }
        return cacheMap;
    }

    public void refreshInitDataCacheAsync(HttpRequest servletRequest, boolean cleanAble) {
        CompletableFuture.runAsync(() -> {
            refreshInitDataCache(servletRequest, cleanAble);
        });
    }

    private void refreshWebSite(Map<String, Object> newWebSite) {
        for (String key : Constants.WEB_SITE.keySet()) {
            if (!newWebSite.containsKey(key)) {
                Constants.WEB_SITE.remove(key);
            }
        }
        Constants.WEB_SITE.putAll(newWebSite);
    }

    public void refreshInitDataCache(HttpRequest servletRequest, boolean cleanAble) {
        if (cleanAble || cacheInit == null) {
            long start = System.currentTimeMillis();
            lock.lock();
            try {
                cacheInit = getCacheInit();
                new Tag().refreshTag();
                //缓存静态资源map
                Map<String, String> tempMap = getCacheFileMap();
                cacheFileMap.clear();
                //重新填充Map
                cacheFileMap.putAll(tempMap);
                //清除模版的缓存数据
                WebSite.clearTemplateConfigMap();
                //静态化插件，重新生成全量的 html
                Optional<IPlugin> first = Constants.zrLogConfig.getPlugins().stream().filter(e -> e instanceof StaticHtmlPlugin).findFirst();
                Optional<IPlugin> templatePlugin = Constants.zrLogConfig.getPlugins().stream().filter(e -> e instanceof TemplateDownloadPlugin).findFirst();
                templatePlugin.ifPresent(IPlugin::start);
                if (first.isPresent()) {
                    StaticHtmlPlugin staticHtmlPlugin = (StaticHtmlPlugin) first.get();
                    //restart plugin, for update
                    staticHtmlPlugin.stop();
                    staticHtmlPlugin.start();
                }
                refreshWebSite(cacheInit.getWebSite());

            } catch (SQLException e) {
                throw new RuntimeException(e);
            } finally {
                lock.unlock();
                if (Constants.DEV_MODE) {
                    LOGGER.info("RefreshInitDataCache used time " + (System.currentTimeMillis() - start) + "ms");
                }
            }
        }
        if (Objects.isNull(servletRequest)) {
            return;
        }
        servletRequest.getAttr().put("init", cacheInit);
        servletRequest.getAttr().put("website", cacheInit.getWebSite());
        servletRequest.getAttr().put("WEB_SITE", cacheInit.getWebSite());
    }

    public Map<String, Object> refreshWebSite() {
        Map<String, Object> website = new WebSite().getWebSite();
        refreshWebSite(website);
        return Constants.WEB_SITE;
    }

    private BaseDataInitVO getCacheInit() throws SQLException {
        BaseDataInitVO cacheInit = new BaseDataInitVO();
        BaseDataInitVO.Statistics statistics = new BaseDataInitVO.Statistics();
        statistics.setTotalArticleSize(new Log().count());
        cacheInit.setStatistics(statistics);
        cacheInit.setWebSite(refreshWebSite());
        cacheInit.setLinks(new Link().findAll());
        cacheInit.setTypes(new Type().findAll());
        statistics.setTotalTypeSize(cacheInit.getTypes().size());
        cacheInit.setLogNavs(new LogNav().findAll());
        cacheInit.setPlugins(new Plugin().findAll());
        cacheInit.setArchives(new Log().getArchives());
        List<Map<String, Object>> all = new Tag().findAll();
        for (Map<String, Object> kv : all) {
            kv.put("keycode", kv.get("text").hashCode());
        }
        cacheInit.setTags(all);
        statistics.setTotalTagSize(cacheInit.getTags().size());
        List<Map<String, Object>> types = cacheInit.getTypes();
        cacheInit.setHotLogs(new Log().visitorFind(new PageRequest(1, 6), "").getRows());
        Map<Map<String, Object>, List<Map<String, Object>>> indexHotLog = new LinkedHashMap<>();
        for (Map<String, Object> type : types) {
            Map<String, Object> typeMap = new TreeMap<>();
            typeMap.put("typeName", type.get("typeName"));
            typeMap.put("alias", type.get("alias"));
            indexHotLog.put(typeMap, new Log().findByTypeAlias(1, 6, (String) type.get("alias")).getRows());
        }
        cacheInit.setIndexHotLogs(indexHotLog);

        if (cacheInit.getTags() == null || cacheInit.getTags().isEmpty()) {
            cacheInit.getPlugins().stream().filter(e -> Objects.equals(e.get("pluginName"), "tags")).findFirst().ifPresent(e -> {
                cacheInit.getPlugins().remove(e);
            });
        }
        if (cacheInit.getArchives() == null || cacheInit.getArchives().isEmpty()) {
            cacheInit.getPlugins().stream().filter(e -> Objects.equals(e.get("pluginName"), "archives")).findFirst().ifPresent(e -> {
                cacheInit.getPlugins().remove(e);
            });
        }
        if (cacheInit.getTypes() == null || cacheInit.getTypes().isEmpty()) {
            cacheInit.getPlugins().stream().filter(e -> Objects.equals(e.get("pluginName"), "types")).findFirst().ifPresent(e -> {
                cacheInit.getPlugins().remove(e);
            });
        }
        if (cacheInit.getLinks() == null || cacheInit.getLinks().isEmpty()) {
            cacheInit.getPlugins().stream().filter(e -> Objects.equals(e.get("pluginName"), "links")).findFirst().ifPresent(e -> {
                cacheInit.getPlugins().remove(e);
            });
        }
        //默认开启文章封面
        cacheInit.getWebSite().putIfAbsent("article_thumbnail_status", "1");
        return cacheInit;
    }

}
