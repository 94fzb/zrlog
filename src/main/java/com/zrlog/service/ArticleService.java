package com.zrlog.service;

import com.zrlog.common.response.PageableResponse;
import com.zrlog.model.Log;
import com.zrlog.web.token.AdminTokenThreadLocal;
import com.zrlog.common.Constants;
import com.zrlog.common.request.PageableRequest;
import com.zrlog.common.response.ArticleResponseEntry;
import com.zrlog.common.response.CreateOrUpdateLogResponse;
import com.zrlog.model.Tag;
import com.zrlog.util.BeanUtil;
import com.zrlog.util.ParseUtil;
import com.zrlog.util.ThumbnailUtil;
import com.zrlog.web.token.AdminToken;
import com.hibegin.common.util.IOUtil;
import com.hibegin.common.util.http.HttpUtil;
import com.hibegin.common.util.http.handle.HttpFileHandle;
import com.jfinal.core.JFinal;
import com.jfinal.kit.PathKit;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 与文章相关的业务代码
 */
public class ArticleService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ArticleService.class);

    public CreateOrUpdateLogResponse createOrUpdate(Integer userId, Map<String, String[]> createArticleRequestMap) {
        String[] logId = createArticleRequestMap.get("logId");
        if (logId == null || StringUtils.isEmpty(logId[0])) {
            return add(userId, createArticleRequestMap);
        } else {
            return update(userId, createArticleRequestMap);
        }
    }

    private CreateOrUpdateLogResponse add(Integer userId, Map<String, String[]> createArticleRequestMap) {
        Log log = getLog(userId, createArticleRequestMap);
        if (BooleanUtils.isFalse(log.getBoolean("rubbish"))) {
            Tag.dao.insertTag(log.getStr("keywords"));
        }
        CreateOrUpdateLogResponse createOrUpdateLogResponse = getCreateOrUpdateLogResponse(log);
        createOrUpdateLogResponse.setError(log.save() ? 0 : 1);
        return createOrUpdateLogResponse;
    }

    private CreateOrUpdateLogResponse update(Integer userId, Map<String, String[]> createArticleRequestMap) {
        Log log = getLog(userId, createArticleRequestMap);
        String oldTagStr = Log.dao.findById(log.getInt("logId")).get("keywords");
        Tag.dao.update(log.getStr("keywords"), oldTagStr);
        CreateOrUpdateLogResponse updateLogResponse = getCreateOrUpdateLogResponse(log);
        updateLogResponse.setError(log.update() ? 0 : 1);
        return updateLogResponse;
    }

    private CreateOrUpdateLogResponse getCreateOrUpdateLogResponse(Log log) {
        CreateOrUpdateLogResponse updateLogResponse = new CreateOrUpdateLogResponse();
        updateLogResponse.setLogId(log.getInt("logId"));
        updateLogResponse.setAlias(log.getStr("alias"));
        updateLogResponse.setThumbnail(log.getStr("thumbnail"));
        return updateLogResponse;
    }

    private Log getLog(Integer userId, Map<String, String[]> createArticleRequestMap) {
        Log log = new Log();
        for (Map.Entry<String, String[]> map : createArticleRequestMap.entrySet()) {
            if (map.getValue().length > 0) {
                log.set(map.getKey(), map.getValue()[0]);
            }
        }
        Object logId = log.get("logId");
        String content = log.get("content");
        if (StringUtils.isEmpty(logId + "")) {
            logId = Log.dao.getMaxRecord() + 1;
            log.set("logId", logId);
            log.set("releaseTime", new Date());
        } else {
            log.set("logId", Integer.valueOf(logId.toString()));
        }
        if (log.get("alias") == null || "".equals((log.get("alias") + "").trim())) {
            log.set("alias", logId + "");
        } else {
            log.set("alias", (log.get("alias") + "").trim().replace(" ", "-"));
        }
        log.set("userId", userId);
        log.set("last_update_date", new Date());
        log.set("canComment", createArticleRequestMap.get("canComment") != null);
        log.set("recommended", createArticleRequestMap.get("recommended") != null);
        log.set("private", createArticleRequestMap.get("private") != null);
        log.set("rubbish", createArticleRequestMap.get("rubbish") != null);
        if (StringUtils.isBlank((String) log.get("thumbnail"))) {
            log.set("thumbnail", getFirstImgUrl(content, userId));
        }
        // 自动摘要
        if (log.get("digest") == null || "".equals(log.get("digest"))) {
            log.set("digest", ParseUtil.autoDigest(log.get("content").toString(), 200));
        }
        log.set("plain_content", getPlainSearchTxt((String) log.get("content")));
        return log;
    }

    public PageableResponse<ArticleResponseEntry> page(PageableRequest pageableRequest, String keywords) {
        return BeanUtil.convertPageable(Log.dao.queryAll(
                pageableRequest.getPage(), pageableRequest.getRows(), keywords, pageableRequest.getOrder(), pageableRequest.getSort()), ArticleResponseEntry.class);
    }

    /**
     * 高亮用户检索的关键字
     *
     * @param logs
     * @param keyword
     */
    public void wrapperSearchKeyword(List<Log> logs, String keyword) {
        for (Log log : logs) {
            String title = log.get("title").toString();
            String content = log.get("content").toString();
            String digest = log.get("digest").toString();
            log.put("title", wrapper(title, keyword));
            String tryWrapperDigest = wrapper(digest, keyword);
            boolean findInDigest = tryWrapperDigest.length() != digest.length();

            if (findInDigest) {
                log.put("digest", tryWrapperDigest);
            } else {
                String wrapperContent = wrapper(ParseUtil.removeHtmlElement(content), keyword);
                log.put("digest", wrapperContent);
            }
        }
    }

    private String wrapper(String content, String keyword) {
        String newContent = content;
        if (content.contains(keyword)) {
            newContent = content.replace(keyword, wrapperFontRed(keyword));
        } else {
            String lowerContent = content.toLowerCase();
            if (lowerContent.contains(keyword.toLowerCase())) {
                String[] strings = lowerContent.split(keyword.toLowerCase());
                StringBuilder sb = new StringBuilder();
                int count = 0;
                if (strings.length > 1) {
                    for (int i = 0; i < strings.length - 1; i++) {
                        count += strings[i].length();
                        String str = wrapperFontRed(content.substring(count, count + keyword.length()));
                        sb.append(content.substring(count - strings[i].length(), count));
                        sb.append(str);
                        count += keyword.length();
                    }
                    // 添加最后一段数据
                    sb.append(content.substring(count).replace(keyword.toLowerCase(), wrapperFontRed(keyword)));
                    newContent = sb.toString();
                } else if (strings.length > 0) {
                    sb.append(content.substring(0, strings[0].length()));
                    sb.append(lowerContent.substring(strings[0].length()).replace(keyword.toLowerCase(), wrapperFontRed(content.substring(strings[0].length()))));
                    newContent = sb.toString();
                } else {
                    newContent = wrapperFontRed(content);
                }
            }
        }
        if (newContent.length() != content.length()) {
            int first = newContent.indexOf("<font") - 5;
            if (first > -1 && newContent.length() > Constants.DEFAULT_ARTICLE_DIGEST_LENGTH) {
                String[] fontArr = (newContent.substring(first)).split("</font>");
                newContent = "";
                for (String str : fontArr) {
                    if (!"".equals(newContent) && newContent.length() + str.length() > Constants.DEFAULT_ARTICLE_DIGEST_LENGTH) {
                        if (newContent.length() < 100) {
                            newContent += str.substring(0, 100);
                        }
                        break;
                    }
                    newContent += str + "</font>";
                }
            }
        }
        return newContent;
    }

    private String wrapperFontRed(String content) {
        return "<font color=\"#CC0000\">" + content + "</font>";
    }

    public String getFirstImgUrl(String htmlContent, int userId) {
        Elements elements = Jsoup.parse(htmlContent).select("img");
        if (!elements.isEmpty()) {
            String url = elements.first().attr("src");
            try {
                AdminToken adminToken = new AdminToken();
                adminToken.setUserId(userId);
                AdminTokenThreadLocal.setAdminToken(adminToken);
                new File(JFinal.me().getConstants().getBaseUploadPath()).mkdirs();
                String path = url;
                File thumbnailFile;
                byte[] bytes;
                if (url.startsWith("https://") || url.startsWith("http://")) {
                    HttpFileHandle fileHandler = new HttpFileHandle(JFinal.me().getConstants().getBaseUploadPath());
                    path = new URL(url).getPath();
                    path = path.substring(0, path.indexOf(".")) + "_thumbnail" + path.substring(path.indexOf("."));
                    HttpUtil.getInstance().sendGetRequest(url, new HashMap<String, String[]>(), fileHandler, new HashMap<String, String>());
                    bytes = IOUtil.getByteByInputStream(new FileInputStream(fileHandler.getT().getPath()));
                    File tmpFile = new File(fileHandler.getT().getPath());
                    thumbnailFile = new File(tmpFile.getParent() + "/tmp-" + tmpFile.getName());
                } else {
                    path = url.substring(0, url.indexOf(".")) + "_thumbnail" + url.substring(path.indexOf("."));
                    thumbnailFile = new File(PathKit.getWebRootPath() + path);
                    bytes = IOUtil.getByteByInputStream(new FileInputStream(PathKit.getWebRootPath() + url));
                }
                if (bytes.length > 0) {
                    try {
                        String extName = thumbnailFile.getName().substring(thumbnailFile.getName().lastIndexOf("."));
                        if (!extName.equalsIgnoreCase(".gif")) {
                            IOUtil.writeBytesToFile(ThumbnailUtil.jpeg(bytes, 1f), thumbnailFile);
                        } else {
                            IOUtil.writeBytesToFile(bytes, thumbnailFile);
                        }
                    } catch (IOException e) {
                        LOGGER.error("generation jpeg thumbnail error ", e);
                        return url;
                    }
                    return new UploadService().getCloudUrl(JFinal.me().getContextPath(), path, thumbnailFile.getPath(), null) + "?v=1";
                }
            } catch (Exception e) {
                LOGGER.error("", e);
            }
        }
        return null;
    }

    public String getPlainSearchTxt(String content) {
        return Jsoup.parse(content).body().text();
    }
}
