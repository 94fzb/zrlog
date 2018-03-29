package com.zrlog.web.handler;

import com.googlecode.htmlcompressor.compressor.HtmlCompressor;
import com.zrlog.service.CacheService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

class TrimPrintWriter extends PrintWriter {

    private static final Logger LOGGER = LoggerFactory.getLogger(TrimPrintWriter.class);

    private final StringBuilder builder = new StringBuilder();
    private HtmlCompressor compressor = new HtmlCompressor();
    private String body;
    private boolean compress;
    private long startTime = System.currentTimeMillis();
    private String baseUrl;

    public String getResponseBody() {
        return body;
    }

    TrimPrintWriter(OutputStream out, boolean compress, String baseUrl) {
        super(out);
        this.compress = compress;
        compressor.setRemoveIntertagSpaces(true);
        compressor.setRemoveComments(true);
        this.baseUrl = baseUrl;
    }

    private void tryFlush() {
        if (builder.indexOf("</html>") > 0 || builder.indexOf("</partial-response>") > 0) {
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
                if (body.trim().endsWith("</html>")) {
                    Document document = Jsoup.parse(body);
                    List<ReplaceVo> replaceVoList = new ArrayList<>();

                    for (Element element : document.select("link")) {
                        tryReplace(replaceVoList, element, "href", element.attr("href"));
                    }
                    for (Element element : document.select("script")) {
                        tryReplace(replaceVoList, element, "src", element.attr("src"));
                    }
                    for (ReplaceVo replaceVo : replaceVoList) {
                        replaceVo.getElement().attr(replaceVo.getAttr(), replaceVo.getNewVal());
                    }
                    body = document.outputSettings(new Document.OutputSettings().prettyPrint(false)).outerHtml();
                }
                if (compress) {
                    body = compressor.compress(body);
                }
                if (body.trim().endsWith("</html>")) {
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

    private void tryReplace(List<ReplaceVo> replaceVoList, Element element, String attr, String href) {
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
                replaceVoList.add(new ReplaceVo(element, attr, href));
            }
        }
    }

    class ReplaceVo {
        Element element;
        String attr;
        String newVal;

        ReplaceVo(Element element, String attr, String newVal) {
            this.element = element;
            this.attr = attr;
            this.newVal = newVal;
        }

        public Element getElement() {
            return element;
        }

        public void setElement(Element element) {
            this.element = element;
        }

        public String getAttr() {
            return attr;
        }

        public void setAttr(String attr) {
            this.attr = attr;
        }

        public String getNewVal() {
            return newVal;
        }

        public void setNewVal(String newVal) {
            this.newVal = newVal;
        }
    }

}