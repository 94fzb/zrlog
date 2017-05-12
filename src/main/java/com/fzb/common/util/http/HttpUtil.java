package com.fzb.common.util.http;

import com.fzb.common.util.http.handle.HttpHandle;
import com.fzb.common.util.http.handle.HttpStringHandle;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;

public class HttpUtil {
    private static final Logger LOGGER = Logger.getLogger(HttpUtil.class);
    private static CloseableHttpClient httpClient;
    private static CloseableHttpClient disableRedirectHttpClient;
    private static HttpUtil disableRedirectInstance = new HttpUtil(true);
    private static HttpUtil instance = new HttpUtil(false);

    static {
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();
        httpClient = HttpClientBuilder.create().setConnectionManager(connectionManager).build();
        disableRedirectHttpClient = HttpClientBuilder.create().setConnectionManager(connectionManager).disableRedirectHandling().build();
    }

    private boolean disableRedirect;

    private HttpUtil(boolean disableRedirect) {
        this.disableRedirect = disableRedirect;
    }

    public static HttpUtil getDisableRedirectInstance() {
        return disableRedirectInstance;
    }

    public static HttpUtil getInstance() {
        return instance;
    }

    public static void main(String[] args) throws IOException {
        String urlStr = "http://ports.ubuntu.com/pool/universe/o/opencv/libopencv-imgproc2.4_2.4.9%2bdfsg-1ubuntu4_armhf.deb";
        URL url = new URL(urlStr);
        try {
            URI uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef());
            System.out.println(uri.toASCIIString());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private HttpPost postForm(String urlPath, Map<String, String[]> params) {
        HttpPost httPost = new HttpPost(urlPath);
        List<BasicNameValuePair> basicNameValuePairList = new ArrayList<BasicNameValuePair>();
        if (params == null) {
            return httPost;
        }
        Set<String> keySet = params.keySet();
        for (String key : keySet) {
            for (String string : params.get(key)) {
                try {
                    basicNameValuePairList.add(new BasicNameValuePair(key, URLDecoder.decode(string, "UTF-8")));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }
        try {
            httPost.setEntity(new UrlEncodedFormEntity(basicNameValuePairList, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return httPost;
    }

    private HttpPost postForm(String urlPath, byte[] data) {
        HttpPost httPost = new HttpPost(urlPath);
        httPost.setEntity(new ByteArrayEntity(data));
        return httPost;
    }

    private String mapToQueryStr(Map<String, String[]> params) {
        String queryStr = "";
        if ((params != null) && (!params.isEmpty())) {
            queryStr = queryStr + "?";
            Set<String> keySet = params.keySet();
            for (String key : keySet) {
                for (String string : params.get(key)) {
                    queryStr += key + "=" + string + "&";
                }
            }
            queryStr = queryStr.substring(0, queryStr.length() - 1);
        }
        return queryStr;
    }

    private void setHttpHeaders(HttpRequestBase header, Map<String, String> reqHeaders) {
        header.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        header.setHeader("Accept-Charset", "GB2312,UTF-8;q=0.7,*;q=0.7");
        header.setHeader("Accept-Encoding", "gzip, deflate");
        header.setHeader("Accept-Language", "zh-cn,zh;q=0.5");
        header.setHeader("User-Agent", "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:41.0) Gecko/20100101 Firefox/41.0");

        for (Map.Entry<String, String> reqHeader : reqHeaders.entrySet()) {
            header.setHeader(reqHeader.getKey(), reqHeader.getValue());
            LOGGER.info("key " + reqHeader.getKey() + " value-> " + reqHeader.getValue());
        }
    }

    public <T> HttpHandle<? extends T> sendPostRequest(String urlPath, Map<String, String[]> params,
                                                       HttpHandle<T> httpHandle, Map<String, String> reqHeaders)
            throws IOException, InstantiationException {
        LOGGER.info(urlPath + " http post params " + params);
        return sendRequest(postForm(urlPath, params), httpHandle, reqHeaders);
    }

    public <T> HttpHandle<? extends T> sendPostRequest(String urlPath, byte[] date,
                                                       HttpHandle<T> httpHandle, Map<String, String> reqHeaders)
            throws IOException, InstantiationException {
        reqHeaders.remove("Content-Length");
        return sendRequest(postForm(urlPath, date), httpHandle, reqHeaders);
    }

    public <T> HttpHandle<? extends T> sendRequest(HttpRequestBase httpRequestBase, HttpHandle<T> httpHandle, Map<String, String> reqHeaders)
            throws IOException {
        setHttpHeaders(httpRequestBase, reqHeaders);
        CloseableHttpClient tClient;
        if (disableRedirect) {
            tClient = disableRedirectHttpClient;
        } else {
            tClient = httpClient;
        }
        CloseableHttpResponse response = tClient.execute(httpRequestBase);
        boolean needClose = httpHandle.handle(httpRequestBase, response);
        if (needClose) {
            response.close();
        }
        return httpHandle;
    }

    public <T> HttpHandle<? extends T> sendGetRequest(String urlPath, Map<String, String[]> requestParam, HttpHandle<T> httpHandle, Map<String, String> reqHeaders)
            throws IOException {
        String queryStr = mapToQueryStr(requestParam);
        if (queryStr.length() > 0) {
            queryStr = queryStr.substring(1);
        }
        URI uri;
        try {
            URL url = new URL(urlPath);
            if (queryStr.length() == 0) {
                queryStr = url.getQuery() != null ? URLDecoder.decode(url.getQuery(), "UTF-8") : null;
            }
            uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), queryStr, url.getRef());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        HttpGet httpGet = new HttpGet(uri.toASCIIString());
        return sendRequest(httpGet, httpHandle, reqHeaders);
    }

    public <T> HttpHandle<? extends T> sendGetRequest(String urlPath, HttpHandle<T> httpHandle, Map<String, String> reqHeaders)
            throws IOException {
        return sendGetRequest(urlPath, null, httpHandle, reqHeaders);
    }

    public String getTextByUrl(String url) throws IOException {
        return sendGetRequest(url, new HttpStringHandle(), new HashMap<String, String>()).getT();
    }
}
