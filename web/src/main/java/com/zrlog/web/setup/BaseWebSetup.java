package com.zrlog.web.setup;

import com.hibegin.common.util.LoggerUtil;
import com.hibegin.http.server.api.Interceptor;
import com.zrlog.common.Constants;
import com.zrlog.common.Updater;
import com.zrlog.common.ZrLogConfig;
import com.zrlog.util.ThreadUtils;
import com.zrlog.web.WebSetup;
import com.zrlog.web.config.DbUtils;
import com.zrlog.web.inteceptor.GlobalBaseInterceptor;
import com.zrlog.web.inteceptor.MyI18nInterceptor;
import com.zrlog.web.inteceptor.StaticResourceInterceptor;

import java.util.List;
import java.util.logging.Logger;

public class BaseWebSetup implements WebSetup {

    private static final Logger LOGGER = LoggerUtil.getLogger(BaseWebSetup.class);

    private final ZrLogConfig zrLogConfig;

    public BaseWebSetup(ZrLogConfig zrLogConfig) {
        this.zrLogConfig = zrLogConfig;
    }

    @Override
    public void setup() {
        if (ThreadUtils.isEnableLoom() && Constants.debugLoggerPrintAble()) {
            LOGGER.info("Java VirtualThread(loom) enabled");
        }
        List<Class<? extends Interceptor>> interceptors = zrLogConfig.getServerConfig().getInterceptors();
        //all
        interceptors.add(GlobalBaseInterceptor.class);
        interceptors.add(StaticResourceInterceptor.class);
        interceptors.add(MyI18nInterceptor.class);
    }
}
