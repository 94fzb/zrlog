package com.zrlog.web.controller.admin.page;

import com.hibegin.common.util.FileUtils;
import com.hibegin.common.util.ZipUtil;
import com.hibegin.common.util.http.HttpUtil;
import com.hibegin.common.util.http.handle.HttpFileHandle;
import com.jfinal.core.JFinal;
import com.jfinal.kit.PathKit;
import com.zrlog.common.Constants;
import com.zrlog.model.WebSite;
import com.zrlog.service.TemplateService;
import com.zrlog.util.I18NUtil;
import com.zrlog.web.controller.BaseController;
import com.zrlog.web.interceptor.TemplateHelper;
import org.apache.log4j.Logger;

import javax.servlet.http.Cookie;
import java.io.File;
import java.util.HashMap;

public class AdminTemplatePageController extends BaseController {

    private static final Logger LOGGER = Logger.getLogger(AdminTemplatePageController.class);

    private TemplateService templateService = new TemplateService();

    public String index() {
        setAttr("templates", templateService.getAllTemplates(getRequest().getContextPath()));
        setAttr("previewTemplate", TemplateHelper.getTemplatePathByCookie(getRequest().getCookies()));
        return "/admin/template";
    }

    public String config() {
        String templateName = getPara("template");
        File file = new File(PathKit.getWebRootPath() + templateName + "/setting/index.jsp");
        if (file.exists()) {
            setAttr("include", templateName + "/setting/index");
            setAttr("template", templateName);
            setAttr("templateInfo", templateService.getTemplateVO(JFinal.me().getContextPath(), file.getParentFile().getParentFile()));
            I18NUtil.addToRequest(PathKit.getWebRootPath() + templateName + "/language/", getRequest(), JFinal.me().getConstants().getDevMode());
            String jsonStr = new WebSite().getStringValueByName(templateName + Constants.TEMPLATE_CONFIG_SUFFIX);
            fullTemplateSetting(jsonStr);
            return "/admin/template_config";
        } else {
            return Constants.ADMIN_NOT_FOUND_PAGE;
        }
    }

    public void preview() {
        String template = getPara("template");
        if (template != null) {
            Cookie cookie = new Cookie("template", template);
            cookie.setPath("/");
            getResponse().addCookie(cookie);
            String path = getRequest().getContextPath();
            String basePath = getRequest().getScheme() + "://" + getRequest().getHeader("host") + path + "/";
            redirect(basePath);
        } else {
            setAttr("message", I18NUtil.getStringFromRes("templatePathNotNull"));
        }
    }

    public void download() {
        try {
            String fileName = getRequest().getParameter("templateName");
            String templatePath = fileName.substring(0, fileName.indexOf('.'));
            File path = new File(PathKit.getWebRootPath() + Constants.TEMPLATE_BASE_PATH + templatePath + "/");

            if (!path.exists()) {
                HttpFileHandle fileHandle = (HttpFileHandle) HttpUtil.getInstance().sendGetRequest(getPara("host") + "/template/download?id=" + getParaToInt("id"),
                        new HttpFileHandle(PathKit.getWebRootPath() + Constants.TEMPLATE_BASE_PATH), new HashMap<String, String>());
                String target = fileHandle.getT().getParent() + "/" + fileName;
                FileUtils.moveOrCopyFile(fileHandle.getT().toString(), target, true);
                ZipUtil.unZip(target, path.toString() + "/");
                setAttr("message", I18NUtil.getStringFromRes("templateDownloadSuccess"));
            } else {
                setAttr("message", I18NUtil.getStringFromRes("templateExists"));
            }
        } catch (Exception e) {
            LOGGER.error("download error ", e);
            setAttr("message", I18NUtil.getStringFromRes("someError"));
        }
    }
}
