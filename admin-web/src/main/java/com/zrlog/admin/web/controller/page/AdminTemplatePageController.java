package com.zrlog.admin.web.controller.page;

import com.hibegin.common.util.FileUtils;
import com.hibegin.common.util.ZipUtil;
import com.hibegin.common.util.http.HttpUtil;
import com.hibegin.common.util.http.handle.HttpFileHandle;
import com.hibegin.http.server.util.PathUtil;
import com.hibegin.http.server.web.Controller;
import com.zrlog.admin.business.exception.ArgsException;
import com.zrlog.admin.business.exception.TemplateExistsException;
import com.zrlog.common.Constants;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;

public class AdminTemplatePageController extends Controller {

    public void download() throws IOException, URISyntaxException, InterruptedException {
        String fileName = getRequest().getParaToStr("templateName");
        String templatePath = fileName.substring(0, fileName.indexOf('.'));
        File path = new File(PathUtil.getStaticPath() + Constants.TEMPLATE_BASE_PATH + templatePath + File.separator);
        if (path.exists()) {
            throw new TemplateExistsException();
        }
        String downloadHost = request.getParaToStr("host");
        if (!Constants.TEMPLATE_REPO_HOSTS.contains(downloadHost)) {
            throw new ArgsException();
        }
        HttpFileHandle fileHandle = (HttpFileHandle) HttpUtil.getInstance().sendGetRequest(downloadHost +
                        "/template/download?id=" + request.getParaToInt("id"),
                new HttpFileHandle(PathUtil.getStaticPath() + Constants.TEMPLATE_BASE_PATH), new HashMap<>());
        String target = fileHandle.getT().getParent() + "/" + fileName;
        FileUtils.moveOrCopyFile(fileHandle.getT().toString(), target, true);
        ZipUtil.unZip(target, path + "/");
        response.redirect(Constants.ADMIN_URI_BASE_PATH + "/website#template");
    }


}
