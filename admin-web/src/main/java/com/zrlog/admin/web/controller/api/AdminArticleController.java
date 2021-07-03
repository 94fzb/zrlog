package com.zrlog.admin.web.controller.api;

import com.hibegin.common.util.BeanUtil;
import com.hibegin.common.util.StringUtils;
import com.jfinal.core.Controller;
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
import com.zrlog.data.dto.PageData;
import com.zrlog.model.Log;
import com.zrlog.model.Tag;
import com.zrlog.model.Type;
import com.zrlog.util.ZrLogUtil;

public class AdminArticleController extends Controller {

    private final AdminArticleService articleService = new AdminArticleService();

    @RefreshCache(async = true)
    public DeleteLogResponse delete() {
        if (ZrLogUtil.isPreviewMode()) {
            throw new PermissionErrorException();
        }
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
        return articleService.create(AdminTokenThreadLocal.getUser(), ZrLogUtil.convertRequestBody(getRequest(),
                CreateArticleRequest.class));
    }

    @RefreshCache(async = true)
    public CreateOrUpdateArticleResponse update() {
        return articleService.update(AdminTokenThreadLocal.getUser(), ZrLogUtil.convertRequestBody(getRequest(),
                UpdateArticleRequest.class));
    }

    public PageData<ArticleResponseEntry> index() {
        PageData<ArticleResponseEntry> key = articleService.adminPage(ControllerUtil.getPageRequest(this),
                WebTools.convertRequestParam(getPara("key")));
        key.getRows().forEach(x -> x.setUrl(WebTools.getHomeUrl(getRequest()) + Constants.getArticleUri() + getAccessKey(x)));
        return key;
    }

    private String getAccessKey(ArticleResponseEntry articleResponseEntry) {
        if (StringUtils.isNotEmpty(articleResponseEntry.getAlias())) {
            return articleResponseEntry.getAlias();
        }
        return articleResponseEntry.getId() + "";
    }

    public ArticleGlobalResponse global() {
        ArticleGlobalResponse response = new ArticleGlobalResponse();
        response.setTags(new Tag().findAll());
        response.setTypes(new Type().findAll());
        return response;
    }

    public LoadEditArticleResponse detail() {
        if (getPara("id") == null) {
            throw new NotFindResourceException();
        }
        Log log = new Log().adminFindByIdOrAlias(getPara("id"));
        if (log == null) {
            throw new NotFindResourceException();
        }
        return BeanUtil.convert(log.getAttrs(), LoadEditArticleResponse.class);
    }

}
