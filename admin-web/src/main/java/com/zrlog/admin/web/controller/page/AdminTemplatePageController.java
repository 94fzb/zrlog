package com.zrlog.admin.web.controller.page;

import com.hibegin.common.util.FileUtils;
import com.hibegin.common.util.ZipUtil;
import com.hibegin.common.util.http.HttpUtil;
import com.hibegin.common.util.http.handle.HttpFileHandle;
import com.jfinal.core.Controller;
import com.jfinal.kit.PathKit;
import com.zrlog.admin.business.exception.ArgsException;
import com.zrlog.admin.business.exception.TemplateExistsException;
import com.zrlog.common.Constants;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class AdminTemplatePageController extends Controller {

    public void download() throws IOException {
        String fileName = getRequest().getParameter("templateName");
        String templatePath = fileName.substring(0, fileName.indexOf('.'));
        File path = new File(PathKit.getWebRootPath() + Constants.TEMPLATE_BASE_PATH + templatePath + File.separator);
        if (path.exists()) {
            throw new TemplateExistsException();
        }
        String downloadHost = getPara("host");
        if (!Constants.TEMPLATE_REPO_HOSTS.contains(downloadHost)) {
            throw new ArgsException();
        }
        HttpFileHandle fileHandle = (HttpFileHandle) HttpUtil.getInstance().sendGetRequest(downloadHost +
                        "/template/download?id=" + getParaToInt("id"),
                new HttpFileHandle(PathKit.getWebRootPath() + Constants.TEMPLATE_BASE_PATH), new HashMap<>());
        String target = fileHandle.getT().getParent() + "/" + fileName;
        FileUtils.moveOrCopyFile(fileHandle.getT().toString(), target, true);
        ZipUtil.unZip(target, path + "/");
        redirect(Constants.ADMIN_URI_BASE_PATH + "/website#template");
    }


}
