package com.zrlog.service;

import com.hibegin.common.util.IOUtil;
import com.hibegin.common.util.StringUtils;
import com.hibegin.common.util.http.HttpUtil;
import com.hibegin.common.util.http.handle.HttpFileHandle;
import com.jfinal.core.JFinal;
import com.jfinal.kit.PathKit;
import com.zrlog.common.Constants;
import com.zrlog.common.request.CreateArticleRequest;
import com.zrlog.common.request.PageableRequest;
import com.zrlog.common.request.UpdateArticleRequest;
import com.zrlog.common.response.ArticleResponseEntry;
import com.zrlog.common.response.CreateOrUpdateLogResponse;
import com.zrlog.common.response.PageableResponse;
import com.zrlog.model.Log;
import com.zrlog.model.Tag;
import com.zrlog.util.BeanUtil;
import com.zrlog.util.ParseUtil;
import com.zrlog.util.ThumbnailUtil;
import com.zrlog.web.token.AdminToken;
import com.zrlog.web.token.AdminTokenThreadLocal;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
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

    public CreateOrUpdateLogResponse create(Integer userId, CreateArticleRequest createArticleRequest) {
        return save(userId, createArticleRequest);
    }

    public CreateOrUpdateLogResponse update(Integer userId, UpdateArticleRequest updateArticleRequest) {
        return save(userId, updateArticleRequest);
    }

    private CreateOrUpdateLogResponse save(Integer userId, CreateArticleRequest createArticleRequest) {
        Log log = getLog(userId, createArticleRequest);
        if (!createArticleRequest.isRubbish() && StringUtils.isNotEmpty(log.getStr("keywords"))) {
            Tag.dao.insertTag(log.getStr("keywords"));
        }
        CreateOrUpdateLogResponse updateLogResponse = new CreateOrUpdateLogResponse();
        updateLogResponse.setId(log.getInt("logId"));
        updateLogResponse.setAlias(log.getStr("alias"));
        updateLogResponse.setDigest(log.getStr("digest"));
        if (createArticleRequest instanceof UpdateArticleRequest) {
            updateLogResponse.setError(log.update() ? 0 : 1);
        } else {
            updateLogResponse.setError(log.save() ? 0 : 1);
        }
        updateLogResponse.setThumbnail(log.getStr("thumbnail"));
        return updateLogResponse;
    }

    private Log getLog(Integer userId, CreateArticleRequest createArticleRequest) {
        Log log = new Log();
        log.set("content", createArticleRequest.getContent());
        log.set("title", createArticleRequest.getTitle());
        log.set("keywords", createArticleRequest.getKeywords());
        log.set("mdContent", createArticleRequest.getMdContent());
        log.set("content", createArticleRequest.getContent());
        log.set("userId", userId);
        log.set("typeId", createArticleRequest.getTypeId());
        log.set("last_update_date", new Date());
        log.set("canComment", createArticleRequest.isCanComment());
        log.set("recommended", createArticleRequest.isRecommended());
        log.set("private", createArticleRequest.is_private());
        log.set("rubbish", createArticleRequest.isRubbish());
        if (StringUtils.isEmpty(createArticleRequest.getThumbnail())) {
            log.set("thumbnail", getFirstImgUrl(createArticleRequest.getContent(), userId));
        } else {
            log.set("thumbnail", createArticleRequest.getThumbnail());
        }
        // 自动摘要
        if (StringUtils.isEmpty(createArticleRequest.getDigest())) {
            log.set("digest", ParseUtil.autoDigest(log.get("content").toString(), 200));
        } else {
            log.set("digest", createArticleRequest.getDigest());
        }
        log.set("plain_content", getPlainSearchTxt((String) log.get("content")));
        int articleId;
        String alias;
        if (createArticleRequest instanceof UpdateArticleRequest) {
            articleId = ((UpdateArticleRequest) createArticleRequest).getId();
        } else {
            articleId = Log.dao.getMaxRecord() + 1;
            log.set("releaseTime", new Date());
        }
        if (createArticleRequest.getAlias() == null) {
            alias = articleId + "";
        } else {
            alias = createArticleRequest.getAlias().trim().replace(" ", "-");
        }
        log.set("logId", articleId);
        log.set("alias", alias);
        return log;
    }

    public PageableResponse<ArticleResponseEntry> page(PageableRequest pageableRequest, String keywords) {
        Map<String, Object> data = Log.dao.queryAll(
                pageableRequest.getPage(), pageableRequest.getRows(), keywords, pageableRequest.getOrder(), pageableRequest.getSort());
        wrapperSearchKeyword(data, keywords);
        return BeanUtil.convertPageable(data, ArticleResponseEntry.class);
    }

    public Map<String, Object> searchArticle(int page, int row, String keywords) {
        Map<String, Object> data = Log.dao.findByTitleOrPlainContentLike(page, row, keywords);
        wrapperSearchKeyword(data, keywords);
        return data;
    }

    /**
     * 高亮用户检索的关键字
     */
    private void wrapperSearchKeyword(Map<String, Object> data, String keywords) {
        if (StringUtils.isNotEmpty(keywords)) {
            List<Object> logs = (List<Object>) data.get("rows");
            if (logs != null && !logs.isEmpty()) {
                for (Object tLog : logs) {
                    Map<String, Object> log = null;
                    if (tLog instanceof Map) {
                        log = (Map<String, Object>) tLog;
                    }
                    if (tLog instanceof Log) {
                        log = ((Log) tLog).getAttrs();
                    }
                    if (log != null) {
                        String title = log.get("title").toString();
                        String content = log.get("content").toString();
                        String digest = log.get("digest").toString();
                        log.put("title", wrapper(title, keywords));
                        String tryWrapperDigest = wrapper(digest, keywords);
                        boolean findInDigest = tryWrapperDigest.length() != digest.length();

                        if (findInDigest) {
                            log.put("digest", tryWrapperDigest);
                        } else {
                            String wrapperContent = wrapper(ParseUtil.removeHtmlElement(content), keywords);
                            log.put("digest", wrapperContent);
                        }
                    }
                }
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
                //检查默认目录是否存在
                new File(JFinal.me().getConstants().getBaseUploadPath()).mkdirs();
                String path = url;
                File thumbnailFile;
                byte[] bytes;
                if (url.startsWith("https://") || url.startsWith("http://")) {
                    path = new URL(url).getPath();
                    if (!path.startsWith(Constants.ATTACHED_FOLDER)) {
                        path = (Constants.ATTACHED_FOLDER + path).replace("//", "/");
                    } else {
                        path = path.replace("//", "/");
                    }
                    bytes = getRequestBodyBytes(url);
                    path = path.substring(0, path.indexOf(".")) + "_thumbnail" + path.substring(path.indexOf("."));
                    thumbnailFile = new File(PathKit.getWebRootPath() + path);
                } else {
                    bytes = IOUtil.getByteByInputStream(new FileInputStream(PathKit.getWebRootPath() + url));
                    path = url.substring(0, url.indexOf(".")) + "_thumbnail" + url.substring(path.indexOf("."));
                    thumbnailFile = new File(PathKit.getWebRootPath() + path);
                }
                int height = -1;
                int width = -1;
                if (bytes.length > 0) {
                    try {
                        String extName = thumbnailFile.getName().substring(thumbnailFile.getName().lastIndexOf("."));
                        //创建文件夹，避免保存失败
                        thumbnailFile.getParentFile().mkdirs();
                        if (!extName.equalsIgnoreCase(".gif")) {
                            IOUtil.writeBytesToFile(ThumbnailUtil.jpeg(bytes, 1f), thumbnailFile);
                            BufferedImage bimg = ImageIO.read(thumbnailFile);
                            height = bimg.getHeight();
                            width = bimg.getWidth();
                        } else {
                            IOUtil.writeBytesToFile(bytes, thumbnailFile);
                        }
                    } catch (IOException e) {
                        LOGGER.error("generation jpeg thumbnail error ", e);
                        return url;
                    }
                    return new UploadService().getCloudUrl(JFinal.me().getContextPath(), path, thumbnailFile.getPath(), null) + "?h=" + height + "&w=" + width;
                }
            } catch (Exception e) {
                LOGGER.error("", e);
            }
        }
        return null;
    }

    private byte[] getRequestBodyBytes(String url) throws IOException {
        HttpFileHandle fileHandler = new HttpFileHandle(JFinal.me().getConstants().getBaseUploadPath());
        HttpUtil.getInstance().sendGetRequest(url, new HashMap<String, String[]>(), fileHandler, new HashMap<String, String>());
        return IOUtil.getByteByInputStream(new FileInputStream(fileHandler.getT().getPath()));
    }

    public String getPlainSearchTxt(String content) {
        return Jsoup.parse(content).body().text();
    }
}
