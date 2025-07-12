package com.zrlog.business.cache;

import com.google.gson.Gson;
import com.hibegin.common.BaseLockObject;
import com.hibegin.common.dao.ResultValueConvertUtils;
import com.hibegin.common.dao.dto.PageRequestImpl;
import com.hibegin.common.util.*;
import com.hibegin.http.server.api.HttpRequest;
import com.hibegin.http.server.util.PathUtil;
import com.zrlog.business.cache.vo.BaseDataInitVO;
import com.zrlog.business.cache.vo.HotLogBasicInfoVO;
import com.zrlog.business.plugin.StaticSitePlugin;
import com.zrlog.common.AdminResource;
import com.zrlog.common.CacheService;
import com.zrlog.common.Constants;
import com.zrlog.common.ZrLogConfig;
import com.zrlog.data.dto.FaviconBase64DTO;
import com.zrlog.model.*;
import com.zrlog.util.I18nUtil;
import com.zrlog.util.ThreadUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * 对缓存数据的操作
 */
public class CacheServiceImpl extends BaseLockObject implements CacheService<BaseDataInitVO> {
    private static final Logger LOGGER = LoggerUtil.getLogger(CacheServiceImpl.class);

    private final AtomicLong version = new AtomicLong(System.currentTimeMillis());
    private final Map<String, String> cacheFileMap = new ConcurrentHashMap<>();
    private final String contextPath;
    private volatile BaseDataInitVO cacheInit;
    private final ExecutorService executor;

    public CacheServiceImpl(String contextPath) {
        this.contextPath = contextPath;
        this.executor = ThreadUtils.newFixedThreadPool(10);

    }

    private static String getStreamTag(InputStream inputStream) {
        return Math.abs(SecurityUtils.md5(inputStream).hashCode()) + "";
    }

    private static void setToRequest(HttpRequest servletRequest, BaseDataInitVO cacheInit) {
        if (Objects.isNull(servletRequest)) {
            return;
        }
        if (Objects.isNull(cacheInit)) {
            return;
        }
        servletRequest.getAttr().put("init", cacheInit);
        servletRequest.getAttr().put("website", cacheInit.getWebSite());
        //website alias
        servletRequest.getAttr().put("webSite", cacheInit.getWebSite());
        servletRequest.getAttr().put("webs", cacheInit.getWebSite());
        servletRequest.getAttr().put("WEB_SITE", cacheInit.getWebSite());
    }

    @Override
    public boolean isCacheableByRequest(HttpRequest request) {
        //disable html client cache
        if (request.getUri().endsWith(".html")) {
            return false;
        }
        return cacheFileMap.containsKey(request.getUri().substring(1));
    }

    @Override
    public String getFileFlagFirstByCache(String uri) {
        if (uri.startsWith("/")) {
            uri = uri.substring(1);
        }
        String s = cacheFileMap.get(uri);
        if (Objects.nonNull(s)) {
            return s;
        }
        if (("/" + uri).startsWith(Constants.DEFAULT_TEMPLATE_PATH) || uri.startsWith("assets/") || uri.startsWith("pwa/") || Objects.equals(uri, "favicon.ico")) {
            InputStream inputStream = CacheServiceImpl.class.getResourceAsStream("/" + uri);
            if (Objects.isNull(inputStream)) {
                return null;
            }
            String flag = getStreamTag(inputStream);
            cacheFileMap.put(uri, flag);
            return flag;
        }
        File staticFile = PathUtil.getStaticFile("/" + uri);
        if (!staticFile.exists()) {
            return null;
        }
        if (staticFile.isDirectory()) {
            return null;
        }
        try (FileInputStream fileInputStream = new FileInputStream(staticFile)) {
            String streamTag = getStreamTag(fileInputStream);
            cacheFileMap.put(uri, streamTag);
            return streamTag;
        } catch (IOException e) {
            LOGGER.warning("Get " + uri + " stream tag error " + e.getMessage());
        }
        return null;
    }

    @Override
    public void refreshFavicon() {
        FaviconBase64DTO faviconBase64DTO = new WebSite().faviconBase64DTO();
        faviconHandle(faviconBase64DTO.getFavicon_ico_base64(), Constants.FAVICON_ICO_URI_PATH, ResultValueConvertUtils.toBoolean(faviconBase64DTO.getGenerator_html_status()));
        faviconHandle(faviconBase64DTO.getFavicon_png_pwa_192_base64(), Constants.FAVICON_PNG_PWA_192_URI_PATH, ResultValueConvertUtils.toBoolean(faviconBase64DTO.getGenerator_html_status()));
        faviconHandle(faviconBase64DTO.getFavicon_png_pwa_512_base64(), Constants.FAVICON_PNG_PWA_512_URI_PATH, ResultValueConvertUtils.toBoolean(faviconBase64DTO.getGenerator_html_status()));
    }


