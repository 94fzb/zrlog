package com.zrlog.web.controller.admin.api;

import com.zrlog.common.request.CreateArticleRequest;
import com.zrlog.common.request.UpdateArticleRequest;
import com.zrlog.common.response.ArticleResponseEntry;
import com.zrlog.common.response.CreateOrUpdateArticleResponse;
import com.zrlog.common.response.DeleteLogResponse;
import com.zrlog.common.response.PageableResponse;
import com.zrlog.web.token.AdminTokenThreadLocal;
import com.zrlog.service.ArticleService;
import com.zrlog.util.ZrLogUtil;
import com.zrlog.web.annotation.RefreshCache;
import com.zrlog.web.controller.BaseController;

public class ArticleController extends BaseController {

    private ArticleService articleService = new ArticleService();

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

    public PageableResponse<ArticleResponseEntry> index() {
        return articleService.page(getPageable(), convertRequestParam(getPara("keywords")));
    }
}
