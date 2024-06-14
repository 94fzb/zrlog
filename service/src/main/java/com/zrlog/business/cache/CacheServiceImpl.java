package com.zrlog.business.cache;

import com.hibegin.common.BaseLockObject;
import com.hibegin.common.util.*;
import com.hibegin.http.server.api.HttpRequest;
import com.hibegin.http.server.util.PathUtil;
import com.zrlog.business.cache.vo.BaseDataInitVO;
import com.zrlog.business.cache.vo.HotLogBasicInfoVO;
import com.zrlog.business.plugin.StaticHtmlPlugin;
import com.zrlog.business.plugin.TemplateDownloadPlugin;
import com.zrlog.common.CacheService;
import com.zrlog.common.Constants;
import com.zrlog.common.rest.request.PageRequestImpl;
import com.zrlog.data.dto.FaviconBase64DTO;
import com.zrlog.model.*;
import com.zrlog.plugin.IPlugin;
import com.zrlog.util.I18nUtil;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;

/**
 * 对缓存数据的操作
 */
public class CacheServiceImpl extends BaseLockObject implements CacheService {
    private static final Logger LOGGER = LoggerUtil.getLogger(CacheServiceImpl.class);
    private final String CACHE_HTML_PATH = PathUtil.getCachePath();

    private final AtomicLong version = new AtomicLong();
    private final Map<String, String> cacheFileMap = new ConcurrentHashMap<>();
    private BaseDataInitVO cacheInit;

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

    private static String getStreamTag(InputStream inputStream) {
        return Math.abs(SecurityUtils.md5(inputStream).hashCode()) + "";
    }

    @Override
    public void refreshFavicon() {
        FaviconBase64DTO faviconBase64DTO = new WebSite().faviconBase64DTO();
        faviconHandle(faviconBase64DTO.getFavicon_ico_base64(), Constants.FAVICON_ICO_URI_PATH);
        faviconHandle(faviconBase64DTO.getFavicon_png_pwa_192_base64(), Constants.FAVICON_PNG_PWA_192_URI_PATH);
        faviconHandle(faviconBase64DTO.getFavicon_png_pwa_512_base64(), Constants.FAVICON_PNG_PWA_512_URI_PATH);
    }

    @Override
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
    @Override
    public File saveResponseBodyToHtml(HttpRequest httpRequest, String copy) {
        if (copy == null) {
            return null;
        }
        byte[] bytes = copy.getBytes(StandardCharsets.UTF_8);
        FileUtils.tryResizeDiskSpace(CACHE_HTML_PATH, bytes.length, Constants.getMaxCacheHtmlSize());
        File htmlFile = loadCacheFile(httpRequest);
        if (!htmlFile.exists()) {
            htmlFile.getParentFile().mkdirs();
        }
        IOUtil.writeBytesToFile(bytes, htmlFile);
        return htmlFile;
    }

    @Override
    public void saveToCacheFolder(InputStream inputStream, String uri) {
        File file = new File(CACHE_HTML_PATH + "/zh_CN/" + uri);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
        }
        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            inputStream.transferTo(outputStream);
            inputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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

    @Override
    public CompletableFuture<Void> refreshInitDataCacheAsync(HttpRequest servletRequest, boolean cleanAble) {
        long expectVersion = version.incrementAndGet();
        return CompletableFuture.runAsync(() -> {
            refreshInitDataCache(servletRequest, cleanAble, expectVersion);
        });
    }

    private void refreshWebSite(Map<String, Object> newWebSite) {
        for (String key : Constants.WEB_SITE.keySet()) {
            if (!newWebSite.containsKey(key)) {
                Constants.WEB_SITE.remove(key);
            }
        }
        newWebSite.forEach((key, value) -> {
            if (Objects.nonNull(value)) {
                Constants.WEB_SITE.put(key, value);
            }
        });

    }

