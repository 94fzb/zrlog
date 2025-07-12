package com.zrlog.blog.web.plugin;

import com.hibegin.common.BaseLockObject;
import com.hibegin.common.util.*;
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
import com.zrlog.blog.web.interceptor.TemplateUtils;
import com.zrlog.business.cache.CacheServiceImpl;
import com.zrlog.business.plugin.StaticSitePlugin;
import com.zrlog.business.service.TemplateInfoHelper;
import com.zrlog.common.AdminResource;
import com.zrlog.common.CacheService;
import com.zrlog.common.Constants;
import com.zrlog.common.vo.TemplateVO;
import com.zrlog.util.I18nUtil;
import com.zrlog.util.ThreadUtils;
import com.zrlog.util.ZrLogUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

public class StaticSitePluginImpl extends BaseLockObject implements StaticSitePlugin {

    private final String staticUserAgent;
    private static final Logger LOGGER = LoggerUtil.getLogger(StaticSitePluginImpl.class);
    private final AbstractServerConfig serverConfig;
    private final ApplicationContext applicationContext;
    private final Map<String, HandleState> handleStatusPageMap = new ConcurrentHashMap<>();
    private final ReentrantLock parseLock = new ReentrantLock();
    private final String notFindFile;
    private final String contextPath;
    private long version;
    private String siteVersion;
    private final String cacheStaticPath;
    private final PageService pageService;


    public StaticSitePluginImpl(AbstractServerConfig abstractServerConfig, String contextPath) {
        this.applicationContext = new ApplicationContext(abstractServerConfig.getServerConfig());
        this.applicationContext.init();
        this.serverConfig = abstractServerConfig;
        this.contextPath = contextPath;
        this.notFindFile = contextPath + "/error/404.html";
        this.staticUserAgent = "Static-Blog-Plugin/" + UUID.randomUUID().toString().replace("-", "");
        this.cacheStaticPath = PathUtil.getCachePath();
        this.pageService = new PageService(this);
    }

    @Override
    public void copyResourceToCacheFolder(String resourceName) {
        InputStream inputStream = CacheServiceImpl.class.getResourceAsStream(resourceName);
        if (Objects.isNull(inputStream)) {
            LOGGER.warning("Missing resource " + resourceName);
            return;
        }
        AdminResource adminResource = Constants.zrLogConfig.getAdminResource();
        if (Objects.isNull(adminResource)) {
            return;
        }
        if (adminResource.isAdminMainJs(resourceName)) {
            String stringInputStream = IOUtil.getStringInputStream(inputStream);
            String adminPath = "admin/";
            String newStr = stringInputStream.replace("\"/admin/\"", "document.currentScript.baseURI + \"" + adminPath + "\"");
            saveToCacheFolder(new ByteArrayInputStream(newStr.getBytes()), resourceName);
            return;
        }
        saveToCacheFolder(inputStream, resourceName);
    }

