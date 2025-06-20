package com.zrlog.business.plugin;

import com.hibegin.common.BaseLockObject;
import com.hibegin.common.util.FileUtils;
import com.hibegin.common.util.IOUtil;
import com.hibegin.common.util.LoggerUtil;
import com.hibegin.common.util.StringUtils;
import com.hibegin.http.HttpMethod;
import com.hibegin.http.server.ApplicationContext;
import com.hibegin.http.server.api.HttpRequest;
import com.hibegin.http.server.config.AbstractServerConfig;
import com.hibegin.http.server.config.ResponseConfig;
import com.hibegin.http.server.handler.HttpRequestHandlerRunnable;
import com.hibegin.http.server.impl.SimpleHttpResponse;
import com.hibegin.http.server.util.PathUtil;
import com.zrlog.blog.web.util.WebTools;
import com.zrlog.business.service.TemplateHelper;
import com.zrlog.business.service.TemplateInfoHelper;
import com.zrlog.business.util.PageServiceUtil;
import com.zrlog.common.Constants;
import com.zrlog.common.vo.TemplateVO;
import com.zrlog.plugin.IPlugin;
import com.zrlog.util.ZrLogUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.*;
import java.time.Duration;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

public class StaticSitePlugin extends BaseLockObject implements IPlugin {
    public static final String HTML_FILE_KEY = "_htmlFile";


    private static final Logger LOGGER = LoggerUtil.getLogger(StaticSitePlugin.class);
    private final AbstractServerConfig serverConfig;
    private final ApplicationContext applicationContext;
    private final Map<String, HandleState> handleStatusPageMap = new ConcurrentHashMap<>();
    private final ReentrantLock parseLock = new ReentrantLock();

    public StaticSitePlugin(AbstractServerConfig abstractServerConfig) {
        this.applicationContext = new ApplicationContext(abstractServerConfig.getServerConfig());
        this.applicationContext.init();
        this.serverConfig = abstractServerConfig;
    }

    private static void copyCommonAssert() {
        //admin resource
        Constants.zrLogConfig.getAdminResource().getAdminResourceUris(true).forEach(StaticSitePlugin::copyResourceToCacheFolder);
        //video.js
        copyResourceToCacheFolder("/assets/css/font/vjs.eot");
        copyResourceToCacheFolder("/assets/css/font/vjs.svg");
        copyResourceToCacheFolder("/assets/css/font/vjs.ttf");
        copyResourceToCacheFolder("/assets/css/font/vjs.woff");
        copyResourceToCacheFolder("/assets/css/video-js.css");
        copyResourceToCacheFolder("/assets/css/katex.min.css");
        copyResourceToCacheFolder("/assets/js/video.js");
        //default avatar url
        copyResourceToCacheFolder("/assets/images/default-portrait.gif");
        File faviconFile = new File(PathUtil.getStaticPath() + "/favicon.ico");
        if (faviconFile.exists()) {
            try {
                Constants.zrLogConfig.getCacheService().saveToCacheFolder(new FileInputStream(faviconFile), "/" + faviconFile.getName());
            } catch (FileNotFoundException e) {
                LOGGER.warning("Missing resource " + faviconFile);
            }
        } else {
            //favicon
            copyResourceToCacheFolder("/favicon.ico");
        }
    }

    public static void copyResourceToCacheFolder(String resourceName) {
        InputStream inputStream = StaticSitePlugin.class.getResourceAsStream(resourceName);
        if (Objects.isNull(inputStream)) {
            LOGGER.warning("Missing resource " + resourceName);
            return;
        }
        if (Constants.zrLogConfig.getAdminResource().isAdminMainJs(resourceName)) {
            String stringInputStream = IOUtil.getStringInputStream(inputStream);
            String adminPath = Constants.zrLogConfig.getAdminResource().getContextPath().substring(1) + "admin/";
            String newStr = stringInputStream.replace("\"./admin/\"", "document.currentScript.baseURI + \"" + adminPath + "\"");
            Constants.zrLogConfig.getCacheService().saveToCacheFolder(new ByteArrayInputStream(newStr.getBytes()), resourceName);
            return;
        }
        Constants.zrLogConfig.getCacheService().saveToCacheFolder(inputStream, resourceName);
    }

