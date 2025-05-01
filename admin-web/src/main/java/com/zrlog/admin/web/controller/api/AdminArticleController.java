package com.zrlog.admin.web.controller.api;

import com.hibegin.common.util.BeanUtil;
import com.hibegin.common.util.StringUtils;
import com.hibegin.http.annotation.ResponseBody;
import com.zrlog.admin.business.AdminConstants;
import com.zrlog.admin.business.exception.PermissionErrorException;
import com.zrlog.admin.business.rest.request.CreateArticleRequest;
import com.zrlog.admin.business.rest.request.UpdateArticleRequest;
import com.zrlog.admin.business.rest.response.*;
import com.zrlog.admin.business.service.AdminArticleService;
import com.zrlog.admin.web.annotation.RefreshCache;
import com.zrlog.admin.web.token.AdminTokenThreadLocal;
import com.zrlog.business.plugin.StaticSitePlugin;
import com.zrlog.business.service.VisitorArticleService;
import com.zrlog.business.util.ControllerUtil;
import com.zrlog.common.Constants;
import com.zrlog.common.controller.BaseController;
import com.zrlog.common.exception.ArgsException;
import com.zrlog.common.exception.NotFindDbEntryException;
import com.zrlog.model.Log;
import com.zrlog.util.I18nUtil;
import com.zrlog.util.ThreadUtils;
import com.zrlog.util.ZrLogUtil;

import java.sql.SQLException;
import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

public class AdminArticleController extends BaseController {

    private final AdminArticleService articleService = new AdminArticleService();

    @RefreshCache(async = true)
    @ResponseBody
    public AdminApiPageDataStandardResponse<DeleteLogResponse> delete() throws SQLException {
        if (ZrLogUtil.isPreviewMode()) {
            throw new PermissionErrorException();
        }
        String idStr = getParamWithEmptyCheck("id");
        if (StringUtils.isEmpty(idStr)) {
            throw new ArgsException("id");
        }
        String[] ids = idStr.split(",");
        for (String id : ids) {
            articleService.delete(Long.valueOf(id));
        }
        return new AdminApiPageDataStandardResponse<>(new DeleteLogResponse(true));
    }

    private String getResponseMsg(CreateOrUpdateArticleResponse response) {
        return I18nUtil.getBackendStringFromRes(Objects.equals(response.getRubbish(), true)
                || Objects.equals(response.getPrivacy(), true) ? "saveSuccess" : "releaseSuccess");
    }

    @ResponseBody
    public AdminApiPageDataStandardResponse<ArticleGlobalResponse> create() throws SQLException {
        CreateOrUpdateArticleResponse create = articleService.create(AdminTokenThreadLocal.getUser(),
                getRequestBodyWithNullCheck(CreateArticleRequest.class));
        AdminApiPageDataStandardResponse<ArticleGlobalResponse> apiResponse = loadDetailById(create.getLogId() + "");
        apiResponse.setMessage(getResponseMsg(create));
        return apiResponse;
    }

    @ResponseBody
    public AdminApiPageDataStandardResponse<ArticleGlobalResponse> update() throws SQLException {
        CreateOrUpdateArticleResponse update = articleService.update(AdminTokenThreadLocal.getUser(),
                getRequestBodyWithNullCheck(UpdateArticleRequest.class));
        AdminApiPageDataStandardResponse<ArticleGlobalResponse> apiResponse = loadDetailById(update.getLogId() + "");
        LoadEditArticleResponse loadEditArticleResponse = apiResponse.getData().getArticle();
        //为发布状态才需要更新缓存信息（避免无用更新）
        if (Objects.equals(loadEditArticleResponse.isRubbish(), false)) {
            request.getAttr().put(AdminConstants.SYNC_UPDATE_CACHE_KEY, true);
        }
        apiResponse.setMessage(getResponseMsg(update));
        return apiResponse;
    }

