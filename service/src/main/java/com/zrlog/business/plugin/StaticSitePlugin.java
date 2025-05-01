package com.zrlog.business.plugin;

import com.hibegin.common.util.IOUtil;
import com.hibegin.common.util.LoggerUtil;
import com.hibegin.common.util.SecurityUtils;
import com.hibegin.common.util.StringUtils;
import com.hibegin.common.util.http.HttpUtil;
import com.hibegin.http.HttpMethod;
import com.hibegin.http.server.ApplicationContext;
import com.hibegin.http.server.api.HttpRequest;
import com.hibegin.http.server.config.AbstractServerConfig;
import com.hibegin.http.server.config.RequestConfig;
import com.hibegin.http.server.config.ResponseConfig;
import com.hibegin.http.server.handler.HttpRequestHandlerRunnable;
import com.hibegin.http.server.impl.SimpleHttpResponse;
import com.hibegin.http.server.util.HttpRequestBuilder;
import com.hibegin.http.server.util.PathUtil;
import com.zrlog.common.Constants;
import com.zrlog.data.cache.CacheServiceImpl;
import com.zrlog.model.WebSite;
import com.zrlog.plugin.IPlugin;
import com.zrlog.util.I18nUtil;
import com.zrlog.util.ZrLogUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.locks.Lock;
import java.util.logging.Level;
import java.util.logging.Logger;

public interface StaticSitePlugin extends IPlugin {

    String STATIC_USER_AGENT = "Static-Blog-Plugin/" + UUID.randomUUID().toString().replace("-", "");
    String STATIC_SITE_PLUGIN_HTML_FILE_KEY = "_htmlFile";


    static String getSuffix(HttpRequest request) {
        if (isStaticPluginRequest(request) || Constants.isStaticHtmlStatus()) {
            return ".html";
        }
        return "";
    }

    Logger LOGGER = LoggerUtil.getLogger(StaticSitePlugin.class);

    default void copyResourceToCacheFolder(String resourceName) {
        InputStream inputStream = CacheServiceImpl.class.getResourceAsStream(resourceName);
        if (Objects.isNull(inputStream)) {
            LOGGER.warning("Missing resource " + resourceName);
            return;
        }
        saveToCacheFolder(inputStream, resourceName);
    }

    static boolean isDisabled() {
        if (!Constants.isStaticHtmlStatus()) {
            return true;
        }
        return StringUtils.isEmpty(ZrLogUtil.getBlogHostByWebSite());
    }

    /**
     * 将一个网页转化对应文件，用于静态化文章页
     */
    default File saveResponseBodyToHtml(HttpRequest httpRequest, String copy) {
        if (copy == null) {
            return null;
        }
        byte[] bytes = copy.getBytes(StandardCharsets.UTF_8);
        File htmlFile = loadCacheFile(httpRequest);
        if (!htmlFile.exists()) {
            htmlFile.getParentFile().mkdirs();
        }
        if (httpRequest.getUri().startsWith("/admin") && !httpRequest.getUri().contains(".")) {
            htmlFile = new File(htmlFile + ".html");
        }
        IOUtil.writeBytesToFile(bytes, htmlFile);
        return htmlFile;
    }

    static boolean isStaticPluginRequest(HttpRequest request) {
        if (Objects.isNull(request)) {
            return false;
        }
        String ua = request.getHeader("User-Agent");
        if (Objects.isNull(ua)) {
            return false;
        }
        return Objects.equals(ua, STATIC_USER_AGENT);
    }

    default String getSiteVersion() {
        StringBuilder sb = new StringBuilder();
        for (File file : new TreeSet<>(getCacheFiles())) {
            try {
                sb.append(SecurityUtils.md5(new FileInputStream(file)));
            } catch (FileNotFoundException e) {
                LOGGER.warning("getCacheHtmlFolderVersion error " + e.getMessage());
            }
        }
        return Math.abs(sb.toString().hashCode()) + "";
    }

    String getVersionFileName();

    String getDbCacheKey();

    default boolean isSynchronized(HttpRequest request) {
        try {
            if (isDisabled()) {
                return true;
            }
            String versionFile = (Objects.nonNull(request) ? request.getScheme() : "https") + "://" + Constants.getHost() + "/" + getVersionFileName();
            String remoteSiteVersion = HttpUtil.getInstance().getSuccessTextByUrl(versionFile);
            return Objects.equals(getSiteVersion(), remoteSiteVersion);
        } catch (IOException | InterruptedException | URISyntaxException e) {
            LOGGER.info(e.getMessage());
        }
        return false;
    }

    default File loadCacheFile(HttpRequest request) {
        String lang = I18nUtil.getAcceptLocal(request);
        String cacheKey = request.getUri();
        if (Objects.equals(cacheKey, "/")) {
            cacheKey = "/index.html";
        }
        File file = getCacheFile(lang, cacheKey);
        if (file.exists()) {
            return file;
        }
        return getCacheFile(getDefaultLang(), cacheKey);
    }

    String getContextPath();

    String getDefaultLang();

    private File getCacheFile(String lang, String uri) {
        StringJoiner fileSj = new StringJoiner("/");
        fileSj.add(lang);
        fileSj.add(getContextPath());
        fileSj.add(uri);
        return PathUtil.getCacheFile(fileSj.toString());
    }

    default File getCacheFile(String uri) {
        return getCacheFile(getDefaultLang(), uri);
    }

