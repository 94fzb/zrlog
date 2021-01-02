package com.zrlog.admin.web.controller.page;

import com.hibegin.common.util.FileUtils;
import com.hibegin.common.util.StringUtils;
import com.hibegin.common.util.ZipUtil;
import com.hibegin.common.util.http.HttpUtil;
import com.hibegin.common.util.http.handle.HttpFileHandle;
import com.jfinal.core.Controller;
import com.jfinal.kit.PathKit;
import com.zrlog.admin.business.exception.BadTemplatePathException;
import com.zrlog.admin.business.exception.TemplateExistsException;
import com.zrlog.admin.business.exception.TemplatePathNotNullException;
import com.zrlog.admin.web.annotation.RefreshCache;
import com.zrlog.blog.web.util.WebTools;
import com.zrlog.common.Constants;
import com.zrlog.model.WebSite;

import javax.servlet.http.Cookie;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class AdminTemplatePageController extends Controller {

    @RefreshCache
    public void preview() {
        String template = getPara("template");
        if (StringUtils.isEmpty(template)) {
            throw new TemplatePathNotNullException();
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
            throw new TemplateExistsException();
        }
        HttpFileHandle fileHandle = (HttpFileHandle) HttpUtil.getInstance().sendGetRequest(getPara("host") +
                "/template/download?id=" + getParaToInt("id"),
                new HttpFileHandle(PathKit.getWebRootPath() + Constants.TEMPLATE_BASE_PATH), new HashMap<>());
        String target = fileHandle.getT().getParent() + "/" + fileName;
        FileUtils.moveOrCopyFile(fileHandle.getT().toString(), target, true);
        ZipUtil.unZip(target, path.toString() + "/");
        redirect(Constants.ADMIN_URI_BASE_PATH + "/website#template");
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
