package com.fzb.blog.web.controller.admin.page;

import com.fzb.blog.common.Constants;
import com.fzb.blog.model.WebSite;
import com.fzb.blog.service.CacheService;
import com.fzb.blog.util.I18NUtil;
import com.fzb.blog.web.controller.BaseController;
import com.fzb.common.util.IOUtil;
import com.fzb.common.util.ZipUtil;
import com.fzb.common.util.http.HttpUtil;
import com.fzb.common.util.http.handle.HttpFileHandle;
import com.jfinal.kit.PathKit;
import org.apache.log4j.Logger;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class AdminTemplatePageController extends BaseController {

    private static final Logger LOGGER = Logger.getLogger(AdminTemplatePageController.class);

    private CacheService cacheService = new CacheService();

    public String index() {
        String webPath = PathKit.getWebRootPath();
        File[] templatesFile = new File(webPath + Constants.TEMPLATE_BASE_PATH).listFiles();
        List<Map<String, Object>> templates = new ArrayList<Map<String, Object>>();
        HttpServletRequest request = getRequest();
        if (templatesFile != null) {
            for (File file : templatesFile) {
                if (file.isDirectory() && !file.isHidden()) {
                    String templatePath = file.toString().substring(webPath.length()).replace("\\", "/");
                    Map<String, Object> map = new HashMap<String, Object>();
                    File templateInfo = new File(file.toString() + "/template.properties");
                    if (templateInfo.exists()) {
                        Properties properties = new Properties();
                        InputStream in = null;
                        try {
                            in = new FileInputStream(templateInfo);
                            properties.load(in);
                            map.put("author", properties.get("author"));
                            map.put("name", properties.get("name"));
                            map.put("digest", properties.get("digest"));
                            map.put("version", properties.get("version"));
                            map.put("url", properties.get("url"));
                            if (properties.get("previewImages") != null) {
                                String[] images = properties.get("previewImages").toString().split(",");
                                for (int i = 0; i < images.length; i++) {
                                    String image = images[i];
                                    if (!image.startsWith("https://") && !image.startsWith("http://")) {
                                        images[i] = request.getContextPath() + templatePath + "/" + image;
                                    }
                                }
                                map.put("previewImages", images);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                if (in != null) {
                                    in.close();
                                }
                            } catch (IOException e) {
                                LOGGER.error("close stream error ", e);
                            }
                        }
                    } else {
                        map.put("author", "");
                        map.put("name", templatePath.substring(Constants.TEMPLATE_BASE_PATH.length()));
                        map.put("version", "");
                        map.put("url", "");
                    }
                    if (map.get("previewImages") == null || ((String[]) map.get("previewImages")).length == 0) {
                        map.put("previewImages", new String[]{"assets/images/template-default-preview.png"});
                    }
                    if (org.apache.commons.lang3.StringUtils.isEmpty((String) map.get("digest"))) {
                        map.put("digest", "无简介");
                    }
                    map.put("template", templatePath);
                    templates.add(map);
                }
            }
        }

        List<Map<String, Object>> sortTemplates = new ArrayList<Map<String, Object>>();
        for (Map<String, Object> stringObjectMap : templates) {
            if (stringObjectMap.get("template").equals(Constants.DEFAULT_TEMPLATE_PATH)) {
                sortTemplates.add(stringObjectMap);
            }
        }
        for (Map<String, Object> stringObjectMap : templates) {
            if (!stringObjectMap.get("template").equals(Constants.DEFAULT_TEMPLATE_PATH)) {
                sortTemplates.add(stringObjectMap);
            }
        }
        setAttr("templates", sortTemplates);
        setAttr("previewTemplate", getTemplatePathByCookie());
        return "/admin/template";
    }

    public String config() {
        String templateName = getPara("template");
        File file = new File(PathKit.getWebRootPath() + templateName + "/setting/index.jsp");
        if (file.exists()) {
            setAttr("include", templateName + "/setting/index");
            setAttr("template", templateName);
            setAttr("menu", "1");
            I18NUtil.addToRequest(PathKit.getWebRootPath() + templateName + "/language/", this);
            String jsonStr = new WebSite().getValueByName(templateName + templateConfigSuffix);
            fullTemplateSetting(jsonStr);
            return "/admin/blank";
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
            setAttr("message", I18NUtil.getStringFromRes("templatePathNotNull", getRequest()));
        }
    }

    public void download() {
        try {
            String fileName = getRequest().getParameter("templateName");
            String templatePath = fileName.substring(0, fileName.indexOf("."));
            File path = new File(PathKit.getWebRootPath() + Constants.TEMPLATE_BASE_PATH + templatePath + "/");

            if (!path.exists()) {
                HttpFileHandle fileHandle = (HttpFileHandle) HttpUtil.getInstance().sendGetRequest(getPara("host") + "/template/download?id=" + getParaToInt("id"),
                        new HttpFileHandle(PathKit.getWebRootPath() + Constants.TEMPLATE_BASE_PATH), new HashMap<String, String>());
                String target = fileHandle.getT().getParent() + "/" + fileName;
                IOUtil.moveOrCopyFile(fileHandle.getT().toString(), target, true);
                ZipUtil.unZip(target, path.toString() + "/");
                setAttr("message", I18NUtil.getStringFromRes("templateDownloadSuccess", getRequest()));
            } else {
                setAttr("message", I18NUtil.getStringFromRes("templateExists", getRequest()));
            }
        } catch (Exception e) {
            setAttr("message", I18NUtil.getStringFromRes("someError", getRequest()));
            LOGGER.error("download error ", e);
        }
    }
}