    @Override
    public void saveToCacheFolder(InputStream inputStream, String uri) {
        if (!Constants.isStaticHtmlStatus()) {
            return;
        }
        File file = new File(getCacheHtmlFolder() + "/" + uri);
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


    @Override
    public File loadCacheFile(HttpRequest request) {
        String lang = I18nUtil.getAcceptLocal(request);
        String cacheKey = request.getUri();
        if (Objects.equals(cacheKey, "/")) {
            cacheKey = "/index.html";
        }
        return new File(cacheStaticPath + lang + contextPath + "/" + cacheKey);
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

    @Override
    public File getCacheHtmlFolder() {
        return new File(cacheStaticPath + "/zh_CN/" + contextPath + "/");
    }

    @Override
    public String saveCacheHtmlFolderVersion() {
        List<File> files = new ArrayList<>();
        FileUtils.getAllFiles(getCacheHtmlFolder().toString(), files);
        StringBuilder sb = new StringBuilder();
        File versionFile = new File(getCacheHtmlFolder() + "/" + versionFileName);
        for (File file : files) {
            try {
                if (Objects.equals(file.toString(), versionFile.toString())) {
                    continue;
                }
                sb.append(SecurityUtils.md5(new FileInputStream(file)));
            } catch (FileNotFoundException e) {
                LOGGER.warning("getCacheHtmlFolderVersion error " + e.getMessage());
            }
        }
        String version = SecurityUtils.md5(sb.toString());
        IOUtil.writeStrToFile(version, new File(getCacheHtmlFolder() + "/" + versionFileName));
        return version;
    }

    private void copyCommonAssert() {
        //admin resource
        CacheService<?> cacheService = Constants.zrLogConfig.getCacheService();
        Constants.zrLogConfig.getAdminResource().getAdminStaticResourceUris().forEach(this::copyResourceToCacheFolder);
        //video.js
        copyResourceToCacheFolder("/assets/css/font/vjs.eot");
        copyResourceToCacheFolder("/assets/css/font/vjs.svg");
        copyResourceToCacheFolder("/assets/css/font/vjs.ttf");
        copyResourceToCacheFolder("/assets/css/font/vjs.woff");
        copyResourceToCacheFolder("/assets/css/video-js.css");
        copyResourceToCacheFolder("/assets/css/katex.min.css");
        copyResourceToCacheFolder("/assets/js/video.js");
        copyResourceToCacheFolder("/assets/css/hljs/dark.css");
        copyResourceToCacheFolder("/assets/css/hljs/light.css");
        //default avatar url
        copyResourceToCacheFolder("/assets/images/default-portrait.gif");
        File faviconFile = new File(PathUtil.getStaticPath() + "/favicon.ico");
        if (faviconFile.exists()) {
            try {
                saveToCacheFolder(new FileInputStream(faviconFile), "/" + faviconFile.getName());
            } catch (FileNotFoundException e) {
                LOGGER.warning("Missing resource " + faviconFile);
            }
        } else {
            //favicon
            copyResourceToCacheFolder("/favicon.ico");
        }
    }


    private void copyDefaultTemplateAssets() {
        String templatePath = TemplateUtils.getTemplatePath(null);
        if (!Objects.equals(templatePath, Constants.DEFAULT_TEMPLATE_PATH)) {
            return;
        }
        TemplateVO templateVO = TemplateInfoHelper.getDefaultTemplateVO();
        templateVO.getStaticResources().forEach(e -> {
            String resourceUri = Constants.DEFAULT_TEMPLATE_PATH + "/" + e;
            copyResourceToCacheFolder(resourceUri);
        });
    }

    private void doParse(File file) throws IOException {
        parseLock.lock();
        try {
            Document document = Jsoup.parse(file);
            Elements links = document.select("a");
            links.forEach(element -> {
                String href = element.attr("href");
                if (href.startsWith("//")) {
                    return;
                }
                //exists jobs
                if (handleStatusPageMap.containsKey(href)) {
                    return;
                }
                if (href.startsWith("/") && href.endsWith(".html")) {
                    handleStatusPageMap.put(href, HandleState.NEW);
                }
            });
        } finally {
            parseLock.unlock();
        }
    }

    public boolean isStaticPlugin(HttpRequest request) {
        if (Objects.isNull(request)) {
            return false;
        }
        String ua = request.getHeader("User-Agent");
        if (Objects.isNull(ua)) {
            return false;
        }
        return Objects.equals(ua, staticUserAgent);
    }

    @Override
    public void setVersion(long version) {
        this.version = version;
    }

    @Override
    public boolean isSynchronized() {
        try {
            String host = Constants.getHost();
            if (StringUtils.isEmpty(host)) {
                return true;
            }
            String remoteSiteVersion = HttpUtil.getInstance().getSuccessTextByUrl("https://" + Constants.getHost() + "/" + versionFileName);
            return Objects.equals(siteVersion, remoteSiteVersion);
        } catch (IOException | InterruptedException | URISyntaxException e) {
            LOGGER.info(e.getMessage());
        }
        return false;
    }

    private HttpRequest buildMockRequest(HttpMethod method, String uri, RequestConfig requestConfig, ApplicationContext applicationContext) throws Exception {
        return HttpRequestBuilder.buildRequest(method, uri, ZrLogUtil.getBlogHostByWebSite(), staticUserAgent, requestConfig, applicationContext);
    }

    private CompletableFuture<Void> doAsyncFetch(String key, Executor executor) {
        handleStatusPageMap.put(key, HandleState.HANDING);
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
                File file = (File) httpRequest.getAttr().get(Constants.STATIC_SITE_PLUGIN_HTML_FILE_KEY);
                if (Objects.equals(uri, Constants.ADMIN_PWA_MANIFEST_JSON)) {
                    return;
                }
                if (Objects.equals(uri, Constants.ADMIN_SERVICE_WORKER_JS)) {
                    return;
                }
                if (Objects.equals(uri, notFindFile)) {
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
                handleStatusPageMap.put(key, error ? HandleState.RE_FETCH : HandleState.HANDLED);
            }
        }, executor);
    }

