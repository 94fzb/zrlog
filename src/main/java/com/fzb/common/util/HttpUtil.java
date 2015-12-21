package com.fzb.common.util;

import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.Map.Entry;

/**
 * 用于发送HTTP POST 请求
 *
 * @author xiaochun
 */
public class HttpUtil {

    private static Logger log = Logger.getLogger(HttpUtil.class);


    /**
     * 利用HTTPClient 发送Post请求
     *
     * @param urlPath 服务器中servlet 的urlPath
     * @param params  需要提交的参数
     * @return
     * @throws IOException
     */
    public static String sendPostReuqest(String urlPath, Map<String, Object> params) throws IOException {
        log.info(urlPath + " http post params " + params);
        long start = System.currentTimeMillis();
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost httppost = postForm(urlPath, params);
        CloseableHttpResponse response = httpclient.execute(httppost);
        BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        String temp = null;
        StringBuffer sb = new StringBuffer();
        while ((temp = reader.readLine()) != null) {
            sb.append(temp);
        }
        reader.close();
        String html = new String(sb.toString().getBytes(), "utf-8");
        log.info("used Time " + html + " " + (System.currentTimeMillis() - start));
        return html;
    }

    /**
     * @param urlPath
     * @return
     * @throws IOException
     */
    public static String getResponse(String urlPath) throws IOException {
        return getResponse(urlPath, new HashMap<String, Object>());
    }

    public static String getResponse(String urlPath, Map<String, Object> params) throws IOException {
        urlPath += mapToQueryStr(params);
        ResponseData<String> data = new ResponseData<String>() {
        };
        try {
            return (String) getResponse(urlPath, data).getT();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 组装POST 请求参数
     *
     * @param urlPath
     * @param params
     * @return
     */
    @SuppressWarnings("deprecation")
    private static HttpPost postForm(String urlPath, Map<String, Object> params) {

        HttpPost httpost = new HttpPost(urlPath);
        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        if (params == null) {
            return httpost;
        }
        Set<String> keySet = params.keySet();
        for (String key : keySet) {
            if (params.get(key) instanceof List) {
                @SuppressWarnings("unchecked")
                List<String> list = (List<String>) params.get(key);
                for (String string : list) {
                    nvps.add(new BasicNameValuePair(key, string));
                }
            } else {
                nvps.add(new BasicNameValuePair(key, (String) params.get(key)));
            }
        }

        try {
            httpost.setEntity(new UrlEncodedFormEntity(nvps, HTTP.UTF_8));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return httpost;
    }

    public static ResponseData<?> getResponse(String urlPath, ResponseData<?> i) throws IOException, InstantiationException {
        return getResponse(urlPath, null, i, null);
    }

    public static ResponseData<?> getResponse(String urlPath, Map<String, Object> params, ResponseData<?> i) throws IOException, InstantiationException {
        return getResponse(urlPath, params, i, null);
    }

    public static ResponseData<?> getResponse(String urlPath, ResponseData<?> i, String filePath) throws IOException, InstantiationException {
        return getResponse(urlPath, null, i, filePath);
    }

    /**
     * 仅用于GET方式的下载文件
     *
     * @param urlPath
     * @param i
     * @param filePath 处理文件是需要这个参数
     * @return
     * @throws ClientProtocolException
     * @throws IOException
     * @throws InstantiationException
     */
    public static ResponseData<?> getResponse(String urlPath, Map<String, Object> params, ResponseData<?> i, String filePath) throws IOException, InstantiationException {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        urlPath += mapToQueryStr(params);
        HttpGet httpget = new HttpGet(urlPath);
        Class<?> clazz = i.getClazz();
        if (clazz == File.class) {
            if (filePath == null) return null;
            File f = null;
            CloseableHttpResponse response = null;
            try {
                if (!new URI(urlPath).getPath().contains(".")) {
                    httpget.getParams().setParameter("http.protocol.handle-redirects", false);
                    response = httpclient.execute(httpget);
                    Header[] headers = response.getAllHeaders();
                    String loca = null;
                    for (Header header : headers) {
                        if ("Location".equals(header.getName())) {
                            loca = header.getValue();
                            break;
                        }
                    }
                    if (loca != null) {
                        httpget = new HttpGet(loca);
                        String path = new URI(loca).getPath();
                        f = new File(filePath + "/" + path.substring(path.lastIndexOf("/") + 1));
                        response = httpclient.execute(httpget);
                    } else {
                        //TODO 还存在跳转吗??
                    }
                } else {
                    String path = new URI(urlPath).getPath();
                    f = new File(filePath + "/" + path.substring(path.lastIndexOf("/")));
                    response = httpclient.execute(httpget);
                }
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
            FileOutputStream fin = new FileOutputStream(f);
            fin.write(IOUtil.getByteByInputStream(response.getEntity().getContent()));
            fin.close();
            i.setT(f);
        } else if (clazz == String.class) {
            CloseableHttpResponse response = httpclient.execute(httpget);
            String html = IOUtil.getStringInputStream(response.getEntity().getContent());
            i.setT(html);
        }
        return i;
    }

    private static String mapToQueryStr(Map<String, Object> params) {
        String queryStr = "";
        if (params != null && !params.isEmpty()) {
            queryStr += "?";
            for (Entry<String, Object> param : params.entrySet()) {
                if (param.getValue() instanceof List) {
                    @SuppressWarnings("unchecked")
                    List<Object> values = (List<Object>) param.getValue();
                    for (Object object : values) {
                        queryStr += param.getKey() + "=" + object + "&";
                    }
                } else {
                    queryStr += param.getKey() + "=" + param.getValue() + "&";
                }
            }
            queryStr = queryStr.substring(0, queryStr.length() - 1);
        }
        return queryStr;
    }

    public static void main(String[] args) throws IOException {
        ResponseData<File> i = new ResponseData<File>() {
        };
        try {
            getResponse("http://localhost:8080/zrlog_ext/plugin/download?id=1", i, "E:/test");
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        File f = i.getT();
        System.out.println(f);
    }
}
