package com.zrlog.admin.web.controller.api;

import com.hibegin.common.util.BeanUtil;
import com.hibegin.common.util.StringUtils;
import com.hibegin.http.annotation.ResponseBody;
import com.hibegin.http.server.api.HttpRequest;
import com.hibegin.http.server.api.HttpResponse;
import com.hibegin.http.server.web.Controller;
import com.zrlog.admin.business.exception.NotFindResourceException;
import com.zrlog.admin.business.exception.PermissionErrorException;
import com.zrlog.admin.business.rest.request.CreateArticleRequest;
import com.zrlog.admin.business.rest.request.UpdateArticleRequest;
import com.zrlog.admin.business.rest.response.*;
import com.zrlog.admin.business.service.AdminArticleService;
import com.zrlog.admin.business.util.ControllerUtil;
import com.zrlog.admin.web.annotation.RefreshCache;
import com.zrlog.admin.web.token.AdminTokenThreadLocal;
import com.zrlog.blog.web.util.WebTools;
import com.zrlog.common.Constants;
import com.zrlog.common.rest.response.ApiStandardResponse;
import com.zrlog.data.dto.PageData;
import com.zrlog.model.Log;
import com.zrlog.model.Tag;
import com.zrlog.model.Type;
import com.zrlog.util.ZrLogUtil;

import java.sql.SQLException;
import java.util.Map;

public class AdminArticleController extends Controller {

    private final AdminArticleService articleService = new AdminArticleService();

    public AdminArticleController() {
    }

    public AdminArticleController(HttpRequest request, HttpResponse response) {
        super(request, response);
    }

    @RefreshCache(async = true)
    @ResponseBody
    public ApiStandardResponse<DeleteLogResponse> delete() throws SQLException {
        if (ZrLogUtil.isPreviewMode()) {
            throw new PermissionErrorException();
        }
        String[] ids = getRequest().getParaToStr("id").split(",");
        for (String id : ids) {
            articleService.delete(Long.valueOf(id));
        }
        DeleteLogResponse deleteLogResponse = new DeleteLogResponse();
        deleteLogResponse.setDelete(true);
        return new ApiStandardResponse<>(deleteLogResponse);
    }

    @RefreshCache(async = true)
    @ResponseBody
    public ApiStandardResponse<CreateOrUpdateArticleResponse> create() {
        return new ApiStandardResponse<>(articleService.create(AdminTokenThreadLocal.getUser(), ZrLogUtil.convertRequestBody(getRequest(),
                CreateArticleRequest.class)));
    }

    @RefreshCache(async = true)
    @ResponseBody
    public ApiStandardResponse<CreateOrUpdateArticleResponse> update() {
        return new ApiStandardResponse<>(articleService.update(AdminTokenThreadLocal.getUser(), ZrLogUtil.convertRequestBody(getRequest(),
                UpdateArticleRequest.class)));
    }

    @ResponseBody
    public ApiStandardResponse<PageData<ArticleResponseEntry>> index() throws SQLException {
        PageData<ArticleResponseEntry> pageData = articleService.adminPage(ControllerUtil.getPageRequest(this),
                WebTools.convertRequestParam(request.getParaToStr("key")));
        pageData.getRows().forEach(x -> x.setUrl(WebTools.getHomeUrl(getRequest()) + Constants.getArticleUri() + getAccessKey(x)));
        return new ApiStandardResponse<>(pageData);
    }

    private String getAccessKey(ArticleResponseEntry articleResponseEntry) {
        if (StringUtils.isNotEmpty(articleResponseEntry.getAlias())) {
            return articleResponseEntry.getAlias();
        }
        return articleResponseEntry.getId() + "";
    }

    @ResponseBody
    public ApiStandardResponse<ArticleGlobalResponse> articleEdit() throws SQLException {
        ArticleGlobalResponse response = new ArticleGlobalResponse();
        response.setTags(new Tag().findAll());
        response.setTypes(new Type().findAll());
        String id = request.getParaToStr("id");
        if(StringUtils.isNotEmpty(id)){
            response.setArticle(detail().getData());
        } else {
            response.setArticle(new LoadEditArticleResponse());
        }
        return new ApiStandardResponse<>(response);
    }

    /**
     * 仅保留，便于测试
     * @return
     * @throws SQLException
     */
    @ResponseBody
    public ApiStandardResponse<LoadEditArticleResponse> detail() throws SQLException {
        if (getRequest().getParaToStr("id") == null) {
            throw new NotFindResourceException();
        }
        Map<String, Object> log = new Log().adminFindByIdOrAlias(request.getParaToStr("id"));
        if (log == null) {
            throw new NotFindResourceException();
        }
        log.remove("releaseTime");
        log.remove("last_update_date");
        log.remove("lastUpdateDate");
        return new ApiStandardResponse<>(BeanUtil.convert(log, LoadEditArticleResponse.class));
    }

}
