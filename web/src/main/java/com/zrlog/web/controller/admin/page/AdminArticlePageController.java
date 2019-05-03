package com.zrlog.web.controller.admin.page;

import com.google.gson.Gson;
import com.hibegin.common.util.StringUtils;
import com.jfinal.core.JFinal;
import com.jfinal.kit.PathKit;
import com.zrlog.common.Constants;
import com.zrlog.model.Log;
import com.zrlog.service.TemplateService;
import com.zrlog.util.I18nUtil;
import com.zrlog.util.ZrLogUtil;
import com.zrlog.web.controller.BaseController;
import com.zrlog.web.interceptor.TemplateHelper;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class AdminArticlePageController extends BaseController {

    public String preview() {
        Integer logId = getParaToInt("id");
        if (logId != null) {
            Log log = new Log().adminFindLogByLogId(logId);
            if (log != null) {
                log.put("lastLog", new Log().findLastLog(logId, I18nUtil.getStringFromRes("noLastLog")));
                log.put("nextLog", new Log().findNextLog(logId, I18nUtil.getStringFromRes("noNextLog")));
                setAttr("log", log.getAttrs());
                TemplateHelper.fillArticleInfo(log, getRequest(), "");
            }
            return getTemplatePath() + "/detail" + ZrLogUtil.getViewExt(new TemplateService().getTemplateVO(JFinal.me().getContextPath(),
                    new File(PathKit.getWebRootPath() + getTemplatePath())).getViewType());
        } else {
            return Constants.NOT_FOUND_PAGE;
        }
    }

    public String edit() {
        boolean skipFirstRubbishSave = false;
        String editorType = "";
        Map<String, Object> articleContent = new HashMap<>();
        if (getPara("id") != null) {
            Integer logId = Integer.parseInt(getPara("id"));
            Log log = new Log().adminFindLogByLogId(logId);
            if (log != null) {
                setAttr("log", log.getAttrs());
                articleContent.putAll(log.getAttrs());
                if (StringUtils.isNotEmpty(log.getStr("editor_type"))) {
                    editorType = log.getStr("editor_type");
                } else {
                    editorType = "markdown";
                }
                skipFirstRubbishSave = true;
            }
        }
        if (StringUtils.isEmpty(editorType)) {
            if (Constants.WEB_SITE.get("editor_type") != null && StringUtils.isNotEmpty(Constants.WEB_SITE.get("editor_type").toString())) {
                editorType = Constants.WEB_SITE.get("editor_type").toString();
            } else {
                editorType = "markdown";
            }
        }
        articleContent.put("editorType", editorType);
        setAttr("article", new Gson().toJson(articleContent));
        setAttr("skipFirstRubbishSave", skipFirstRubbishSave);
        setAttr("post", Constants.getArticleUri());
        return "/admin/article_edit";
    }
}
