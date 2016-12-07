package com.fzb.blog.web.controller.admin.page;

import com.fzb.blog.common.Constants;
import com.fzb.blog.model.Log;
import com.fzb.blog.util.ResUtil;
import com.fzb.blog.web.controller.BaseController;

public class AdminArticlePageController extends BaseController {

    public String preview() {
        Integer logId = getParaToInt("id");
        if (logId != null) {
            Log log = new Log().adminQueryLogByLogId(logId);
            log.put("lastLog", Log.dao.getLastLog(logId, ResUtil.getStringFromRes("noLastLog", getRequest())));
            log.put("nextLog", Log.dao.getNextLog(logId, ResUtil.getStringFromRes("noNextLog", getRequest())));

            setAttr("log", log.getAttrs());
            return getTemplatePath() + "/detail";
        } else {
            return Constants.ADMIN_NOT_FOUND_PAGE;
        }
    }

    public String edit() {
        Integer logId = Integer.parseInt(getPara("id"));
        setAttr("log", Log.dao.adminQueryLogByLogId(logId).getAttrs());
        return "/admin/article_edit_frame";
    }
}
