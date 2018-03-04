package com.zrlog.web.controller.admin.page;

import com.zrlog.common.Constants;
import com.zrlog.model.Log;
import com.zrlog.util.I18NUtil;
import com.zrlog.web.controller.BaseController;
import com.zrlog.web.interceptor.TemplateHelper;

public class AdminArticlePageController extends BaseController {

    public String preview() {
        Integer logId = getParaToInt("id");
        if (logId != null) {
            Log log = new Log().adminFindLogByLogId(logId);
            log.put("lastLog", Log.dao.findLastLog(logId, I18NUtil.getStringFromRes("noLastLog", getRequest())));
            log.put("nextLog", Log.dao.findNextLog(logId, I18NUtil.getStringFromRes("noNextLog", getRequest())));

            setAttr("log", log.getAttrs());
            TemplateHelper.fillArticleInfo(log, TemplateHelper.setBaseUrl(getRequest(), false, Constants.webSite), "");
            return getTemplatePath() + "/detail";
        } else {
            return Constants.ADMIN_NOT_FOUND_PAGE;
        }
    }

    public String edit() {
        boolean skipFirstRubbishSave = false;
        if (getPara("id") != null) {
            Integer logId = Integer.parseInt(getPara("id"));
            Log log = Log.dao.adminFindLogByLogId(logId);
            if (log != null) {
                setAttr("log", log.getAttrs());
                skipFirstRubbishSave = true;
            }
        }
        setAttr("skipFirstRubbishSave", skipFirstRubbishSave);
        return "/admin/edit";
    }
}
