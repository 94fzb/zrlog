package com.zrlog.web.handler;

import com.googlecode.htmlcompressor.compressor.HtmlCompressor;
import com.hibegin.common.util.IOUtil;
import com.hibegin.common.util.http.handle.CloseResponseHandle;
import com.zrlog.service.CacheService;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.SimpleHtmlSerializer;
import org.htmlcleaner.TagNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

class ResponseRenderPrintWriter extends PrintWriter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ResponseRenderPrintWriter.class);

    private final StringBuilder builder = new StringBuilder();
    private HtmlCompressor compressor = new HtmlCompressor();
    private String body;
    private boolean compress;
    private long startTime = System.currentTimeMillis();
    private String baseUrl;
    private String endFlag;
    private HttpServletRequest request;

    public String getResponseBody() {
        return body;
    }

    public boolean isIncludePageEndTag(String str) {
        return str.trim().endsWith("</html>") || str.trim().endsWith(endFlag);
    }

    ResponseRenderPrintWriter(OutputStream out, boolean compress, String baseUrl, String endFlag, HttpServletRequest request) {
        super(out);
        this.compress = compress;
        compressor.setRemoveIntertagSpaces(true);
        compressor.setRemoveComments(true);
        this.baseUrl = baseUrl;
        this.endFlag = endFlag;
        this.request = request;
    }

    private void tryFlush() {
        if (isIncludePageEndTag(builder.toString())) {
            flush();
        }
    }

    @Override
    public void write(int c) {
        builder.append((char) c); // It is actually a char, not an int.
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

    // Finally override the flush method so that it trims whitespace.
    @Override
    public void flush() {
        synchronized (builder) {
            try {
                body = builder.toString();
                boolean includeEndTag = isIncludePageEndTag(body);
                if (includeEndTag) {
                    if (body.endsWith(endFlag)) {
                        body = body.substring(0, body.length() - endFlag.length());
                    }
                    HtmlCleaner htmlCleaner = new HtmlCleaner();
                    htmlCleaner.getProperties().setUseCdataForScriptAndStyle(false);
                    TagNode tagNode = htmlCleaner.clean(body);
                    TagNode[] tagNodes = tagNode.getAllElements(true);
                    Map<String, String> plugin = new HashMap<>();
                    for (TagNode tag : tagNodes) {
                        if (tag != null) {
                            String tagName = tag.getName();
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
                                    CloseResponseHandle handle = PluginHandler.getContext(url, "GET", request, false);
                                    byte[] bytes = IOUtil.getByteByInputStream(handle.getT().getEntity().getContent());
                                    plugin.put(content, new String(bytes, "UTF-8"));
                                } catch (Exception e) {
                                    LOGGER.error("", e);
                                }
                            }
                        }
                    }

                    SimpleHtmlSerializer serializer = new SimpleHtmlSerializer(htmlCleaner.getProperties());
                    StringWriter stringWriter = new StringWriter();
                    tagNode.serialize(serializer, stringWriter);
                    body = stringWriter.toString();
                    if (tagNode.getDocType() != null) {
                        body = tagNode.getDocType() + body;
                    }
                    for (Map.Entry<String, String> entry : plugin.entrySet()) {
                        body = body.replace(entry.getKey(), entry.getValue());
                    }
                }
                if (compress) {
                    body = compressor.compress(body);
                }
                if (includeEndTag) {
                    body = body + "<!--" + (System.currentTimeMillis() - startTime) + "ms-->";
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

    private String tryReplace(String href) {
        if (href.startsWith(baseUrl) || href.startsWith("admin/js") || href.startsWith("admin/markdwon") || href.startsWith("assets")) {
            String uriPath = href;
            if (href.startsWith(baseUrl)) {
                uriPath = href.substring(baseUrl.length());
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