    @Override
    public long getWebSiteVersion() {
        if (Objects.isNull(cacheInit)) {
            return 0;
        }
        return ObjectUtil.requireNonNullElse(cacheInit.getWebSiteVersion(), 0L);
    }


    private Map<String, String> getCacheFileMap() {
        //cache fileMap
        List<File> staticFiles = new ArrayList<>();
        FileUtils.getAllFiles(PathUtil.getStaticPath(), staticFiles);
        Map<String, String> cacheMap = new HashMap<>();
        List<String> cacheableFileExts = Arrays.asList(".css", ".js", ".png", ".jpg", ".png", ".webp", ".ico");
        for (File file : staticFiles) {
            //目录长度不够
            if (file.toString().length() <= PathUtil.getStaticPath().length()) {
                continue;
            }
            String uri = file.toString().substring(PathUtil.getStaticPath().length());
            if (cacheableFileExts.stream().noneMatch(e -> uri.toLowerCase().endsWith(e))) {
                continue;
            }
            getFileFlagFirstByCache(uri);
        }
        return cacheMap;
    }

    private long getUpdateVersion(boolean cleanAble) {
        if (Objects.isNull(cacheInit) || cleanAble) {
            return version.incrementAndGet();
        }
        return version.get();
    }

    @Override
    public CompletableFuture<BaseDataInitVO> refreshInitDataCacheAsync(HttpRequest servletRequest, boolean cleanAble) {
        long expectVersion = getUpdateVersion(cleanAble);
        return CompletableFuture.supplyAsync(() -> {
            BaseDataInitVO cache = refreshInitDataCache(cleanAble, expectVersion);
            setToRequest(servletRequest, cache);
            return cache;
        }, executor);
    }

    private BaseDataInitVO refreshInitDataCache(boolean cleanAble, long expectVersion) {
        if (!cleanAble && Objects.nonNull(cacheInit)) {
            return cacheInit;
        }
        lock.lock();
        try {
            if (!Objects.equals(version.get(), expectVersion)) {
            /*//LOGGER.info("Version skip " + version.get() + " -> " + expectVersion);
            return;*/
                return cacheInit;
            } else {
                long start = System.currentTimeMillis();
                ExecutorService executor = ThreadUtils.newFixedThreadPool(10);
                try {
                    cacheInit = getCacheInit(executor);
                    //清除模版的缓存数据
                    WebSite.clearTemplateConfigMap();
                    Constants.zrLogConfig.refreshPluginCacheData(expectVersion);
                } finally {
                    executor.shutdown();
                    LOGGER.info("RefreshInitDataCache used time " + (System.currentTimeMillis() - start) + "ms");
                }
            }
        } finally {
            lock.unlock();
        }
        return cacheInit;
    }

