package com.zrlog.web.controller.admin.api;

import com.zrlog.common.request.CreateArticleRequest;
import com.zrlog.common.request.UpdateArticleRequest;
import com.zrlog.common.response.*;
import com.zrlog.model.Log;
import com.zrlog.model.Tag;
import com.zrlog.service.ArticleService;
import com.zrlog.service.CacheService;
import com.zrlog.util.ZrLogUtil;
import com.zrlog.web.controller.BaseController;
import com.zrlog.service.AdminTokenThreadLocal;

public class ArticleController extends BaseController {

    private CacheService cacheService = new CacheService();

    private ArticleService articleService = new ArticleService();

    public DeleteLogResponse delete() {
        String[] ids = getPara("id").split(",");
        for (String id : ids) {
            delete(id);
        }
        DeleteLogResponse deleteLogResponse = new DeleteLogResponse();
        deleteLogResponse.setDelete(true);
        return deleteLogResponse;
    }

    private UpdateRecordResponse delete(Object logId) {
        if (logId != null) {
            Log log = Log.dao.adminFindLogByLogId(logId);
            if (log != null && log.get("keywords") != null) {
                Tag.dao.deleteTag(log.get("keywords").toString());
            }
            Log.dao.deleteById(logId);
        }
        return new UpdateRecordResponse();
    }

    public CreateOrUpdateLogResponse create() {
        // 移除缓存文件
        if (isStaticHtmlStatus()) {
            cacheService.removeCachedStaticFile();
        }
        return articleService.create(AdminTokenThreadLocal.getUserId(), ZrLogUtil.convertRequestBody(getRequest(), CreateArticleRequest.class));
    }

    public CreateOrUpdateLogResponse update() {
        // 移除缓存文件
        if (isStaticHtmlStatus()) {
            cacheService.removeCachedStaticFile();
        }
        return articleService.update(AdminTokenThreadLocal.getUserId(), ZrLogUtil.convertRequestBody(getRequest(), UpdateArticleRequest.class));
    }

    public PageableResponse<ArticleResponseEntry> index() {
        return articleService.page(getPageable(), convertRequestParam(getPara("keywords")));
    }
}
