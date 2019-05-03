package com.zrlog.web.controller.admin.page;

import com.hibegin.common.util.FileUtils;
import com.hibegin.common.util.ZipUtil;
import com.hibegin.common.util.http.HttpUtil;
import com.hibegin.common.util.http.handle.HttpFileHandle;
import com.jfinal.core.JFinal;
import com.jfinal.kit.PathKit;
import com.zrlog.common.Constants;
import com.zrlog.common.vo.TemplateVO;
import com.zrlog.model.WebSite;
import com.zrlog.service.TemplateService;
import com.zrlog.util.I18nUtil;
import com.zrlog.util.ZrLogUtil;
import com.zrlog.web.controller.BaseController;
import com.zrlog.web.interceptor.TemplateHelper;
import com.zrlog.web.util.WebTools;
import org.apache.log4j.Logger;

import javax.servlet.http.Cookie;
import java.io.File;
import java.util.HashMap;

public class AdminTemplatePageController extends BaseController {

    private static final Logger LOGGER = Logger.getLogger(AdminTemplatePageController.class);

    private TemplateService templateService = new TemplateService();

    public String index() {
        setAttr("templates", templateService.getAllTemplates(getRequest().getContextPath(), TemplateHelper.getTemplatePathByCookie(getRequest().getCookies())));
        return "/admin/template";
    }

    public String config() {
        String templateName = getPara("template");
        setAttr("templateInfo", templateService.getTemplateVO(JFinal.me().getContextPath(), new File(PathKit.getWebRootPath() + templateName)));
        return "/admin/template_config";
    }

    public String configPage() {
        String templateName = getPara("template");
        setAttr("template", templateName);
        TemplateVO templateVO = templateService.getTemplateVO(JFinal.me().getContextPath(), new File(PathKit.getWebRootPath() + templateName));
        setAttr("templateInfo", templateVO);
        I18nUtil.addToRequest(PathKit.getWebRootPath() + templateName + "/language/", getRequest(), JFinal.me().getConstants().getDevMode(), true);
        String jsonStr = new WebSite().getStringValueByName(templateName + Constants.TEMPLATE_CONFIG_SUFFIX);
        fullTemplateSetting(jsonStr);
        return templateName + "/setting/index" + ZrLogUtil.getViewExt(templateVO.getViewType());
    }

    public void preview() {
        String template = getPara("template");
        if (template != null) {
            Cookie cookie = new Cookie("template", template);
            cookie.setPath("/");
            getResponse().addCookie(cookie);
            redirect(WebTools.getHomeUrl(getRequest()));
        } else {
            setAttr("message", I18nUtil.getStringFromRes("templatePathNotNull"));
        }
    }

    public void download() {
        try {
            String fileName = getRequest().getParameter("templateName");
            String templatePath = fileName.substring(0, fileName.indexOf('.'));
            File path = new File(PathKit.getWebRootPath() + Constants.TEMPLATE_BASE_PATH + templatePath + File.separator);

            if (!path.exists()) {
                HttpFileHandle fileHandle = (HttpFileHandle) HttpUtil.getInstance().sendGetRequest(getPara("host") + "/template/download?id=" + getParaToInt("id"),
                        new HttpFileHandle(PathKit.getWebRootPath() + Constants.TEMPLATE_BASE_PATH), new HashMap<>());
                String target = fileHandle.getT().getParent() + "/" + fileName;
                FileUtils.moveOrCopyFile(fileHandle.getT().toString(), target, true);
                ZipUtil.unZip(target, path.toString() + "/");
                setAttr("message", I18nUtil.getStringFromRes("templateDownloadSuccess"));
                setAttr("backUrl", "admin/index#website");
            } else {
                setAttr("message", I18nUtil.getStringFromRes("templateExists"));
            }
        } catch (Exception e) {
            LOGGER.error("download error ", e);
            setAttr("message", I18nUtil.getStringFromRes("someError"));
        }
    }
}
