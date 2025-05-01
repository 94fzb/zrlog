package com.zrlog.web.config;

import com.hibegin.common.util.LoggerUtil;
import com.hibegin.http.server.api.HttpRequest;
import com.hibegin.http.server.api.HttpRequestListener;
import com.hibegin.http.server.api.HttpResponse;
import com.zrlog.common.Constants;
import com.zrlog.util.I18nUtil;

import java.util.logging.Logger;

public class ZrLogHttpRequestListener implements HttpRequestListener {

    private static final Logger LOGGER = LoggerUtil.getLogger(HttpRequestListener.class);


    @Override
    public void onHandled(HttpRequest request, HttpResponse httpResponse) {
        try {
            I18nUtil.removeI18n();
        } finally {
            long used = System.currentTimeMillis() - request.getCreateTime();
            if (used > 5000) {
                LOGGER.info("Slow request [" + request.getMethod() + "] " + request.getUri() + " used time " + used + "ms");
            } else if (Constants.debugLoggerPrintAble()) {
                LOGGER.info("Request [" + request.getMethod() + "] " + request.getUri() + " used time " + used + "ms");
            }
        }
    }
}
