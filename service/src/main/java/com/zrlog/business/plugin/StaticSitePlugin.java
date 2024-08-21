package com.zrlog.business.plugin;

import com.hibegin.common.BaseLockObject;
import com.hibegin.common.util.FileUtils;
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.time.Duration;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class StaticSitePlugin extends BaseLockObject implements IPlugin {
    public static final String HTML_FILE_KEY = "_htmlFile";


    private static final Logger LOGGER = LoggerUtil.getLogger(StaticSitePlugin.class);
    private final AbstractServerConfig serverConfig;
    private final ApplicationContext applicationContext;
    private final Map<String, HandleState> handleStatusPageMap = new ConcurrentHashMap<>();

    public StaticSitePlugin(AbstractServerConfig abstractServerConfig) {
        this.applicationContext = new ApplicationContext(abstractServerConfig.getServerConfig());
        this.applicationContext.init();
        this.serverConfig = abstractServerConfig;
    }

    enum HandleState {
        NEW, HANDING, HANDLED
    }


    private void doFetch() {
        handleStatusPageMap.entrySet().stream().filter(e -> Objects.equals(e.getValue(), HandleState.NEW)).forEach((e) -> {
            handleStatusPageMap.put(e.getKey(), HandleState.HANDING);
            try {
                ResponseConfig responseConfig = serverConfig.getResponseConfig();
                responseConfig.setEnableGzip(false);
                HttpRequest httpRequest = WebTools.buildMockRequest(HttpMethod.GET, e.getKey(), serverConfig.getRequestConfig(), applicationContext);
                new HttpRequestHandlerRunnable(httpRequest, new SimpleHttpResponse(httpRequest, serverConfig.getResponseConfig())).run();
                File file = (File) httpRequest.getAttr().get(HTML_FILE_KEY);
                if (Objects.isNull(file) || !file.exists()) {
                    return;
                }
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
            } catch (Exception ex) {
                LOGGER.warning("Generator " + e.getKey() + " error: " + ex.getMessage());
            } finally {
                handleStatusPageMap.put(e.getKey(), HandleState.HANDLED);
            }
        });

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
        copyCommonAssert();
        copyDefaultTemplateAssets();
        handleStatusPageMap.clear();
        //从首页开始查找
        handleStatusPageMap.put("/", HandleState.NEW);
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
            } else if (usedTime > Duration.ofMinutes(1).toMillis()) {
                LOGGER.warning("Generator slow size " + handleStatusPageMap.size() + " finish in " + usedTime);
            }
        }
    }

    private static void copyCommonAssert() {
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

    private static void copyResourceToCacheFolder(String resourceName) {
        InputStream inputStream = StaticSitePlugin.class.getResourceAsStream(resourceName);
        if (Objects.isNull(inputStream)) {
            LOGGER.warning("Missing resource " + resourceName);
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
}
