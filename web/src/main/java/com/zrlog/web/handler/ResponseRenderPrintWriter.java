package com.zrlog.web.handler;

import com.hibegin.common.util.IOUtil;
import com.hibegin.common.util.http.handle.CloseResponseHandle;
import com.zrlog.common.vo.AdminTokenVO;
import com.zrlog.web.cache.CacheService;
import com.zrlog.web.util.PluginHelper;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.SimpleHtmlSerializer;
import org.htmlcleaner.TagNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

class ResponseRenderPrintWriter extends PrintWriter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResponseRenderPrintWriter.class);

    private final StringBuilder builder = new StringBuilder();
    private String body;
    private long startTime = System.currentTimeMillis();
    private String baseUrl;
    private String endFlag;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private String charset;
    private AdminTokenVO adminTokenVO;

    public String getResponseBody() {
        return body;
    }

    public boolean isIncludePageEndTag(String str) {
        return str.trim().endsWith("</html>") || str.trim().endsWith(endFlag);
    }

    ResponseRenderPrintWriter(OutputStream out, String baseUrl, String endFlag, HttpServletRequest request, HttpServletResponse response, AdminTokenVO adminTokenVO) {
        super(out);
        this.baseUrl = baseUrl;
        this.endFlag = endFlag;
        this.request = request;
        this.response = response;
        this.charset = System.getProperty("file.encoding");
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
                if (includeEndTag && response.getContentType().contains("text/html")) {
                    body = getCompressAndParseHtml(body);
                }
                out.write(body);

                // Reset the local StringBuilder and issue real flush.
                builder.setLength(0);
                super.flush();
            } catch (IOException ex) {
                LOGGER.error("", ex);
            }
        }
    }

    private String getCompressAndParseHtml(String inputBody) throws IOException {
        String currentBody = inputBody;

        //不显示none标签
        if (currentBody.endsWith(endFlag)) {
            currentBody = currentBody.substring(0, currentBody.length() - endFlag.length());
        }
        HtmlCleaner htmlCleaner = new HtmlCleaner();
        htmlCleaner.getProperties().setCharset(charset);
        htmlCleaner.getProperties().setUseCdataForScriptAndStyle(false);
        TagNode tagNode = htmlCleaner.clean(currentBody);
        TagNode[] tagNodes = tagNode.getAllElements(true);
        Map<String, String> plugin = new HashMap<>();
        for (TagNode tag : tagNodes) {
            if (tag != null) {
                String tagName = tag.getName();
                addStaticResourceFlag(tag, tagName);
                parseCustomHtmlTag(htmlCleaner, plugin, tag, tagName);
            }
        }

        SimpleHtmlSerializer serializer = new SimpleHtmlSerializer(htmlCleaner.getProperties());
        StringWriter stringWriter = new StringWriter();
        tagNode.serialize(serializer, stringWriter);
        currentBody = stringWriter.toString();
        if (tagNode.getDocType() != null) {
            currentBody = tagNode.getDocType() + currentBody;
        }
        for (Map.Entry<String, String> entry : plugin.entrySet()) {
            currentBody = currentBody.replace(entry.getKey(), entry.getValue());
        }
        currentBody = currentBody + "<!--" + (System.currentTimeMillis() - startTime) + "ms-->";
        return currentBody;

    }

    private void parseCustomHtmlTag(HtmlCleaner htmlCleaner, Map<String, String> plugin, TagNode tag, String tagName) throws IOException {
        if ("plugin".equals(tagName) && tag.hasAttribute("name")) {
            tag.setForeignMarkup(true);
            Map<String, String> tmp = new LinkedHashMap<>(tag.getAttributes());
            tmp.put("_tmp", System.currentTimeMillis() + "");
            tag.setAttributes(tmp);
            SimpleHtmlSerializer serializer = new SimpleHtmlSerializer(htmlCleaner.getProperties());
            StringWriter stringWriter = new StringWriter();
            tag.serialize(serializer, stringWriter);
            String content = stringWriter.toString();
            try {
                String url = "/" + tag.getAttributeByName("name") + "/" + tag.getAttributeByName("view");
                if (tag.hasAttribute("param")) {
                    url += "?" + tag.getAttributeByName("param");
                }
                CloseResponseHandle handle = PluginHelper.getContext(url, "GET", request, false, adminTokenVO);
                byte[] bytes = IOUtil.getByteByInputStream(handle.getT().getEntity().getContent());
                plugin.put(content, new String(bytes, StandardCharsets.UTF_8));
            } catch (Exception e) {
                LOGGER.error("", e);
            }
        }
    }

    private void addStaticResourceFlag(TagNode tag, String tagName) {
        if ("script".equals(tagName)) {
            String src = tag.getAttributeByName("src");
            if (src != null) {
                tag.setForeignMarkup(true);
                Map<String, String> tmp = new LinkedHashMap<>(tag.getAttributes());
                tmp.put("src", tryReplace(src));
                tag.setAttributes(tmp);
            }
        }
        if ("link".equals(tagName)) {
            String src = tag.getAttributeByName("href");
            if (src != null) {
                tag.setForeignMarkup(true);
                Map<String, String> tmp = new LinkedHashMap<>(tag.getAttributes());
                tmp.put("href", tryReplace(src));
                tag.setAttributes(tmp);
            }
        }
    }

    private String tryReplace(String href) {
        String staticResourceBaseUrl = (String) request.getAttribute("staticResourceBaseUrl");
        if (staticResourceBaseUrl == null) {
            staticResourceBaseUrl = baseUrl;
        }
        if (href.startsWith(baseUrl) || href.startsWith(staticResourceBaseUrl) || href.startsWith("admin/js") || href.startsWith("admin/markdwon") || href.startsWith("admin/summernote") || href.startsWith("assets")) {
            String uriPath = href;
            if (href.startsWith(baseUrl)) {
                uriPath = href.substring(baseUrl.length());
            } else if (href.startsWith(staticResourceBaseUrl)) {
                uriPath = href.substring(staticResourceBaseUrl.length());
            }
            if (uriPath.contains("?")) {
                uriPath = uriPath.substring(0, uriPath.lastIndexOf("?"));
            }
            uriPath = "/" + uriPath;
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