package com.zrlog.web.controller.admin.page;

import com.hibegin.common.util.FileUtils;
import com.hibegin.common.util.StringUtils;
import com.hibegin.common.util.ZipUtil;
import com.hibegin.common.util.http.HttpUtil;
import com.hibegin.common.util.http.handle.HttpFileHandle;
import com.jfinal.core.JFinal;
import com.jfinal.kit.PathKit;
import com.zrlog.business.service.WebSiteService;
import com.zrlog.common.Constants;
import com.zrlog.common.vo.TemplateVO;
import com.zrlog.model.WebSite;
import com.zrlog.business.service.TemplateService;
import com.zrlog.util.I18nUtil;
import com.zrlog.util.ZrLogUtil;
import com.zrlog.web.annotation.RefreshCache;
import com.zrlog.web.controller.BaseController;
import com.zrlog.web.exception.BadTemplatePathException;
import com.zrlog.web.interceptor.TemplateHelper;
import com.zrlog.web.util.WebTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;
import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.HashMap;

public class AdminTemplatePageController extends BaseController {

    @RefreshCache
    public void preview() {
        String template = getPara("template");
        if (StringUtils.isEmpty(template)) {
            throw new RuntimeException(I18nUtil.getStringFromRes("templatePathNotNull"));
        }
        Cookie cookie = new Cookie("template", template);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        getResponse().addCookie(cookie);
        redirect(WebTools.getHomeUrl(getRequest()));
    }

    public void download() throws IOException {
        String fileName = getRequest().getParameter("templateName");
        String templatePath = fileName.substring(0, fileName.indexOf('.'));
        File path = new File(PathKit.getWebRootPath() + Constants.TEMPLATE_BASE_PATH + templatePath + File.separator);
        if (path.exists()) {
            throw new RemoteException(I18nUtil.getStringFromRes("templateExists"));
        }
        HttpFileHandle fileHandle = (HttpFileHandle) HttpUtil.getInstance().sendGetRequest(getPara("host") + "/template/download?id=" + getParaToInt("id"),
                new HttpFileHandle(PathKit.getWebRootPath() + Constants.TEMPLATE_BASE_PATH), new HashMap<>());
        String target = fileHandle.getT().getParent() + "/" + fileName;
        FileUtils.moveOrCopyFile(fileHandle.getT().toString(), target, true);
        ZipUtil.unZip(target, path.toString() + "/");
        redirect("/admin/website#template");
    }

    @RefreshCache
    public void apply() {
        String template = getPara("template");
        if (template == null) {
            throw new BadTemplatePathException("");
        }
        new WebSite().updateByKV("template", template);
        Cookie cookie = new Cookie("template", "");
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        getResponse().addCookie(cookie);
        redirect(WebTools.getHomeUrl(getRequest()));
    }
}