    private static void copyDefaultTemplateAssets() {
        String templatePath = TemplateHelper.getTemplatePath(null);
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

    private CompletableFuture<Void> doAsyncFetch(String key, Executor executor) {
        handleStatusPageMap.put(key, HandleState.HANDING);
        return CompletableFuture.runAsync(() -> {
            boolean error = false;
            try {
                ResponseConfig responseConfig = serverConfig.getResponseConfig();
                responseConfig.setEnableGzip(false);
                HttpRequest httpRequest = WebTools.buildMockRequest(HttpMethod.GET, key, serverConfig.getRequestConfig(), applicationContext);
                new HttpRequestHandlerRunnable(httpRequest, new SimpleHttpResponse(httpRequest, serverConfig.getResponseConfig())).run();
                File file = (File) httpRequest.getAttr().get(HTML_FILE_KEY);
                if (Objects.equals(key, Constants.ADMIN_PWA_MANIFEST_JSON)) {
                    return;
                }
                if (Objects.equals(key, Constants.ADMIN_SERVICE_WORKER_JS)) {
                    return;
                }
                if (Objects.isNull(file) || !file.exists()) {
                    LOGGER.warning("Generator " + key + " error: missing static file");
                    error = true;
                    return;
                }
                doParse(file);
            } catch (Exception ex) {
                LOGGER.warning("Generator " + key + " error: " + ex.getMessage());
            } finally {
                if (error) {
                    //如果抓取错误了，需要暂停一下，避免重新获取过快
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException ex) {
                        LOGGER.warning("Generator " + key + " error: " + ex.getMessage());
                    }
                }
                handleStatusPageMap.put(key, error ? HandleState.RE_FETCH : HandleState.HANDLED);
            }
        }, executor);
    }

    private void doFetch() {
        ExecutorService executorService = Executors.newFixedThreadPool(20);
        try {
            CompletableFuture.allOf(handleStatusPageMap.entrySet().stream().filter(e -> Arrays.asList(HandleState.NEW, HandleState.RE_FETCH).contains(e.getValue())).map(e -> {
                return doAsyncFetch(e.getKey(), executorService);
            }).toArray(CompletableFuture[]::new)).join();
        } finally {
            executorService.shutdown();
        }
    }

    private void doGeneratorAllAsync() {
        if (!Constants.isStaticHtmlStatus()) {
            return;
        }
        if (StringUtils.isEmpty(ZrLogUtil.getBlogHostByWebSite())) {
            return;
        }
        File cacheFolder = Constants.zrLogConfig.getCacheService().getCacheHtmlFolder();
        if (cacheFolder.exists()) {
            FileUtils.deleteFile(cacheFolder.toString());
        }
        Constants.zrLogConfig.getCacheService().refreshFavicon();
        copyCommonAssert();
        copyDefaultTemplateAssets();
        handleStatusPageMap.clear();
        //从首页开始查找
        handleStatusPageMap.put("/", HandleState.NEW);
        handleStatusPageMap.put(Constants.ADMIN_PWA_MANIFEST_JSON, HandleState.NEW);
        handleStatusPageMap.put(Constants.ADMIN_SERVICE_WORKER_JS, HandleState.NEW);
        Constants.zrLogConfig.getAdminResource().getAdminPageUris().forEach(uri -> {
            handleStatusPageMap.put(uri, HandleState.NEW);
        });
        //生成 404 页面，用于配置第三方 cdn，或者云存储的错误页面
        String notFindFile = "/error/404.html";
        handleStatusPageMap.put(notFindFile, HandleState.NEW);
        PageServiceUtil.saveRedirectRules(notFindFile);
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
                LOGGER.info("Generator " + ZrLogUtil.getBlogHostByWebSite() + " size " + handleStatusPageMap.size() + " finished in " + usedTime + "ms");
            } else if (usedTime > Duration.ofSeconds(10).toMillis()) {
                LOGGER.warning("Generator slow size " + handleStatusPageMap.size() + " finish in " + usedTime);
            }
        }
    }

    @Override
    public boolean start() {
        doGeneratorAllAsync();
        return true;
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
