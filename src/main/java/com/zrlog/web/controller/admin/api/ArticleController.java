package com.zrlog.web.controller.admin.api;

import com.zrlog.common.response.*;
import com.zrlog.model.Log;
import com.zrlog.model.Tag;
import com.zrlog.service.ArticleService;
import com.zrlog.service.CacheService;
import com.zrlog.web.controller.BaseController;
import com.zrlog.web.token.AdminTokenThreadLocal;

public class ArticleController extends BaseController {

    private CacheService cacheService = new CacheService();

    private ArticleService articleService = new ArticleService();

    public DeleteLogResponse delete() {
        String ids[] = getPara("id").split(",");
        for (String id : ids) {
            delete(id);
        }
        DeleteLogResponse deleteLogResponse = new DeleteLogResponse();
        deleteLogResponse.setDelete(true);
        return deleteLogResponse;
    }

    private UpdateRecordResponse delete(Object logId) {
        if (logId != null) {
            Log log = Log.dao.adminQueryLogByLogId(logId);
            if (log != null && log.get("keywords") != null) {
                Tag.dao.deleteTag(log.get("keywords").toString());
            }
            Log.dao.deleteById(logId);
        }
        return new UpdateRecordResponse();
    }

    public CreateOrUpdateLogResponse createOrUpdate() {
        // 移除缓存文件
        if (getStaticHtmlStatus()) {
            cacheService.removeCachedStaticFile();
        }
        return articleService.createOrUpdate(AdminTokenThreadLocal.getUserId(), getRequest().getParameterMap());
    }

    public PageableResponse<ArticleResponseEntry> index() {
        return articleService.page(getPageable(), convertRequestParam(getPara("keywords")));
    }
}