    private void doFetch() {
        ExecutorService executorService = ThreadUtils.newFixedThreadPool(20);
        try {
            CompletableFuture.allOf(handleStatusPageMap.entrySet().stream().filter(e -> Arrays.asList(HandleState.NEW, HandleState.RE_FETCH).contains(e.getValue())).map(e -> {
                return doAsyncFetch(e.getKey(), executorService);
            }).toArray(CompletableFuture[]::new)).join();
        } finally {
            executorService.shutdown();
        }
    }

    private void doGenerator() {
        if (!Constants.isStaticHtmlStatus()) {
            return;
        }
        if (StringUtils.isEmpty(ZrLogUtil.getBlogHostByWebSite())) {
            return;
        }
        File cacheFolder = getCacheHtmlFolder();
        if (cacheFolder.exists()) {
            FileUtils.deleteFile(cacheFolder.toString());
        }
        Constants.zrLogConfig.getCacheService().refreshFavicon();
        copyCommonAssert();
        copyDefaultTemplateAssets();
        handleStatusPageMap.clear();
        //从首页开始查找
        handleStatusPageMap.put(contextPath + "/", HandleState.NEW);
        handleStatusPageMap.put(contextPath + Constants.ADMIN_PWA_MANIFEST_JSON, HandleState.NEW);
        handleStatusPageMap.put(contextPath + Constants.ADMIN_SERVICE_WORKER_JS, HandleState.NEW);
        Constants.zrLogConfig.getAdminResource().getAdminPageUris().forEach(uri -> {
            handleStatusPageMap.put(contextPath + uri, HandleState.NEW);
        });
        //生成 404 页面，用于配置第三方 cdn，或者云存储的错误页面
        handleStatusPageMap.put(notFindFile, HandleState.NEW);
        pageService.saveRedirectRules(notFindFile);
        lock.lock();
        long start = System.currentTimeMillis();
        try {
            while (handleStatusPageMap.values().stream().anyMatch(e -> e == HandleState.NEW)) {
                doFetch();
            }
        } finally {
            lock.unlock();
            long usedTime = (System.currentTimeMillis() - start);
            if (Constants.debugLoggerPrintAble()) {
                LOGGER.info("Generator [" + version + "] " + "size " + handleStatusPageMap.size() + " finished in " + usedTime + "ms");
            } else if (usedTime > Duration.ofSeconds(10).toMillis()) {
                LOGGER.warning("Generator [" + version + "] slow size " + handleStatusPageMap.size() + " finish in " + usedTime + "ms");
            }
            this.siteVersion = saveCacheHtmlFolderVersion();
        }
    }

    @Override
    public boolean start() {
        doGenerator();
        return true;
    }

    @Override
    public boolean autoStart() {
        return false;
    }

    @Override
    public boolean isStarted() {
        return false;
    }

    @Override
    public boolean stop() {
        return true;
    }

    enum HandleState {
        NEW, HANDING, RE_FETCH, HANDLED
    }
}
