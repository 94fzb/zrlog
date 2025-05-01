package com.zrlog.web.inteceptor;

import com.hibegin.http.server.api.HandleAbleInterceptor;
import com.hibegin.http.server.api.HttpRequest;
import com.hibegin.http.server.api.HttpResponse;
import com.zrlog.util.I18nUtil;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * 多语言（国际化）相关的配置，这里并没有直接继承至提供的I18N方案
 */
public class MyI18nInterceptor implements HandleAbleInterceptor {

    private final Set<String> ignoreExt = new HashSet<>();

    public MyI18nInterceptor() {
        ignoreExt.add(".css");
        ignoreExt.add(".js");
        //image
        ignoreExt.add(".png");
        ignoreExt.add(".jpg");
        ignoreExt.add(".gif");
        ignoreExt.add(".webp");
        ignoreExt.add(".bmp");
        //video
        ignoreExt.add(".mp4");
        //audio
        ignoreExt.add(".mp3");
    }

    @Override
    public boolean doInterceptor(HttpRequest request, HttpResponse response) {
        I18nUtil.addToRequest(null, request);
        return true;
    }

    @Override
    public boolean isHandleAble(HttpRequest request) {
        String ext = request.getUri().lastIndexOf(".") > 0 ? request.getUri().substring(request.getUri().lastIndexOf(".")) : null;
        if (Objects.isNull(ext)) {
            return true;
        }
        return !ignoreExt.contains(ext);
    }
}
