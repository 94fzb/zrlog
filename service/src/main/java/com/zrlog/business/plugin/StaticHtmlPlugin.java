package com.zrlog.business.plugin;

import com.hibegin.common.BaseLockObject;
import com.hibegin.common.util.LoggerUtil;
import com.hibegin.common.util.StringUtils;
import com.hibegin.http.server.ApplicationContext;
import com.hibegin.http.server.api.HttpRequest;
import com.hibegin.http.server.config.AbstractServerConfig;
import com.hibegin.http.server.config.ResponseConfig;
import com.hibegin.http.server.handler.HttpRequestHandlerRunnable;
import com.hibegin.http.server.impl.HttpRequestDecoderImpl;
import com.hibegin.http.server.impl.SimpleHttpResponse;
import com.zrlog.common.Constants;
import com.zrlog.plugin.IPlugin;
import com.zrlog.util.ZrLogUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.time.Duration;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

public class StaticHtmlPlugin extends BaseLockObject implements IPlugin {
    public static final String HTML_FILE_KEY = "_htmlFile";


    private final Logger LOGGER = LoggerUtil.getLogger(StaticHtmlPlugin.class);
    private final AbstractServerConfig serverConfig;
    private final ApplicationContext applicationContext;
    private final Map<String, HandleState> handleStatusPageMap = new ConcurrentHashMap<>();

    public StaticHtmlPlugin(AbstractServerConfig abstractServerConfig) {
        this.applicationContext = new ApplicationContext(abstractServerConfig.getServerConfig());
        this.applicationContext.init();
        this.serverConfig = abstractServerConfig;
    }

    enum HandleState {
        NEW, HANDING, HANDLED
    }

    private HttpRequest buildMockRequest(String uri) throws Exception {
        String httpHeader = "GET " + uri + " HTTP/1.1\r\n" +
                "Host: " + ZrLogUtil.getBlogHostByWebSite() + "\r\n" +
                "X-Real-IP: 127.0.0.1\r\n" +
                "User-Agent: " + ZrLogUtil.STATIC_USER_AGENT + "\r\n" +
                "\r\n";
        HttpRequestDecoderImpl httpRequestDecoder = new HttpRequestDecoderImpl(serverConfig.getRequestConfig(), applicationContext, null);
        httpRequestDecoder.doDecode(ByteBuffer.wrap(httpHeader.getBytes(Charset.defaultCharset())));
        return httpRequestDecoder.getRequest();
    }

    private void doFetch() {
        handleStatusPageMap.entrySet().stream().filter(e -> Objects.equals(e.getValue(), HandleState.NEW)).forEach((e) -> {
            handleStatusPageMap.put(e.getKey(), HandleState.HANDING);
            try {
                ResponseConfig responseConfig = serverConfig.getResponseConfig();
                responseConfig.setEnableGzip(false);
                HttpRequest httpRequest = buildMockRequest(e.getKey());
                new HttpRequestHandlerRunnable(httpRequest, new SimpleHttpResponse(httpRequest, serverConfig.getResponseConfig())).run();
                File file = (File) httpRequest.getAttr().get(HTML_FILE_KEY);
                if (!file.exists()) {
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
        handleStatusPageMap.clear();
        //从首页开始查找
        handleStatusPageMap.put("/", HandleState.NEW);
        //生成 404 页面，用于配置第三方 cdn，或者云存储的错误页面
        handleStatusPageMap.put("/_404_.html", HandleState.NEW);
        lock.lock();
        long start = System.currentTimeMillis();
        try {
            while (handleStatusPageMap.values().stream().anyMatch(e -> e == HandleState.NEW)) {
                doFetch();
            }
        } finally {
            lock.unlock();
            long usedTime = (System.currentTimeMillis() - start);
            if (Constants.DEV_MODE) {
                LOGGER.info("Generator " + ZrLogUtil.getBlogHostByWebSite() + " size " + handleStatusPageMap.size() + " finished in " + usedTime + "ms");
            } else if (usedTime > Duration.ofMinutes(1).toMillis()) {
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
    public boolean stop() {
        return true;
    }
}
