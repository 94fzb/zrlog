package com.zrlog.blog.web.interceptor;

import com.hibegin.common.util.IOUtil;
import com.hibegin.common.util.LoggerUtil;
import com.hibegin.common.util.http.handle.CloseResponseHandle;
import com.hibegin.http.HttpMethod;
import com.hibegin.http.server.api.HttpRequest;
import com.hibegin.http.server.api.HttpResponse;
import com.zrlog.business.cache.CacheService;
import com.zrlog.business.util.PluginHelper;
import com.zrlog.common.vo.AdminTokenVO;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

class ResponseRenderPrintWriter extends PrintWriter {

    private static final Logger LOGGER = LoggerUtil.getLogger(ResponseRenderPrintWriter.class);

    private final StringBuilder builder = new StringBuilder();

    private String body;

    private final long startTime = System.currentTimeMillis();

    private final String baseUrl;

    private final HttpRequest request;

    private final HttpResponse response;


    private final AdminTokenVO adminTokenVO;

    public HttpResponse getResponse() {
        return response;
    }

    public String getResponseBody() {
        return body;
    }

    public boolean isIncludePageEndTag(String str) {
        return true;
    }

    ResponseRenderPrintWriter(OutputStream out, String baseUrl, HttpRequest request,
                              HttpResponse response, AdminTokenVO adminTokenVO) {
        super(out);
        this.baseUrl = baseUrl;
        this.request = request;
        this.response = response;
        this.adminTokenVO = adminTokenVO;
    }

    private void tryFlush() {
        if (isIncludePageEndTag(builder.toString())) {
            flush();
        }
    }

    @Override
    public void write(int c) {
        // It is actually a char, not an int.
        builder.append((char) c);
        tryFlush();
    }

    @Override
    public void write(char[] chars, int offset, int length) {
        builder.append(chars, offset, length);
        tryFlush();
    }

    @Override
    public void write(String string, int offset, int length) {
        builder.append(string, offset, length);
        tryFlush();
    }

    @Override
    public void flush() {
        synchronized (builder) {
            try {
                body = new String(builder.toString().getBytes(StandardCharsets.UTF_8));
                boolean includeEndTag = isIncludePageEndTag(body);
                if (includeEndTag) {
                    body = getCompressAndParseHtml(body);
                }
                if (out != null) {
                    out.write(body);
                }

                // Reset the local StringBuilder and issue real flush.
                builder.setLength(0);
                super.flush();
            } catch (IOException ex) {
                LOGGER.log(Level.SEVERE, "", ex);
            }
        }
    }

    public String getCompressAndParseHtml(String inputBody) throws IOException {
        Document document = Jsoup.parse(inputBody, "", Parser.xmlParser());
        Map<String, String> replaceMap = new HashMap<>();
        for (Element tag : document.getAllElements()) {
            if (tag != null) {
                String tagName = tag.tagName();
                addStaticResourceFlag(tag, tagName);
                parseCustomHtmlTag(tag, tagName, replaceMap);
            }
        }
        String html = document.html();
        for (Map.Entry<String, String> entry : replaceMap.entrySet()) {
            html = html.replaceAll(entry.getKey(), entry.getValue());
        }
        return html + "<!--" + (System.currentTimeMillis() - startTime) + "ms-->";
    }

    private void parseCustomHtmlTag(Element element, String tagName, Map<String, String> replaceMap) throws IOException {
        if ("plugin".equals(tagName) && !element.attr("name").isEmpty()) {
            try {
                String url = "/" + element.attr("name") + "/" + element.attr("view");
                if (!element.attr("param").isEmpty()) {
                    url += "?" + element.attr("param");
                }
                element.attr("_id", UUID.randomUUID().toString());
                CloseResponseHandle handle = PluginHelper.getContext(url, HttpMethod.GET, request, adminTokenVO);
                byte[] bytes = IOUtil.getByteByInputStream(handle.getT().body());
                replaceMap.put(element.outerHtml(), new String(bytes, StandardCharsets.UTF_8));
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "", e);
            }
        }
    }

    private void addStaticResourceFlag(Element tag, String tagName) {
        if ("script".equals(tagName)) {
            String src = tag.attr("src");
            if (!src.isEmpty()) {
                tag.attr("src", tryReplace(src));
            }
        }
        if ("link".equals(tagName)) {
            String src = tag.attr("href");
            if (!src.isEmpty()) {
                tag.attr("href", tryReplace(src));
            }
        }
    }

    private String tryReplace(String href) {
        String staticResourceBaseUrl = (String) request.getAttr().get("staticResourceBaseUrl");
        if (staticResourceBaseUrl == null) {
            staticResourceBaseUrl = baseUrl;
        }
        if (href.startsWith(baseUrl) || href.startsWith(staticResourceBaseUrl)) {
            String uriPath = href;
            //优先判断静态资源的情况
            if (href.startsWith(staticResourceBaseUrl)) {
                uriPath = href.substring(staticResourceBaseUrl.length());
            } else if (href.startsWith(baseUrl)) {
                uriPath = href.substring(baseUrl.length());
            }
            if (uriPath.contains("?")) {
                uriPath = uriPath.substring(0, uriPath.lastIndexOf("?"));
            }
            String flag = CacheService.getFileFlag(uriPath);
            if (flag != null) {
                if (href.contains("?")) {
                    href = href + "&t=" + flag;
                } else {
                    href = href + "?t=" + flag;
                }
            }
        }
        return href;
    }

}