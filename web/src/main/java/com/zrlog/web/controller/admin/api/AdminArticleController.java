package com.zrlog.web.controller.admin.api;

import com.zrlog.business.cache.CacheService;
import com.zrlog.business.rest.request.CreateArticleRequest;
import com.zrlog.business.rest.request.UpdateArticleRequest;
import com.zrlog.business.rest.response.ArticleGlobalResponse;
import com.zrlog.business.rest.response.ArticleResponseEntry;
import com.zrlog.business.rest.response.CreateOrUpdateArticleResponse;
import com.zrlog.business.rest.response.DeleteLogResponse;
import com.zrlog.business.service.ArticleService;
import com.zrlog.common.Constants;
import com.zrlog.data.dto.PageData;
import com.zrlog.model.Log;
import com.zrlog.util.ZrLogUtil;
import com.zrlog.web.annotation.RefreshCache;
import com.zrlog.web.controller.BaseController;
import com.zrlog.web.token.AdminTokenThreadLocal;
import com.zrlog.web.util.WebTools;

public class AdminArticleController extends BaseController {

    private final ArticleService articleService = new ArticleService();

    @RefreshCache
    public DeleteLogResponse delete() {
        String[] ids = getPara("id").split(",");
        for (String id : ids) {
            articleService.delete(id);
        }
        DeleteLogResponse deleteLogResponse = new DeleteLogResponse();
        deleteLogResponse.setDelete(true);
        return deleteLogResponse;
    }

    @RefreshCache
    public CreateOrUpdateArticleResponse create() {
        return articleService.create(AdminTokenThreadLocal.getUser(), ZrLogUtil.convertRequestBody(getRequest(), CreateArticleRequest.class));
    }

    @RefreshCache
    public CreateOrUpdateArticleResponse update() {
        return articleService.update(AdminTokenThreadLocal.getUser(), ZrLogUtil.convertRequestBody(getRequest(), UpdateArticleRequest.class));
    }

    public PageData<ArticleResponseEntry> index() {
        PageData<ArticleResponseEntry> key = articleService.page(getPageRequest(), convertRequestParam(getPara("key")));
        key.getRows().forEach(x -> x.setUrl(WebTools.getHomeUrl(getRequest()) + Constants.getArticleUri() + x.getId()));
        return key;
    }


    public ArticleGlobalResponse global() {
        return new CacheService().global();
    }

    public Log detail() {
        if (getPara("id") == null) {
            return null;
        }
        Integer logId = Integer.parseInt(getPara("id"));
        return new Log().adminFindByIdOrAlias(logId);
    }

}
