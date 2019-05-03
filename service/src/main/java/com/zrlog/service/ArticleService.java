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
import com.zrlog.common.response.CreateOrUpdateArticleResponse;
import com.zrlog.common.response.PageableResponse;
import com.zrlog.common.response.UpdateRecordResponse;
import com.zrlog.common.vo.AdminTokenVO;
import com.zrlog.model.Log;
import com.zrlog.model.Tag;
import com.zrlog.util.ParseUtil;
import com.zrlog.util.ThumbnailUtil;
import com.zrlog.util.ZrLogUtil;
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

    public CreateOrUpdateArticleResponse create(AdminTokenVO adminTokenVO, CreateArticleRequest createArticleRequest) {
        return save(adminTokenVO, createArticleRequest);
    }

    public CreateOrUpdateArticleResponse update(AdminTokenVO adminTokenVO, UpdateArticleRequest updateArticleRequest) {
        return save(adminTokenVO, updateArticleRequest);
    }

    private CreateOrUpdateArticleResponse save(AdminTokenVO adminTokenVO, CreateArticleRequest createArticleRequest) {
        Log log = getLog(adminTokenVO, createArticleRequest);
        new Tag().refreshTag();
        CreateOrUpdateArticleResponse updateLogResponse = new CreateOrUpdateArticleResponse();
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

    public UpdateRecordResponse delete(Object logId) {
        if (logId != null) {
            Log log = new Log().adminFindLogByLogId(logId);
            if (log != null && StringUtils.isNotEmpty(log.get("keywords"))) {
                new Tag().refreshTag();
            }
            new Log().deleteById(logId);
        }
        return new UpdateRecordResponse();
    }

    private Log getLog(AdminTokenVO adminTokenVO, CreateArticleRequest createArticleRequest) {
        Log log = new Log();
        log.set("content", createArticleRequest.getContent());
        log.set("title", createArticleRequest.getTitle());
        log.set("keywords", createArticleRequest.getKeywords());
        log.set("markdown", createArticleRequest.getMarkdown());
        log.set("content", createArticleRequest.getContent());
        log.set("userId", adminTokenVO.getUserId());
        log.set("typeId", createArticleRequest.getTypeId());
        log.set("last_update_date", new Date());
        log.set("canComment", createArticleRequest.isCanComment());
        log.set("recommended", createArticleRequest.isRecommended());
        log.set("privacy", createArticleRequest.isPrivacy());
        log.set("rubbish", createArticleRequest.isRubbish());
        if (StringUtils.isEmpty(createArticleRequest.getThumbnail())) {
            log.set("thumbnail", getFirstImgUrl(createArticleRequest.getContent(), adminTokenVO));
        } else {
            log.set("thumbnail", createArticleRequest.getThumbnail());
        }
        // 自动摘要
        if (StringUtils.isEmpty(createArticleRequest.getDigest())) {
            log.set("digest", ParseUtil.autoDigest(log.get("content").toString(), Constants.getAutoDigestLength()));
        } else {
            log.set("digest", createArticleRequest.getDigest());
        }
        log.set("plain_content", getPlainSearchText(log.get("content")));
        log.set("editor_type", createArticleRequest.getEditorType());
        int articleId;
        String alias;
        if (createArticleRequest instanceof UpdateArticleRequest) {
            articleId = ((UpdateArticleRequest) createArticleRequest).getId();
        } else {
            articleId = new Log().findMaxId() + 1;
            log.set("releaseTime", new Date());
        }
        if (createArticleRequest.getAlias() == null) {
            alias = Integer.toString(articleId);
        } else {
            alias = createArticleRequest.getAlias();
        }
        log.set("logId", articleId);
        log.set("alias", alias.trim().replace(" ", "-").replace(".", "-"));
        return log;
    }

    public PageableResponse<ArticleResponseEntry> page(PageableRequest pageableRequest, String keywords) {
        Map<String, Object> data = new Log().find(pageableRequest.getPage(), pageableRequest.getRows(), keywords, pageableRequest.getOrder(), pageableRequest.getSort());
        wrapperSearchKeyword(data, keywords);
        return ZrLogUtil.convertPageable(data, ArticleResponseEntry.class);
    }

    public Map<String, Object> searchArticle(int page, int row, String keywords) {
        Map<String, Object> data = new Log().findByTitleOrPlainContentLike(page, row, keywords);
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
                    } else if (tLog instanceof Log) {
                        log = ((Log) tLog).getAttrs();
                    }
                    if (log != null) {
                        String title = log.get("title").toString();
                        String content = log.get("content").toString();
                        String digest = log.get("digest").toString();
                        log.put("title", ParseUtil.wrapperKeyword(title, keywords));
                        String tryWrapperDigest = ParseUtil.wrapperKeyword(digest, keywords);
                        boolean findInDigest = tryWrapperDigest.length() != digest.length();

                        if (findInDigest) {
                            log.put("digest", tryWrapperDigest);
                        } else {
                            log.put("digest", ParseUtil.wrapperKeyword(ParseUtil.removeHtmlElement(content), keywords));
                        }
                    }
                }
            }
        }

    }

    public String getFirstImgUrl(String htmlContent, AdminTokenVO adminTokenVO) {
        if (StringUtils.isEmpty(htmlContent)) {
            return "";
        }
        Elements elements = Jsoup.parse(htmlContent).select("img");
        if (!elements.isEmpty()) {
            String url = elements.first().attr("src");
            try {
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
                    path = path.substring(0, path.indexOf('.')) + "_thumbnail" + path.substring(path.indexOf('.'));
                    thumbnailFile = new File(PathKit.getWebRootPath() + path);
                } else {
                    bytes = IOUtil.getByteByInputStream(new FileInputStream(PathKit.getWebRootPath() + url.replace(JFinal.me().getContextPath(), "")));
                    path = url.substring(0, url.indexOf('.')) + "_thumbnail" + url.substring(path.indexOf('.'));
                    thumbnailFile = new File(PathKit.getWebRootPath() + path);
                }
                int height = -1;
                int width = -1;
                if (bytes.length > 0) {
                    try {
                        String extName = thumbnailFile.getName().substring(thumbnailFile.getName().lastIndexOf('.'));
                        //创建文件夹，避免保存失败
                        thumbnailFile.getParentFile().mkdirs();
                        if (!".gif".equalsIgnoreCase(extName)) {
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
                    return new UploadService().getCloudUrl(JFinal.me().getContextPath(), path, thumbnailFile.getPath(), null, adminTokenVO).getUrl() + "?h=" + height + "&w=" + width;
                }
            } catch (Exception e) {
                LOGGER.error("", e);
            }
        }
        return null;
    }

    private byte[] getRequestBodyBytes(String url) throws IOException {
        HttpFileHandle fileHandler = new HttpFileHandle(JFinal.me().getConstants().getBaseUploadPath());
        HttpUtil.getInstance().sendGetRequest(url, new HashMap<>(), fileHandler, new HashMap<>());
        return IOUtil.getByteByInputStream(new FileInputStream(fileHandler.getT().getPath()));
    }

    public String getPlainSearchText(String content) {
        return Jsoup.parse(content).body().text();
    }
}
