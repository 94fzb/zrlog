package com.zrlog.web.controller.admin.api;

import com.hibegin.common.util.BeanUtil;
import com.zrlog.business.cache.CacheService;
import com.zrlog.business.exception.NotFindResourceException;
import com.zrlog.business.rest.request.CreateArticleRequest;
import com.zrlog.business.rest.request.UpdateArticleRequest;
import com.zrlog.business.rest.response.*;
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

    public LoadEditArticleResponse detail() {
        if (getPara("id") == null) {
            throw new NotFindResourceException();
        }
        Integer logId = Integer.parseInt(getPara("id"));
        Log log = new Log().adminFindByIdOrAlias(logId);
        if (log == null) {
            throw new NotFindResourceException();
        }
        return BeanUtil.convert(log.getAttrs(), LoadEditArticleResponse.class);
    }

}
