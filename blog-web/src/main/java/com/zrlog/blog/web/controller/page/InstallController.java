package com.zrlog.blog.web.controller.page;

import com.google.gson.Gson;
import com.hibegin.common.util.IOUtil;
import com.jfinal.core.Controller;
import com.jfinal.kit.PathKit;
import com.jfinal.render.HtmlRender;
import com.zrlog.business.service.CommonService;
import com.zrlog.common.Constants;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.Objects;

/**
 * 与安装向导相关的路由进行控制
 * 注意 install.lock 文件相当重要，如果不是重新安装请不要删除这个自动生成的文件
 */
public class InstallController extends Controller {


    /**
     * 加载安装向导第一个页面数据
     */
    public void index() throws FileNotFoundException {
        if (getRequest().getRequestURI().endsWith("/install")) {
            redirect("/install/");
            return;
        }
        File file = new File(PathKit.getWebRootPath() + "/install/index.html");
        if (!file.exists()) {
            renderError(404);
            return;
        }
        Document document = Jsoup.parse(IOUtil.getStringInputStream(new FileInputStream(file)));
        //clean history
        document.body().removeClass("dark");
        document.body().removeClass("light");
        document.selectFirst("base").attr("href", getRequest().getContextPath() + "/");
        Map<String, Object> stringObjectMap = new CommonService().installResourceInfo(getRequest());
        Objects.requireNonNull(document.getElementById("resourceInfo")).text(new Gson().toJson(stringObjectMap));
        document.title(String.valueOf(stringObjectMap.get("installWizard")));
        render(new HtmlRender(document.html()));
    }
}
