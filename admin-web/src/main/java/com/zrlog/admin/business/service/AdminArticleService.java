package com.zrlog.admin.business.service;

import com.hibegin.common.dao.dto.PageData;
import com.hibegin.common.dao.dto.PageRequest;
import com.hibegin.common.util.*;
import com.hibegin.common.util.http.HttpUtil;
import com.hibegin.common.util.http.handle.HttpFileHandle;
import com.hibegin.http.server.api.HttpRequest;
import com.hibegin.http.server.util.PathUtil;
import com.zrlog.admin.business.AdminConstants;
import com.zrlog.admin.business.exception.ArticleMissingTitleException;
import com.zrlog.admin.business.exception.ArticleMissingTypeException;
import com.zrlog.admin.business.exception.UpdateArticleExpireException;
import com.zrlog.admin.business.rest.request.CreateArticleRequest;
import com.zrlog.admin.business.rest.request.UpdateArticleRequest;
import com.zrlog.admin.business.rest.response.ArticleActivityData;
import com.zrlog.admin.business.rest.response.ArticlePageData;
import com.zrlog.admin.business.rest.response.CreateOrUpdateArticleResponse;
import com.zrlog.admin.business.rest.response.UpdateRecordResponse;
import com.zrlog.business.rest.response.ArticleResponseEntry;
import com.zrlog.business.service.VisitorArticleService;
import com.zrlog.common.Constants;
import com.zrlog.common.vo.AdminTokenVO;
import com.zrlog.model.Log;
import com.zrlog.util.ParseUtil;
import com.zrlog.util.ThreadUtils;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

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
                path = URI.create(url).getPath();
                if (!path.startsWith(Constants.ATTACHED_FOLDER)) {
                    path = (Constants.ATTACHED_FOLDER + path).replace("//", "/");
                } else {
                    path = path.replace("//", "/");
                }
                bytes = getRequestBodyBytes(url);
                path = path.substring(0, path.indexOf('.')) + "_thumbnail" + path.substring(path.indexOf('.'));
            } else {
                bytes = IOUtil.getByteByInputStream(new FileInputStream(PathUtil.getStaticPath() + url));
                path = url.substring(0, url.indexOf('.')) + "_thumbnail" + url.substring(path.indexOf('.'));
            }
            File thumbnailFile = new File(PathUtil.getStaticPath() + path);
            if (bytes.length == 0) {
                return null;
            }
            int height = -1;
            int width = -1;
            //创建文件夹，避免保存失败
            thumbnailFile.getParentFile().mkdirs();
            IOUtil.writeBytesToFile(bytes, thumbnailFile);
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
        if (Objects.isNull(createArticleRequest) || StringUtils.isEmpty(createArticleRequest.getTitle())) {
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
        long articleId;
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
        //fix digest xss
        String parseInputDigest = Jsoup.clean(ObjectHelpers.requireNonNullElse(createArticleRequest.getDigest(), ""), Safelist.basicWithImages());
        // 自动摘要
        if (StringUtils.isEmpty(parseInputDigest) && Objects.equals(createArticleRequest.isRubbish(), false)) {
            int autoSize = AdminConstants.getAutoDigestLength();
            if (autoSize < 0) {
                log.put("digest", log.get("content"));
            } else if (autoSize == 0) {
                log.put("digest", "");
            } else {
                log.put("digest", ParseUtil.autoDigest((String) log.get("content"), autoSize));
            }
        } else {
            log.put("digest", parseInputDigest);
        }
        log.put("plain_content", ParseUtil.getPlainSearchText((String) log.get("content")));
        log.put("editor_type", createArticleRequest.getEditorType());
        String alias;
        if (StringUtils.isEmpty(createArticleRequest.getAlias())) {
            alias = Long.toString(articleId);
        } else {
            alias = createArticleRequest.getAlias();
        }
        log.put("alias", Jsoup.clean(alias.trim().replace(" ", "-").replace(".", "-"), Safelist.basic()));
        return log;
    }

    public ArticlePageData adminPage(PageRequest pageRequest, String keywords, String typeAlias, HttpRequest request) {
        ExecutorService executorService = ThreadUtils.newFixedThreadPool(2);
        try {
            CompletableFuture<PageData<Map<String, Object>>> dataCompletableFuture = CompletableFuture.supplyAsync(() -> {
                return new Log().adminFind(pageRequest, keywords, typeAlias);
            }, executorService);
            CompletableFuture<List<Map<String, Object>>> listCompletableFuture = CompletableFuture.supplyAsync(() -> {
                return Constants.zrLogConfig.getCacheService().getArticleTypes(request);
            }, executorService);
            CompletableFuture.allOf(listCompletableFuture, dataCompletableFuture).join();
            VisitorArticleService.wrapperSearchKeyword(dataCompletableFuture.join(), keywords);
            PageData<ArticleResponseEntry> articleResponseEntryPageData = VisitorArticleService.convertPageable(dataCompletableFuture.join(), request);
            ArticlePageData convert = BeanUtil.convert(articleResponseEntryPageData, ArticlePageData.class);
            convert.setTypes(listCompletableFuture.join());
            convert.setKey(keywords);
            convert.setDefaultPageSize(pageRequest.getSize());
            return convert;
        } finally {
            executorService.shutdown();
        }
    }

    public CompletableFuture<List<ArticleActivityData>> activityDataList(Executor executor) {
        return CompletableFuture.supplyAsync(() -> {
            Map<String, Long> adminArticleData;
            try {
                adminArticleData = new Log().getAdminArticleData();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            return adminArticleData.entrySet().stream().map(e -> {
                return new ArticleActivityData(e.getKey(), e.getValue());
            }).collect(Collectors.toList());
        }, executor);
    }
}
