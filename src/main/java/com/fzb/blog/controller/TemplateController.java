package com.fzb.blog.controller;

import com.fzb.blog.incp.MyI18NInterceptor;
import com.fzb.blog.model.Link;
import com.fzb.blog.model.WebSite;
import com.fzb.blog.util.ResUtil;
import com.fzb.common.util.IOUtil;
import com.fzb.common.util.ZipUtil;
import com.fzb.common.util.http.HttpUtil;
import com.fzb.common.util.http.handle.HttpFileHandle;
import com.jfinal.kit.PathKit;
import flexjson.JSONSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.Cookie;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public class TemplateController extends ManageController {
    private static final Logger LOGGER = LoggerFactory.getLogger(TemplateController.class);

    public void delete() {
        Link.dao.deleteById(getPara(0));
    }

    public void apply() {
        String template = getPara("template");
        new WebSite().updateByKV("template", template);
        if (getPara("resultType") != null
                && "html".equals(getPara("resultType"))) {
            setAttr("message", ResUtil.getStringFromRes("templateUpdateSuccess", getRequest()));
        } else {
            getData().put("success", true);
            renderJson(getData());
        }
        Cookie cookie = new Cookie("template", "");
        cookie.setPath("/");
        cookie.setMaxAge(0);
        getResponse().addCookie(cookie);
        // 更新缓存数据
        cleanCache();
    }

    public void index() {
        queryAll();
    }

    public void loadFile() {
        String file = getRequest().getRealPath("/") + getPara("file");
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            String fileContent = IOUtil.getStringInputStream(new FileInputStream(file));
            map.put("fileContent", fileContent);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        renderJson(map);
    }

    public void saveFile() {
        String file = getRequest().getRealPath("/") + getPara("file");
        IOUtil.writeBytesToFile(getPara("content").getBytes(), new File(file));
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("status", 200);
        renderJson(map);
    }

    public void queryAll() {
        String webPath = PathKit.getWebRootPath();
        File[] templatesFile = new File(webPath + "/include/templates/")
                .listFiles();
        List<Map<String, Object>> templates = new ArrayList<Map<String, Object>>();
        if (templatesFile != null) {
            for (File file : templatesFile) {
                if (!file.isFile()) {
                    Map<String, Object> map = new HashMap<String, Object>();
                    File templateInfo = new File(file.toString() + "/template.properties");
                    if (templateInfo.exists()) {
                        Properties properties = new Properties();
                        try {
                            properties.load(new FileInputStream(templateInfo));
                            map.put("author", properties.get("author"));
                            map.put("name", properties.get("name"));
                            map.put("digest", properties.get("digest"));
                            map.put("version", properties.get("version"));
                            map.put("url", properties.get("url"));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        map.put("author", "");
                        map.put("name", "");
                        map.put("digest", "");
                        map.put("version", "");
                        map.put("url", "");
                    }
                    map.put("template", file.toString().substring(webPath.length()).replace("\\", "/"));
                    templates.add(map);
                }
            }
        }

        setAttr("templates", templates);
        render("/admin/template.jsp");
    }

    @Override
    public void add() {

    }

    @Override
    public void update() {

    }

    public void download() {
        try {
            String fileName = getRequest().getParameter("templateName");
            String templatePath = fileName.substring(0, fileName.indexOf("."));
            File path = new File(PathKit.getWebRootPath() + "/include/templates/" + templatePath + "/");

            if (!path.exists()) {
                HttpFileHandle fileHandle = (HttpFileHandle) HttpUtil.sendGetRequest(getPara("host") + "/template/download?id=" + getParaToInt("id"),
                        new HttpFileHandle(PathKit.getWebRootPath() + "/include/templates/"), new HashMap<String, String>());
                String target = fileHandle.getT().getParent() + "/" + fileName;
                IOUtil.moveOrCopyFile(fileHandle.getT().toString(), target, true);
                ZipUtil.unZip(target, path.toString() + "/");
                setAttr("message", ResUtil.getStringFromRes("templateDownloadSuccess", getRequest()));
            } else {
                setAttr("message", ResUtil.getStringFromRes("templateExists", getRequest()));
            }
        } catch (Exception e) {
            setAttr("message", ResUtil.getStringFromRes("someError", getRequest()));
            LOGGER.error("download error ", e);
        }
        setAttr("suburl", "template.jsp");
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
            setAttr("message", ResUtil.getStringFromRes("templatePathNotNull", getRequest()));
        }
    }

    public void upload() {
        String uploadFieldName = "file";

        String fileName = getFile(uploadFieldName).getFileName();
        String finalPath = PathKit.getWebRootPath() + "/include/templates/";
        String finalFile = finalPath + fileName;
        IOUtil.moveOrCopyFile(PathKit.getWebRootPath() + "/attached/" + fileName, finalFile, true);
        getData().put("message", ResUtil.getStringFromRes("templateDownloadSuccess", getRequest()));
        try {
            ZipUtil.unZip(finalFile, finalPath + fileName.replace(".zip", "") + "/");
        } catch (IOException e) {
            e.printStackTrace();
        }
        renderJson(getData());
    }

    public void configPage() {
        String templateName = getPara("template");
        setAttr("include", templateName + "/setting/index");
        setAttr("template", templateName);
        setAttr("menu", "1");
        MyI18NInterceptor.addToRequest(PathKit.getWebRootPath() + templateName + "/language/", getRequest());
        String jsonStr = new WebSite().getValueByName(templateName + templateConfigSuffix);
        fullTemplateSetting(jsonStr);
        File file = new File(PathKit.getWebRootPath() + templateName + "/setting/index.jsp");
        if (file.exists()) {
            render("/admin/blank.jsp");
        } else {
            renderNotPage();
        }
    }

    public void setting() {
        Map<String, String[]> param = getRequest().getParameterMap();
        String template = getPara("template");
        Map<String, Object> settingMap = new HashMap<String, Object>();
        for (Map.Entry<String, String[]> entry : param.entrySet()) {
            if (!entry.getKey().equals("template")) {
                if (entry.getValue().length > 1) {
                    settingMap.put(entry.getKey(), entry.getValue());
                } else {
                    settingMap.put(entry.getKey(), entry.getValue()[0]);
                }
            }
        }
        new WebSite().updateByKV(template + templateConfigSuffix, new JSONSerializer().deepSerialize(settingMap));
        cleanCache();
        setAttr("message", "变更成功");
    }

}
