package com.zrlog.web.inteceptor;

import com.hibegin.http.server.api.HandleAbleInterceptor;
import com.hibegin.http.server.api.HttpRequest;
import com.hibegin.http.server.api.HttpResponse;
import com.hibegin.http.server.util.PathUtil;
import com.hibegin.http.server.web.MethodInterceptor;
import com.zrlog.common.Constants;
import com.zrlog.util.ZrLogUtil;

import java.io.File;

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
            if (Constants.zrLogConfig.getCacheService().isCacheableByRequest(request)) {
                ZrLogUtil.putLongTimeCache(response);
            }
            response.writeFile(staticFile);
            return false;
        }
        if (Constants.catGeneratorHtml(actionKey)) {
            File cacheFile = Constants.zrLogConfig.getCacheService().loadCacheFile(request);
            if (cacheFile.exists()) {
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
        return !Constants.zrLogConfig.isStaticPluginRequest(request);
    }
}