    default void saveToCacheFolder(InputStream inputStream, String uri) {
        if (!Constants.isStaticHtmlStatus()) {
            return;
        }
        File file = getCacheFile(uri);
        if (!file.exists()) {
            file.getParentFile().mkdirs();
        }
        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            inputStream.transferTo(outputStream);
            inputStream.close();
            getCacheFiles().add(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    Map<String, HandleState> getHandleStatusPageMap();

    Lock getParseLock();

    private HttpRequest buildMockRequest(HttpMethod method, String uri, RequestConfig requestConfig, ApplicationContext applicationContext) throws Exception {
        return HttpRequestBuilder.buildRequest(method, uri, ZrLogUtil.getBlogHostByWebSite(), STATIC_USER_AGENT, requestConfig, applicationContext);
    }

    private void doParse(File file) throws IOException {
        if (file.getName().endsWith(".js") || file.getName().endsWith(".json")) {
            return;
        }
        getParseLock().lock();
        try {
            Document document = Jsoup.parse(file);
            Elements links = document.select("a");
            links.forEach(element -> {
                String href = element.attr("href");
                if (href.startsWith("//")) {
                    return;
                }
                //exists jobs
                if (getHandleStatusPageMap().containsKey(href)) {
                    return;
                }
                if (href.startsWith("/") && href.endsWith(".html")) {
                    getHandleStatusPageMap().put(href, HandleState.NEW);
                }
            });
        } finally {
            getParseLock().unlock();
        }
    }

    default String notFindFile() {
        return getContextPath() + "/error/404.html";
    }

    private CompletableFuture<Void> doAsyncFetch(String key, AbstractServerConfig serverConfig, ApplicationContext applicationContext, Executor executor) {
        getHandleStatusPageMap().put(key, HandleState.HANDING);
        return CompletableFuture.runAsync(() -> {
            boolean error = false;
            ResponseConfig responseConfig = serverConfig.getResponseConfig();
            responseConfig.setEnableGzip(false);
            HttpRequest httpRequest;
            try {
                httpRequest = buildMockRequest(HttpMethod.GET, key, serverConfig.getRequestConfig(), applicationContext);
            } catch (Exception e) {
                LOGGER.warning("Generator " + key + " error: " + e.getMessage());
                return;
            }
            String uri = httpRequest.getUri();
            try {
                new HttpRequestHandlerRunnable(httpRequest, new SimpleHttpResponse(httpRequest, serverConfig.getResponseConfig())).run();
                File file = (File) httpRequest.getAttr().get(STATIC_SITE_PLUGIN_HTML_FILE_KEY);
                if (Objects.nonNull(file)) {
                    getCacheFiles().add(file);
                }
                if (httpRequest.getUri().startsWith(Constants.ADMIN_URI_BASE_PATH + "/")) {
                    return;
                }
                if (Objects.equals(uri, notFindFile())) {
                    return;
                }
                if (Objects.isNull(file) || !file.exists()) {
                    LOGGER.warning("Generator " + uri + " error: missing static file");
                    error = true;
                    return;
                }
                doParse(file);
            } catch (Exception ex) {
                LOGGER.warning("Generator " + uri + " error: " + ex.getMessage());
            } finally {
                if (error) {
                    //如果抓取错误了，需要暂停一下，避免重新获取过快
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException ex) {
                        LOGGER.warning("Generator " + uri + " error: " + ex.getMessage());
                    }
                }
                getHandleStatusPageMap().put(key, error ? HandleState.RE_FETCH : HandleState.HANDLED);
            }
        }, executor);
    }

    Executor getExecutorService();

    List<File> getCacheFiles();

    default void doFetch(AbstractServerConfig serverConfig, ApplicationContext applicationContext) {
        long start = System.currentTimeMillis();

        try {
            while (getHandleStatusPageMap().values().stream().anyMatch(e -> e == StaticSitePlugin.HandleState.NEW)) {
                CompletableFuture.allOf(getHandleStatusPageMap().entrySet().stream().filter(e -> Arrays.asList(HandleState.NEW, HandleState.RE_FETCH).contains(e.getValue())).map(e -> {
                    return doAsyncFetch(e.getKey(), serverConfig, applicationContext, getExecutorService());
                }).toArray(CompletableFuture[]::new)).join();
            }
            String version = saveCacheVersion();
            long usedTime = (System.currentTimeMillis() - start);
            if (Constants.debugLoggerPrintAble()) {
                LOGGER.info("Generator [" + version + "] " + "size " + getHandleStatusPageMap().size() + " finished in " + usedTime + "ms");
            } else if (usedTime > Duration.ofSeconds(10).toMillis()) {
                LOGGER.warning("Generator [" + version + "] slow size " + getHandleStatusPageMap().size() + " finished in " + usedTime + "ms");
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "do fetch error", e.getMessage());
        }
    }

    default boolean waitCacheSync(HttpRequest request) {
        for (int i = 0; i < 360; i++) {
            if (isSynchronized(request)) {
                try {
                    new WebSite().updateByKV(getDbCacheKey(), getSiteVersion());
                } catch (SQLException e) {
                    LOGGER.log(Level.SEVERE, "update site version cache error", e);
                }
                return true;
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        return false;
    }

    private String saveCacheVersion() {
        String siteVersion = getSiteVersion();
        IOUtil.writeStrToFile(siteVersion, getCacheFile(getVersionFileName()));
        return siteVersion;
    }

    enum HandleState {
        NEW, HANDING, RE_FETCH, HANDLED
    }
}