    @ResponseBody
    public AdminApiPageDataStandardResponse<ArticlePageData> index() throws SQLException, ExecutionException, InterruptedException {
        String key = request.getParaToStr("key", "");
        String types = request.getParaToStr("types", "");
        int articlePageSize = AdminConstants.getAdminArticlePageSize();
        ArticlePageData pageData = articleService.adminPage(ControllerUtil.toPageRequest(this, articlePageSize), key, types, request);
        AdminApiPageDataStandardResponse<ArticlePageData> standardResponse = new AdminApiPageDataStandardResponse<>(pageData);
        standardResponse.setDocumentTitle(AdminConstants.getAdminDocumentTitleByUri(request.getUri()));
        return standardResponse;
    }


    private String getPreviewUrl(LoadEditArticleResponse articleResponseEntry) {
        String key = articleResponseEntry.getLogId() + "";
        if (StringUtils.isNotEmpty(articleResponseEntry.getAlias())) {
            key = articleResponseEntry.getAlias();
        }
        if (articleResponseEntry.isPrivacy() || articleResponseEntry.isRubbish()) {
            return "/admin/403?message=" + I18nUtil.getBackendStringFromRes("preview403");
        }
        return ZrLogUtil.getHomeUrlWithHost(getRequest()) + Constants.getArticleUri() + key + StaticSitePlugin.getSuffix(request);
    }

    private AdminApiPageDataStandardResponse<ArticleGlobalResponse> loadDetailById(String id) throws SQLException {
        ArticleGlobalResponse response = new ArticleGlobalResponse();
        ExecutorService executorService = ThreadUtils.newFixedThreadPool(2);
        try {
            CompletableFuture.allOf(CompletableFuture.runAsync(() -> {
                response.setTags(Constants.zrLogConfig.getCacheService().getTags());
            }, executorService), CompletableFuture.runAsync(() -> {
                response.setTypes(Constants.zrLogConfig.getCacheService().getArticleTypes());

            }, executorService)).join();
        } finally {
            executorService.shutdown();
        }
        if (StringUtils.isNotEmpty(id)) {
            response.setArticle(loadDetail(id));
        } else {
            response.setArticle(new LoadEditArticleResponse());
        }
        AdminApiPageDataStandardResponse<ArticleGlobalResponse> standardResponse = new AdminApiPageDataStandardResponse<>(response);
        StringJoiner sj = new StringJoiner(AdminConstants.ADMIN_TITLE_CHAR);
        if (Objects.nonNull(response.getArticle().getTitle())) {
            sj.add(response.getArticle().getTitle());
        }
        sj.add(AdminConstants.getAdminDocumentTitleByUri(request.getUri()));
        standardResponse.setDocumentTitle(sj.toString());
        return standardResponse;
    }

    @ResponseBody
    public AdminApiPageDataStandardResponse<ArticleGlobalResponse> articleEdit() throws SQLException {
        String id = request.getParaToStr("id", "");
        return loadDetailById(id);
    }

    /**
     * 仅保留，便于测试
     *
     * @return
     * @throws SQLException
     */
    @ResponseBody
    @Deprecated
    public AdminApiPageDataStandardResponse<LoadEditArticleResponse> detail() throws SQLException {
        return new AdminApiPageDataStandardResponse<>(loadDetail(getParamWithEmptyCheck("id")));
    }

    private LoadEditArticleResponse loadDetail(String id) throws SQLException {
        Map<String, Object> log = new Log().adminFindByIdOrAlias(id);
        if (log == null) {
            throw new NotFindDbEntryException();
        }
        log.remove("releaseTime");
        log.remove("last_update_date");
        log.remove("lastUpdateDate");
        LoadEditArticleResponse loadEditArticleResponse = BeanUtil.convert(VisitorArticleService.handlerArticle(log), LoadEditArticleResponse.class);
        loadEditArticleResponse.setPreviewUrl(getPreviewUrl(loadEditArticleResponse));
        return loadEditArticleResponse;
    }

}
