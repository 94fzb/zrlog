package com.hibegin.common.util.http;

import com.hibegin.common.util.LoggerUtil;
import com.hibegin.common.util.http.handle.HttpHandle;
import com.hibegin.common.util.http.handle.HttpStringHandle;
import com.zrlog.common.Constants;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class HttpUtil {
    private static final Logger LOGGER = LoggerUtil.getLogger(HttpUtil.class);
    private final HttpClient httpClient;

    private static final HttpUtil instance = new HttpUtil();

    private HttpUtil() {
        this.httpClient = HttpClient.newBuilder().build();
    }

    public static HttpUtil getInstance() {
        return instance;
    }

    private static URI getUri(String urlPath, Map<String, String[]> requestParam) {
        if (requestParam.isEmpty()) {
            return URI.create(urlPath);
        }
        String queryStr = mapToQueryStr(requestParam);
        if (urlPath.contains("?")) {
            return URI.create(urlPath + "&" + queryStr);
        }
        return URI.create(urlPath + "?" + queryStr);
    }

    public <T> HttpHandle<T> sendGetRequest(String urlPath, Map<String, String[]> requestParam, HttpHandle<T> httpHandle, Map<String, String> reqHeaders) throws IOException, InterruptedException, URISyntaxException {
        HttpRequest.Builder builder = HttpRequest.newBuilder().uri(getUri(urlPath, requestParam)).GET();
        setHttpHeaders(builder, reqHeaders);
        HttpRequest request = builder.build();
        HttpResponse<InputStream> response = httpClient.send(request, HttpResponse.BodyHandlers.ofInputStream());
        return handleResponse(httpHandle, request, response);
    }

    public <T> HttpHandle<T> sendGetRequest(String urlPath, HttpHandle<T> httpHandle, Map<String, String> reqHeaders) throws IOException, InterruptedException, URISyntaxException {
        sendGetRequest(urlPath, new HashMap<>(), httpHandle, reqHeaders);
        return httpHandle;
    }

    public <T> HttpHandle<T> sendPostRequest(String urlPath, Map<String, String[]> params, HttpHandle<T> httpHandle, Map<String, String> reqHeaders) throws IOException, InterruptedException {
        HttpRequest.Builder builder = HttpRequest.newBuilder().uri(URI.create(urlPath)).POST(HttpRequest.BodyPublishers.ofString(mapToQueryStr(params)));
        setHttpHeaders(builder, reqHeaders);
        HttpRequest request = builder.build();
        HttpResponse<InputStream> response = httpClient.send(request, HttpResponse.BodyHandlers.ofInputStream());
        return handleResponse(httpHandle, request, response);
    }

    public <T> HttpHandle<T> sendPostRequest(String urlPath, byte[] bodyBytes, HttpHandle<T> httpHandle, Map<String, String> reqHeaders) throws IOException, InterruptedException {
        HttpRequest.Builder builder = HttpRequest.newBuilder().uri(URI.create(urlPath)).POST(HttpRequest.BodyPublishers.ofByteArray(bodyBytes));
        setHttpHeaders(builder, reqHeaders);
        HttpRequest request = builder.build();
        HttpResponse<InputStream> response = httpClient.send(request, HttpResponse.BodyHandlers.ofInputStream());
        return handleResponse(httpHandle, request, response);
    }

    private static String mapToQueryStr(Map<String, String[]> params) {
        return params.entrySet().stream().flatMap(entry -> Arrays.stream(entry.getValue()).map(value -> entry.getKey() + "=" + URLEncoder.encode(value, StandardCharsets.UTF_8))).collect(Collectors.joining("&"));
    }

    private void setHttpHeaders(HttpRequest.Builder requestBuilder, Map<String, String> headers) {
        requestBuilder.headers("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        requestBuilder.headers("Accept-Charset", "GB2312,UTF-8;q=0.7,*;q=0.7");
        requestBuilder.headers("Accept-Encoding", "gzip, deflate");
        requestBuilder.headers("Accept-Language", "zh-cn,zh;q=0.5");
        requestBuilder.headers("User-Agent", "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:41.0) Gecko/20100101 Firefox/41.0");
        if (Constants.debugLoggerPrintAble()) {
            if (!headers.isEmpty()) {
                TreeMap<String, String> stringStringTreeMap = new TreeMap<>(headers);
                stringStringTreeMap.put("Cookie", "******");
                LOGGER.info("headers = " + stringStringTreeMap);
            }
        }
        if (Objects.nonNull(headers)) {
            headers.forEach((k, v) -> {
                if (Objects.nonNull(v)) {
                    try {
                        requestBuilder.header(k, v);
                    } catch (Exception e) {
                        LOGGER.warning("set header " + k + ", " + v + ", error: " + e.getMessage());
                    }
                }
            });
        }
    }

    private <T> HttpHandle<T> handleResponse(HttpHandle<T> httpHandle, HttpRequest request, HttpResponse<InputStream> response) {
        httpHandle.handle(request, response);
        return httpHandle;
    }

    public String getTextByUrl(String url) throws IOException, InterruptedException, URISyntaxException {
        return sendGetRequest(url, new HttpStringHandle(), new HashMap<>()).getT();
    }

    public String getSuccessTextByUrl(String url) throws IOException, InterruptedException, URISyntaxException {
        HttpStringHandle httpStringHandle = (HttpStringHandle) sendGetRequest(url, new HttpStringHandle(), new HashMap<>());
        if (httpStringHandle.getStatusCode() == 200) {
            return httpStringHandle.getT();
        }
        return null;
    }
}
