package com.zrlog.web.controller.admin.page;

import com.zrlog.common.Constants;
import com.zrlog.model.Log;
import com.zrlog.util.I18NUtil;
import com.zrlog.web.controller.BaseController;

public class AdminArticlePageController extends BaseController {

    public String preview() {
        Integer logId = getParaToInt("id");
        if (logId != null) {
            Log log = new Log().adminQueryLogByLogId(logId);
            log.put("lastLog", Log.dao.getLastLog(logId, I18NUtil.getStringFromRes("noLastLog", getRequest())));
            log.put("nextLog", Log.dao.getNextLog(logId, I18NUtil.getStringFromRes("noNextLog", getRequest())));

            setAttr("log", log.getAttrs());
            return getTemplatePath() + "/detail";
        } else {
            return Constants.ADMIN_NOT_FOUND_PAGE;
        }
    }

    public String edit() {
        if (getPara("id") != null) {
            Integer logId = Integer.parseInt(getPara("id"));
            Log log = Log.dao.adminQueryLogByLogId(logId);
            if (log != null) {
                setAttr("log", log.getAttrs());
            }
        }
        return "/admin/edit";
    }
}