    private void refreshInitDataCache(HttpRequest servletRequest, boolean cleanAble, long expectVersion) {
        if (cleanAble || cacheInit == null) {
            lock.lock();
            if (!Objects.equals(version.get(), expectVersion)) {
                return;
            }
            long start = System.currentTimeMillis();
            try {
                cacheInit = getCacheInit();
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
            } finally {
                lock.unlock();
                LOGGER.info("RefreshInitDataCache used time " + (System.currentTimeMillis() - start) + "ms");
            }
        }
        if (Objects.isNull(servletRequest)) {
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
    public Map<String, Object> refreshWebSite() {
        Map<String, Object> website = new WebSite().getWebSite();
        refreshWebSite(website);
        String robotTxt = (String) Constants.WEB_SITE.get("robotRuleContent");
        if (StringUtils.isNotEmpty(robotTxt)) {
            File robotFile = PathUtil.getStaticFile("robots.txt");
            if (!robotFile.getParentFile().exists()) {
                robotFile.getParentFile().mkdirs();
            }
            IOUtil.writeStrToFile(robotTxt, robotFile);
        }
        return Constants.WEB_SITE;
    }

    private HotLogBasicInfoVO convertToBasicVO(Map<String, Object> log) {
        log.put("releaseTime", ((LocalDateTime) log.get("releaseTime")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        log.put("last_update_date", ((LocalDateTime) log.get("last_update_date")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        return BeanUtil.convert(log, HotLogBasicInfoVO.class);
    }

    private void faviconHandle(String faviconIconBase64, String saveFilePath) {
        if (StringUtils.isEmpty(faviconIconBase64)) {
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
        } catch (Exception e) {
            LOGGER.warning("Save favicon error " + e.getMessage());
        }
    }

    private BaseDataInitVO getCacheInit() {
        BaseDataInitVO cacheInit = new BaseDataInitVO();
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        try (ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor()) {
            BaseDataInitVO.Statistics statistics = new BaseDataInitVO.Statistics();
            futures.add(CompletableFuture.runAsync(() -> {
                statistics.setTotalArticleSize(new Log().getVisitorCount());
                cacheInit.setStatistics(statistics);
            }, executorService));
            futures.add(CompletableFuture.runAsync(() -> {
                cacheInit.setWebSite(refreshWebSite());
            }, executorService));
            futures.add(CompletableFuture.runAsync(() -> {
                try {
                    cacheInit.setLinks(new Link().findAll());
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }, executorService));
            futures.add(CompletableFuture.runAsync(() -> {
                try {
                    cacheInit.setTypes(new Type().findAll());
                    statistics.setTotalTypeSize((long) cacheInit.getTypes().size());
                    List<Map<String, Object>> types = cacheInit.getTypes();
                    //Last article
                    cacheInit.setHotLogs(new Log().visitorFind(new PageRequestImpl(1L, 6L), "").getRows().stream().map(this::convertToBasicVO).toList());
                    Map<Map<String, Object>, List<HotLogBasicInfoVO>> indexHotLog = new LinkedHashMap<>();
                    cacheInit.setIndexHotLogs(indexHotLog);
                    //设置分类Hot
                    for (Map<String, Object> type : types) {
                        futures.add(CompletableFuture.runAsync(() -> {
                            Map<String, Object> typeMap = new TreeMap<>();
                            typeMap.put("typeName", type.get("typeName"));
                            String alias = (String) type.get("alias");
                            typeMap.put("alias", alias);
                            indexHotLog.put(typeMap, new Log().findByTypeAlias(1, 6, alias).getRows().stream().map(this::convertToBasicVO).toList());
                        }, executorService));
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }, executorService));
            futures.add(CompletableFuture.runAsync(() -> {
                try {
                    cacheInit.setLogNavs(new LogNav().findAll());
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }, executorService));
            futures.add(CompletableFuture.runAsync(() -> {
                try {
                    cacheInit.setPlugins(new Plugin().findAll());
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }, executorService));
            futures.add(CompletableFuture.runAsync(() -> {
                try {
                    cacheInit.setArchives(new Log().getArchives());
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }, executorService));
            futures.add(CompletableFuture.runAsync(() -> refreshFavicon(), executorService));
            futures.add(CompletableFuture.runAsync(() -> {
                List<Map<String, Object>> all;
                try {
                    new Tag().refreshTag();
                    all = new Tag().findAll();
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                for (Map<String, Object> kv : all) {
                    kv.put("keycode", kv.get("text").hashCode());
                }
                cacheInit.setTags(all);
                statistics.setTotalTagSize((long) cacheInit.getTags().size());
            }, executorService));
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

}
