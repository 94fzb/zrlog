package com.zrlog.admin.business.service;

import com.hibegin.common.util.BeanUtil;
import com.hibegin.common.util.IOUtil;
import com.hibegin.common.util.LoggerUtil;
import com.hibegin.common.util.StringUtils;
import com.hibegin.common.util.http.HttpUtil;
import com.hibegin.common.util.http.handle.HttpFileHandle;
import com.hibegin.http.server.util.PathUtil;
import com.zrlog.admin.business.exception.ArticleMissingTitleException;
import com.zrlog.admin.business.exception.ArticleMissingTypeException;
import com.zrlog.admin.business.exception.UpdateArticleExpireException;
import com.zrlog.admin.business.rest.request.CreateArticleRequest;
import com.zrlog.admin.business.rest.request.UpdateArticleRequest;
import com.zrlog.admin.business.rest.response.ArticleResponseEntry;
import com.zrlog.admin.business.rest.response.CreateOrUpdateArticleResponse;
import com.zrlog.admin.business.rest.response.UpdateRecordResponse;
import com.zrlog.admin.business.util.ThumbnailUtil;
import com.zrlog.business.service.VisitorArticleService;
import com.zrlog.common.Constants;
import com.zrlog.common.rest.request.PageRequest;
import com.zrlog.common.vo.AdminTokenVO;
import com.zrlog.data.dto.PageData;
import com.zrlog.model.Log;
import com.zrlog.util.ParseUtil;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.jsoup.select.Elements;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AdminArticleService {

    private static final Logger LOGGER = LoggerUtil.getLogger(VisitorArticleService.class);

    private static final ReentrantLock WRITE_ARTICLE_LOCK = new ReentrantLock();

    public static String getFirstImgUrl(String htmlContent, AdminTokenVO adminTokenVO) {
        if (StringUtils.isEmpty(htmlContent)) {
            return "";
        }
        Elements elements = Jsoup.parse(htmlContent).select("img");
        if (elements.isEmpty()) {
            return null;
        }
        String url = elements.first().attr("src");
        try {
            String path = url;
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
            } else {
                bytes = IOUtil.getByteByInputStream(new FileInputStream(PathUtil.getStaticPath() + url.replace("", "")));
                path = url.substring(0, url.indexOf('.')) + "_thumbnail" + url.substring(path.indexOf('.'));
            }
            File thumbnailFile = new File(PathUtil.getStaticPath() + path);
            if (bytes.length == 0) {
                return null;
            }
            int height = -1;
            int width = -1;
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
            } catch (Throwable e) {
                //fixme jpeg()，对内存使用过大
                LOGGER.log(Level.SEVERE, "generation jpeg thumbnail error ", e);
                IOUtil.writeBytesToFile(bytes, thumbnailFile);
            }
            return new UploadService().getCloudUrl("", path, thumbnailFile.getPath(), null,
                    adminTokenVO).getUrl() + "?h=" + height + "&w=" + width;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "", e);
        }
        return null;
    }

    private static byte[] getRequestBodyBytes(String url) throws IOException, URISyntaxException, InterruptedException {
        HttpFileHandle fileHandler = new HttpFileHandle("");
        HttpUtil.getInstance().sendGetRequest(url, new HashMap<>(), fileHandler, new HashMap<>());
        return IOUtil.getByteByInputStream(new FileInputStream(fileHandler.getT().getPath()));
    }

    public CreateOrUpdateArticleResponse create(AdminTokenVO adminTokenVO, CreateArticleRequest createArticleRequest) {
        return save(adminTokenVO, createArticleRequest);
    }

    public CreateOrUpdateArticleResponse update(AdminTokenVO adminTokenVO, UpdateArticleRequest updateArticleRequest) {
        return save(adminTokenVO, updateArticleRequest);
    }

    private CreateOrUpdateArticleResponse save(AdminTokenVO adminTokenVO, CreateArticleRequest createArticleRequest) {
        if (StringUtils.isEmpty(createArticleRequest.getTitle())) {
            throw new ArticleMissingTitleException();
        }
        if (Objects.isNull(createArticleRequest.getTypeId()) || createArticleRequest.getTypeId() < 1) {
            throw new ArticleMissingTypeException();
        }
        WRITE_ARTICLE_LOCK.lock();
        try {
            Map<String, Object> log = getLog(adminTokenVO, createArticleRequest);
            if (createArticleRequest instanceof UpdateArticleRequest) {
                Number dbVersion = (Number) log.get("version");
                if (dbVersion.longValue() > ((UpdateArticleRequest) createArticleRequest).getVersion()) {
                    throw new UpdateArticleExpireException();
                }
                log.put("version", ((UpdateArticleRequest) createArticleRequest).getVersion() + 1);
                Log logDao = new Log();
                log.forEach((key, value) -> {
                    if (Objects.equals(key, "logId")) {
                        return;
                    }
                    logDao.set(key, value);
                });
                logDao.updateById(((UpdateArticleRequest) createArticleRequest).getLogId());
            } else {
                Log dbLog = new Log();
                dbLog.getAttrs().putAll(log);
                dbLog.save();
            }
            log.put("lastUpdateDate", ((Date) log.get("last_update_date")).getTime());
            log.remove("releaseTime");
            log.remove("last_update_date");
            return BeanUtil.convert(log,
                    CreateOrUpdateArticleResponse.class);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            WRITE_ARTICLE_LOCK.unlock();
        }

    }

    public UpdateRecordResponse delete(Long logId) throws SQLException {
        return new UpdateRecordResponse(new Log().deleteById(Math.toIntExact(logId)));
    }

    private Map<String, Object> getLog(AdminTokenVO adminTokenVO, CreateArticleRequest createArticleRequest) throws SQLException {
        Map<String, Object> log;
        int articleId;
        if (createArticleRequest instanceof UpdateArticleRequest) {
            log = new Log().loadById(((UpdateArticleRequest) createArticleRequest).getLogId());
            articleId = Objects.requireNonNull(((UpdateArticleRequest) createArticleRequest).getLogId());
        } else {
            log = new HashMap<>();
            articleId = new Log().findMaxId() + 1;
            log.put("releaseTime", new Date());
            log.put("version", 0);
            log.put("logId", articleId);
        }
        log.put("content", createArticleRequest.getContent());
        log.put("title", Jsoup.clean(createArticleRequest.getTitle(), Safelist.basic()));
        if (StringUtils.isNotEmpty(createArticleRequest.getKeywords())) {
            log.put("keywords", Jsoup.clean(createArticleRequest.getKeywords(), Safelist.basic()));
        } else {
            log.put("keywords", null);
        }
        log.put("markdown", createArticleRequest.getMarkdown());
        log.put("content", createArticleRequest.getContent());
        log.put("userId", adminTokenVO.getUserId());
        log.put("typeId", createArticleRequest.getTypeId());
        log.put("last_update_date", new Date());
        log.put("canComment", createArticleRequest.isCanComment());
        log.put("recommended", createArticleRequest.isRecommended());
        log.put("privacy", createArticleRequest.isPrivacy());
        log.put("rubbish", createArticleRequest.isRubbish());
        if (StringUtils.isEmpty(createArticleRequest.getThumbnail())) {
            log.put("thumbnail", getFirstImgUrl(createArticleRequest.getContent(), adminTokenVO));
        } else {
            log.put("thumbnail", createArticleRequest.getThumbnail());
        }
        // 自动摘要
        if (StringUtils.isEmpty(createArticleRequest.getDigest())) {
            log.put("digest", ParseUtil.autoDigest((String) log.get("content"), Constants.getAutoDigestLength()));
        } else {
            log.put("digest", createArticleRequest.getDigest());
        }
        //fix digest xss
        Jsoup.clean(Objects.requireNonNullElse(log.get("digest"), "").toString(), Safelist.basicWithImages());
        log.put("plain_content", VisitorArticleService.getPlainSearchText((String) log.get("content")));
        log.put("editor_type", createArticleRequest.getEditorType());
        String alias;
        if (StringUtils.isEmpty(createArticleRequest.getAlias())) {
            alias = Integer.toString(articleId);
        } else {
            alias = createArticleRequest.getAlias();
        }
        log.put("alias", Jsoup.clean(alias.trim().replace(" ", "-").replace(".", "-"), Safelist.basic()));
        return log;
    }

    public PageData<ArticleResponseEntry> adminPage(PageRequest pageRequest, String keywords) throws SQLException {
        PageData<Map<String, Object>> data = new Log().adminFind(pageRequest, keywords);
        VisitorArticleService.wrapperSearchKeyword(data, keywords);
        return convertPageable(data);
    }

    /**
     * 将输入的分页过后的对象，转化PageableResponse的对象
     */
    private PageData<ArticleResponseEntry> convertPageable(PageData<Map<String, Object>> object) {
        List<ArticleResponseEntry> dataList = new ArrayList<>();
        for (Map<String, Object> obj : object.getRows()) {
            obj.put("releaseTime", ((LocalDateTime) obj.get("releaseTime")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            obj.put("lastUpdateDate", ((LocalDateTime) obj.get("lastUpdateDate")).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            obj.remove("last_update_date");
            dataList.add(BeanUtil.convert(obj, ArticleResponseEntry.class));
        }
        PageData<ArticleResponseEntry> pageData = new PageData<>();
        pageData.setTotalElements(object.getTotalElements());
        pageData.setRows(dataList);
        return pageData;
    }

}
