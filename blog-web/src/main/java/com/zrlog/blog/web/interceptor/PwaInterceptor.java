package com.zrlog.blog.web.interceptor;

import com.hibegin.common.util.FileUtils;
import com.hibegin.http.server.api.HttpRequest;
import com.hibegin.http.server.api.HttpResponse;
import com.hibegin.http.server.util.MimeTypeUtil;
import com.hibegin.http.server.util.PathUtil;
import com.zrlog.common.Constants;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Objects;

public class PwaInterceptor implements HandleAbleInterceptor {
    @Override
    public boolean isHandleAble(HttpRequest request) {
        return Arrays.asList(Constants.FAVICON_ICO_URI_PATH, Constants.FAVICON_PNG_PWA_192_URI_PATH, Constants.FAVICON_PNG_PWA_512_URI_PATH).contains(request.getUri());
    }

    @Override
    public boolean doInterceptor(HttpRequest request, HttpResponse response) {
        try (InputStream resourceAsStream = BlogArticleInterceptor.class.getResourceAsStream(request.getUri())) {
            if (Objects.nonNull(resourceAsStream)) {
                response.getHeader().put("Content-Type", MimeTypeUtil.getMimeStrByExt(FileUtils.getFileExt(request.getUri())));
                File file = PathUtil.getStaticFile(request.getUri());
                //优先使用外部配置的
                if (file.exists()) {
                    response.write(new FileInputStream(file), 200);
                    return false;
                }
                response.write(resourceAsStream, 200);
                return false;
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        return true;
    }
}
