package com.zrlog.business.service;

import com.hibegin.common.util.BeanUtil;
import com.hibegin.common.util.IOUtil;
import com.hibegin.common.util.StringUtils;
import com.hibegin.common.util.http.HttpUtil;
import com.hibegin.common.util.http.handle.HttpFileHandle;
import com.jfinal.core.JFinal;
import com.jfinal.kit.PathKit;
import com.zrlog.business.rest.request.CreateArticleRequest;
import com.zrlog.business.rest.request.UpdateArticleRequest;
import com.zrlog.business.rest.response.ArticleResponseEntry;
import com.zrlog.business.rest.response.CreateOrUpdateArticleResponse;
import com.zrlog.business.rest.response.UpdateRecordResponse;
import com.zrlog.business.util.ThumbnailUtil;
import com.zrlog.common.Constants;
import com.zrlog.common.rest.request.PageRequest;
import com.zrlog.common.vo.AdminTokenVO;
import com.zrlog.data.dto.PageData;
import com.zrlog.model.Log;
import com.zrlog.model.Tag;
import com.zrlog.util.ParseUtil;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.*;

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
        if (createArticleRequest instanceof UpdateArticleRequest) {
            log.update();
        } else {
            log.save();
        }
        CreateOrUpdateArticleResponse updateLogResponse = new CreateOrUpdateArticleResponse();
        updateLogResponse.setId(log.getInt("logId"));
        updateLogResponse.setAlias(log.getStr("alias"));
        updateLogResponse.setDigest(log.getStr("digest"));
        updateLogResponse.setThumbnail(log.getStr("thumbnail"));
        return updateLogResponse;
    }

    public UpdateRecordResponse delete(Object logId) {
        if (logId != null) {
            Log log = new Log().adminFindByIdOrAlias(logId);
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
        log.set("title", Jsoup.clean(createArticleRequest.getTitle(), Whitelist.basic()));
        if (StringUtils.isNotEmpty(createArticleRequest.getKeywords())) {
            log.set("keywords", Jsoup.clean(createArticleRequest.getKeywords(), Whitelist.basic()));
        } else {
            log.set("keywords", null);
        }
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
            log.set("digest", ParseUtil.autoDigest(log.get("content"), Constants.getAutoDigestLength()));
        } else {
            log.set("digest", createArticleRequest.getDigest());
        }
        log.set("plain_content", getPlainSearchText(log.get("content")));
        log.set("editor_type", createArticleRequest.getEditorType());
        int articleId;
        String alias;
        if (createArticleRequest instanceof UpdateArticleRequest) {
            articleId = Objects.requireNonNull(((UpdateArticleRequest) createArticleRequest).getLogId());
        } else {
            articleId = new Log().findMaxId() + 1;
            log.set("releaseTime", new Date());
        }
        if (StringUtils.isEmpty(createArticleRequest.getAlias())) {
            alias = Integer.toString(articleId);
        } else {
            alias = createArticleRequest.getAlias();
        }
        log.set("logId", articleId);
        log.set("alias", Jsoup.clean(alias.trim().replace(" ", "-").replace(".", "-"), Whitelist.basic()));
        return log;
    }

    public PageData<ArticleResponseEntry> page(PageRequest pageRequest, String keywords) {
        PageData<Log> data = new Log().adminFind(pageRequest, keywords);
        wrapperSearchKeyword(data, keywords);
        return convertPageable(data);
    }

    /**
     * 将输入的分页过后的对象，转化PageableResponse的对象
     *
     * @param object
     * @return
     */
    private PageData<ArticleResponseEntry> convertPageable(PageData<Log> object) {
        List<ArticleResponseEntry> dataList = new ArrayList<>();
        for (Log obj : object.getRows()) {
            dataList.add(BeanUtil.convert(obj.getAttrs(), ArticleResponseEntry.class));
        }
        PageData<ArticleResponseEntry> pageData = new PageData<>();
        pageData.setTotalElements(object.getTotalElements());
        pageData.setRows(dataList);
        return pageData;
    }

    public PageData<Log> searchArticle(int page, int row, String keywords) {
        PageData<Log> data = new Log().findByTitleOrPlainContentLike(page, row, keywords);
        wrapperSearchKeyword(data, keywords);
        return data;
    }

    /**
     * 高亮用户检索的关键字
     */
    private void wrapperSearchKeyword(PageData<Log> data, String keywords) {
        if (StringUtils.isNotEmpty(keywords)) {
            List<Log> logs = data.getRows();
            if (logs != null && !logs.isEmpty()) {
                for (Log log : logs) {
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
        if (StringUtils.isEmpty(content)) {
        return "";
        }
        return Jsoup.parse(content).body().text();
    }
}
