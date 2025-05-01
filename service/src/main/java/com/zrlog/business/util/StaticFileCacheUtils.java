package com.zrlog.business.util;

import com.hibegin.common.util.FileUtils;
import com.hibegin.common.util.LoggerUtil;
import com.hibegin.common.util.SecurityUtils;
import com.hibegin.http.server.util.PathUtil;
import com.zrlog.common.Constants;
import com.zrlog.data.cache.CacheServiceImpl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class StaticFileCacheUtils {

    private static final Logger LOGGER = LoggerUtil.getLogger(StaticFileCacheUtils.class);

    private final Map<String, String> cacheFileMap = new ConcurrentHashMap<>();

    private static StaticFileCacheUtils instance;

    private StaticFileCacheUtils() {
        refreshCacheFileMap();
    }

    public static StaticFileCacheUtils getInstance() {
        if (Objects.isNull(instance)) {
            instance = new StaticFileCacheUtils();
        }
        return instance;
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


    public void refreshCacheFileMap() {
        //缓存静态资源map
        Map<String, String> tempMap = getCacheFileMap();
        cacheFileMap.clear();
        //重新填充Map
        cacheFileMap.putAll(tempMap);
    }

    public boolean isCacheableByRequest(String uriPath) {
        //disable html client cache
        if (uriPath.endsWith(".html")) {
            return false;
        }
        return cacheFileMap.containsKey(uriPath.substring(1));
    }


    private String getStreamTag(InputStream inputStream) {
        return Math.abs(SecurityUtils.md5(inputStream).hashCode()) + "";
    }

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
}
