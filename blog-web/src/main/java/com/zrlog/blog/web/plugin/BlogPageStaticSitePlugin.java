package com.zrlog.blog.web.plugin;

import com.hibegin.common.BaseLockObject;
import com.hibegin.common.dao.ResultValueConvertUtils;
import com.hibegin.common.util.*;
import com.hibegin.http.server.ApplicationContext;
import com.hibegin.http.server.config.AbstractServerConfig;
import com.hibegin.http.server.util.PathUtil;
import com.zrlog.blog.web.interceptor.TemplateUtils;
import com.zrlog.business.plugin.StaticSitePlugin;
import com.zrlog.business.service.TemplateInfoHelper;
import com.zrlog.common.Constants;
import com.zrlog.common.vo.TemplateVO;
import com.zrlog.data.dto.FaviconBase64DTO;
import com.zrlog.model.WebSite;
import com.zrlog.util.ThreadUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

public class BlogPageStaticSitePlugin extends BaseLockObject implements StaticSitePlugin {

    private static final Logger LOGGER = LoggerUtil.getLogger(BlogPageStaticSitePlugin.class);
    private final AbstractServerConfig serverConfig;
    private final ApplicationContext applicationContext;
    private final Map<String, HandleState> handleStatusPageMap = new ConcurrentHashMap<>();
    private final String contextPath;
    private final PageService pageService;
    private final String defaultLang;
    private final ReentrantLock parseLock = new ReentrantLock();
    private final ExecutorService executorService = ThreadUtils.newFixedThreadPool(20);
    private final List<File> cacheFiles = new CopyOnWriteArrayList<>();

    public BlogPageStaticSitePlugin(AbstractServerConfig abstractServerConfig, String contextPath) {
        this.applicationContext = new ApplicationContext(abstractServerConfig.getServerConfig());
        this.applicationContext.init();
        this.serverConfig = abstractServerConfig;
        this.contextPath = contextPath;
        this.defaultLang = "zh_CN";
        this.pageService = new PageService(this);
        File cacheFolder = getCacheFile("/");
        if (cacheFolder.exists()) {
            FileUtils.deleteFile(cacheFolder.toString());
        }
    }

    @Override
    public ReentrantLock getParseLock() {
        return parseLock;
    }

    @Override
    public Executor getExecutorService() {
        return executorService;
    }

    private void refreshFavicon() {
        FaviconBase64DTO faviconBase64DTO = new WebSite().faviconBase64DTO();
        faviconHandle(faviconBase64DTO.getFavicon_ico_base64(), Constants.FAVICON_ICO_URI_PATH, ResultValueConvertUtils.toBoolean(faviconBase64DTO.getGenerator_html_status()));
        faviconHandle(faviconBase64DTO.getFavicon_png_pwa_192_base64(), Constants.FAVICON_PNG_PWA_192_URI_PATH, ResultValueConvertUtils.toBoolean(faviconBase64DTO.getGenerator_html_status()));
        faviconHandle(faviconBase64DTO.getFavicon_png_pwa_512_base64(), Constants.FAVICON_PNG_PWA_512_URI_PATH, ResultValueConvertUtils.toBoolean(faviconBase64DTO.getGenerator_html_status()));
    }


    private void handleRobotsTxt() {
        Map<String, Object> website = new WebSite().getPublicWebSite();
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
                saveToCacheFolder(new FileInputStream(robotFile), "/" + robotFile.getName());
            } catch (FileNotFoundException e) {
                LOGGER.warning("save to Cache error " + e.getMessage());
            }
        }
    }

    private void faviconHandle(String faviconIconBase64, String saveFilePath, Boolean saveToCache) {
        if (StringUtils.isEmpty(faviconIconBase64)) {
            copyResourceToCacheFolder(saveFilePath);
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
                saveToCacheFolder(new ByteArrayInputStream(binBytes), saveFilePath);
            }
        } catch (Exception e) {
            LOGGER.warning("Save favicon error " + e.getMessage());
        }
    }

    @Override
    public Map<String, HandleState> getHandleStatusPageMap() {
        return handleStatusPageMap;
    }

    @Override
    public String getContextPath() {
        return contextPath;
    }

    @Override
    public String getDefaultLang() {
        return defaultLang;
    }

    private void copyCommonAssert() {
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
        File faviconFile = PathUtil.getStaticFile("/favicon.ico");
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


    @Override
    public String getVersionFileName() {
        return "version.txt";
    }


    @Override
    public String getDbCacheKey() {
        return "static_blog_version";
    }

    private void doGenerator() {
        if (StaticSitePlugin.isDisabled()) {
            return;
        }
        refreshFavicon();
        handleRobotsTxt();
        copyCommonAssert();
        copyDefaultTemplateAssets();
        handleStatusPageMap.clear();
        //从首页开始查找
        handleStatusPageMap.put(contextPath + "/", HandleState.NEW);
        //生成 404 页面，用于配置第三方 cdn，或者云存储的错误页面
        handleStatusPageMap.put(notFindFile(), HandleState.NEW);
        pageService.saveRedirectRules(notFindFile());
        doFetch(serverConfig, applicationContext);
    }

    @Override
    public boolean start() {
        cacheFiles.clear();
        lock.lock();
        try {
            doGenerator();
        } finally {
            lock.unlock();
        }
        return true;
    }

    @Override
    public boolean autoStart() {
        if (EnvKit.isFaaSMode()) {
            return false;
        }
        return !StaticSitePlugin.isDisabled();
    }

    @Override
    public boolean isStarted() {
        return lock.isLocked();
    }

    @Override
    public boolean stop() {
        return true;
    }

    @Override
    public List<File> getCacheFiles() {
        return cacheFiles;
    }
}
