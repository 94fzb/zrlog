package com.zrlog.web.controller.admin.page;

import com.google.gson.Gson;
import com.hibegin.common.util.StringUtils;
import com.zrlog.common.Constants;
import com.zrlog.model.Log;
import com.zrlog.util.I18NUtil;
import com.zrlog.web.controller.BaseController;
import com.zrlog.web.interceptor.TemplateHelper;

import java.util.HashMap;
import java.util.Map;

public class AdminArticlePageController extends BaseController {

    public String preview() {
        Integer logId = getParaToInt("id");
        if (logId != null) {
            Log log = new Log().adminFindLogByLogId(logId);
            if (log != null) {
                log.put("lastLog", Log.dao.findLastLog(logId, I18NUtil.getStringFromRes("noLastLog")));
                log.put("nextLog", Log.dao.findNextLog(logId, I18NUtil.getStringFromRes("noNextLog")));
                setAttr("log", log.getAttrs());
                TemplateHelper.fillArticleInfo(log, TemplateHelper.setBaseUrl(getRequest(), false, Constants.webSite), "");
            }
            return getTemplatePath() + "/detail";
        } else {
            return Constants.NOT_FOUND_PAGE;
        }
    }

    public String edit() {
        boolean skipFirstRubbishSave = false;
        String editorType = "";
        Map<String, String> articleContent = new HashMap<>();
        if (getPara("id") != null) {
            Integer logId = Integer.parseInt(getPara("id"));
            Log log = Log.dao.adminFindLogByLogId(logId);
            if (log != null) {
                setAttr("log", log.getAttrs());
                articleContent.put("markdown", log.getStr("markdown"));
                articleContent.put("content", log.getStr("content"));
                if (StringUtils.isNotEmpty(log.getStr("editor_type"))) {
                    editorType = log.getStr("editor_type");
                } else {
                    editorType = "markdown";
                }
                skipFirstRubbishSave = true;
            }
        }
        if (StringUtils.isEmpty(editorType)) {
            if (Constants.webSite.get("editor_type") != null && StringUtils.isNotEmpty(Constants.webSite.get("editor_type").toString())) {
                editorType = Constants.webSite.get("editor_type").toString();
            } else {
                editorType = "markdown";
            }
        }
        articleContent.put("editorType", editorType);
        setAttr("article", new Gson().toJson(articleContent));
        setAttr("skipFirstRubbishSave", skipFirstRubbishSave);
        return "/admin/article_edit";
    }
}
