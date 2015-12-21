package com.fzb.blog.controlle;

import com.fzb.blog.model.Link;
import com.fzb.common.util.HttpUtil;
import com.fzb.common.util.IOUtil;
import com.fzb.common.util.ResponseData;
import com.fzb.common.util.ZipUtil;
import com.jfinal.kit.PathKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TemplateController extends ManageController {
    private static final Logger LOGGER = LoggerFactory.getLogger(TemplateController.class);

    public void delete() {
        Link.dao.deleteById(getPara(0));
    }

    public void apply() {

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
            for (File aTemplatesFile : templatesFile) {
                if (aTemplatesFile.isFile())
                    continue;
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("template",
                        aTemplatesFile.toString().substring(webPath.length())
                                .replace("\\", "/"));
                map.put("author", "xiaochun");
                map.put("name", "模板");
                map.put("digest", "这个是模板雅");
                map.put("version", "1.0");
                templates.add(map);
            }
        }

        setAttr("templates", templates);
        render("/admin/template.jsp");
        // renderJson(Tag.dao.queryAll(getParaToInt("page"),getParaToInt("rows")));
    }

    @Override
    public void add() {

    }

    @Override
    public void update() {

    }

    public void download() {
        ResponseData<File> data = new ResponseData<File>() {
        };
        try {
            HttpUtil.getResponse(getPara("host") + "/template/download?id="
                    + getParaToInt("id"), data, PathKit.getWebRootPath()
                    + "/include/templates/");
            String folerName = data.getT().getName().substring(0, data.getT().getName().indexOf("."));
            ZipUtil.unZip(data.getT().toString(), PathKit.getWebRootPath() + "/include/templates/" + folerName + "/");
        } catch (Exception e) {
            LOGGER.error("download error ", e);
        }
        setAttr("message", "下载模板成功");
    }

}