    @Override
    public List<Map<String, Object>> getArticleTypes(HttpRequest request) {
        if (Objects.nonNull(cacheInit)) {
            return cacheInit.getTypes();
        }
        try {
            refreshInitDataCacheAsync(request, false);
            return new Type().findAll();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Map<String, Object>> getTags(HttpRequest request) {
        if (Objects.nonNull(cacheInit)) {
            return cacheInit.getTags();
        }
        try {
            refreshInitDataCacheAsync(request, false);
            return new Tag().findAll();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Object getPublicWebSiteInfoFirstByCache(String key) {
        if (!Constants.zrLogConfig.isInstalled()) {
            return null;
        }
        if (Objects.nonNull(cacheInit)) {
            return cacheInit.getWebSite().get(key);
        }
        return new WebSite().getPublicStringValueByName(key);
    }

    private void handleRobotsTxt(Map<String, Object> website) {
        String robotTxt = (String) website.get("robotRuleContent");

        if (StringUtils.isEmpty(robotTxt)) {
            return;
        }
        File robotFile = PathUtil.getStaticFile("robots.txt");
        if (!robotFile.getParentFile().exists()) {
            robotFile.getParentFile().mkdirs();
        }
        IOUtil.writeStrToFile(robotTxt, robotFile);
        if (Constants.websiteValueIsTrue(website.get("generator_html_status"))) {
            try {
                StaticSitePlugin staticSitePlugin = Constants.zrLogConfig.getPlugin(StaticSitePlugin.class);
                if (Objects.nonNull(staticSitePlugin)) {
                    staticSitePlugin.saveToCacheFolder(new FileInputStream(robotFile), "/" + robotFile.getName());
                }
            } catch (FileNotFoundException e) {
                LOGGER.warning("save to Cache error " + e.getMessage());
            }
        }
    }

    @Override
    public Map<String, Object> refreshWebSite() {
        if (!Constants.zrLogConfig.isInstalled()) {
            return new HashMap<>();
        }
        Map<String, Object> website = new WebSite().getPublicWebSite();
        if (Objects.nonNull(cacheInit)) {
            cacheInit.getWebSite().clear();
            cacheInit.getWebSite().putAll(website);
        }
        handleRobotsTxt(website);
        return website;
    }


    private HotLogBasicInfoVO convertToBasicVO(Map<String, Object> log) {
        String format = "yyyy-MM-dd";
        log.put("releaseTime", ResultValueConvertUtils.formatDate(log.get("releaseTime"), format));
        log.put("lastUpdateDate", ResultValueConvertUtils.formatDate(log.get("last_update_date"), format));
        log.put("last_update_date", ResultValueConvertUtils.formatDate(log.get("last_update_date"), format));
        return BeanUtil.convert(log, HotLogBasicInfoVO.class);
    }

    private void faviconHandle(String faviconIconBase64, String saveFilePath, Boolean saveToCache) {
        StaticSitePlugin staticSitePlugin = Constants.zrLogConfig.getPlugin(StaticSitePlugin.class);
        if (StringUtils.isEmpty(faviconIconBase64)) {

            if (staticSitePlugin != null) {
                staticSitePlugin.copyResourceToCacheFolder(saveFilePath);
            }
            return;
        }
        try {
            File file = PathUtil.getStaticFile(saveFilePath);
            file.getParentFile().mkdirs();
            byte[] binBytes;
            if (faviconIconBase64.contains(",")) {
                binBytes = Base64.getDecoder().decode(faviconIconBase64.split(",")[1]);
            } else {
                binBytes = Base64.getDecoder().decode(faviconIconBase64);
            }
            IOUtil.writeBytesToFile(binBytes, file);
            if (Objects.equals(saveToCache, true)) {
                if (Objects.nonNull(staticSitePlugin)) {
                    staticSitePlugin.saveToCacheFolder(new ByteArrayInputStream(binBytes), saveFilePath);
                }
            }
        } catch (Exception e) {
            LOGGER.warning("Save favicon error " + e.getMessage());
        }
    }

    private BaseDataInitVO getCacheInit(Executor executor) {
        BaseDataInitVO cacheInit = new BaseDataInitVO();
        //first set website info
        Map<String, Object> refreshWebSite = refreshWebSite();
        cacheInit.setWebSite(refreshWebSite);
        cacheInit.setWebSiteVersion((long) new Gson().toJson(refreshWebSite).hashCode());
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        futures.add(CompletableFuture.runAsync(() -> {
            //缓存静态资源map
            Map<String, String> tempMap = getCacheFileMap();
            cacheFileMap.clear();
            //重新填充Map
            cacheFileMap.putAll(tempMap);
        }, executor));
        BaseDataInitVO.Statistics statistics = new BaseDataInitVO.Statistics();
        futures.add(CompletableFuture.runAsync(() -> {
            statistics.setTotalArticleSize(new Log().getVisitorCount());
            cacheInit.setStatistics(statistics);
        }, executor));
        futures.add(CompletableFuture.runAsync(() -> {
            try {
                cacheInit.setLinks(new Link().findAll());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }, executor));
        futures.add(CompletableFuture.runAsync(() -> {
            try {
                cacheInit.setTypes(new Type().findAll());
                statistics.setTotalTypeSize((long) cacheInit.getTypes().size());
                List<Map<String, Object>> types = cacheInit.getTypes();
                Map<Map<String, Object>, List<HotLogBasicInfoVO>> indexHotLog = new LinkedHashMap<>();
                cacheInit.setIndexHotLogs(indexHotLog);
                //设置分类Hot
                for (Map<String, Object> type : types) {
                    futures.add(CompletableFuture.runAsync(() -> {
                        Map<String, Object> typeMap = new TreeMap<>();
                        typeMap.put("typeName", type.get("typeName"));
                        String alias = (String) type.get("alias");
                        typeMap.put("alias", alias);
                        indexHotLog.put(typeMap, new Log().findByTypeAlias(1, 6, alias).getRows().stream().map(this::convertToBasicVO).collect(Collectors.toList()));
                    }, executor));
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }, executor));
        futures.add(CompletableFuture.runAsync(() -> {
            //Last article
            cacheInit.setHotLogs(new Log().visitorFind(new PageRequestImpl(1L, 6L), "").getRows().stream().map(this::convertToBasicVO).collect(Collectors.toList()));
        }, executor));
        futures.add(CompletableFuture.runAsync(() -> {
            try {
                cacheInit.setLogNavs(new LogNav().findAll());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }, executor));
        futures.add(CompletableFuture.runAsync(() -> {
            try {
                cacheInit.setPlugins(new Plugin().findAll());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }, executor));
        futures.add(CompletableFuture.runAsync(() -> {
            try {
                cacheInit.setArchives(new Log().getArchives());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }, executor));
        futures.add(CompletableFuture.runAsync(this::refreshFavicon, executor));
        futures.add(CompletableFuture.runAsync(() -> {
            try {
                cacheInit.setTags(new Tag().refreshTag());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            statistics.setTotalTagSize((long) cacheInit.getTags().size());
        }, executor));
        try {
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        } catch (Exception e) {
            LOGGER.warning("Load data error " + e.getMessage());
        }
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
