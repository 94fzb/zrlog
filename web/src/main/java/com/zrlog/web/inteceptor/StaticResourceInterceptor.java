package com.zrlog.web.inteceptor;

import com.hibegin.http.server.api.HandleAbleInterceptor;
import com.hibegin.http.server.api.HttpRequest;
import com.hibegin.http.server.api.HttpResponse;
import com.hibegin.http.server.util.PathUtil;
import com.hibegin.http.server.web.MethodInterceptor;
import com.zrlog.business.plugin.StaticSitePlugin;
import com.zrlog.business.util.StaticFileCacheUtils;
import com.zrlog.common.Constants;
import com.zrlog.util.ZrLogUtil;

import java.io.File;
import java.util.Objects;

public class StaticResourceInterceptor implements HandleAbleInterceptor {


    public StaticResourceInterceptor() {
    }

    @Override
    public boolean doInterceptor(HttpRequest request, HttpResponse response) throws Exception {
        String actionKey = request.getUri();
        //打包过后的静态资源文件进行拦截
        if (Constants.zrLogConfig.getStaticResourcePath().stream().anyMatch(e -> request.getUri().startsWith(e + "/"))) {
            ZrLogUtil.putLongTimeCache(response);
            new MethodInterceptor().doInterceptor(request, response);
            return false;
        }
        File staticFile = PathUtil.getStaticFile(actionKey);
        //静态文件进行拦截
        if (staticFile.isFile() && staticFile.exists()) {
            //缓存静态资源文件
            if (StaticFileCacheUtils.getInstance().isCacheableByRequest(request.getUri())) {
                ZrLogUtil.putLongTimeCache(response);
            }
            response.writeFile(staticFile);
            return false;
        }
        StaticSitePlugin staticSitePlugin = Constants.zrLogConfig.getPlugin(StaticSitePlugin.class);
        if (Objects.nonNull(staticSitePlugin)) {
            File cacheFile = staticSitePlugin.loadCacheFile(request);
            if (cacheFile.isFile() && cacheFile.exists()) {
                response.writeFile(cacheFile);
                return false;
            }
        }
        return true;
    }

    /**
     * staticPlugin，自己控制缓存文件的读取和生成方式
     *
     * @param request
     * @return
     */
    @Override
    public boolean isHandleAble(HttpRequest request) {
        return !StaticSitePlugin.isStaticPluginRequest(request);
    }
}
