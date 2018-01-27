package com.zrlog.web.controller.admin.page;

import com.hibegin.common.util.FileUtils;
import com.hibegin.common.util.StringUtils;
import com.hibegin.common.util.ZipUtil;
import com.hibegin.common.util.http.HttpUtil;
import com.hibegin.common.util.http.handle.HttpFileHandle;
import com.jfinal.kit.PathKit;
import com.zrlog.common.Constants;
import com.zrlog.common.vo.TemplateVO;
import com.zrlog.model.WebSite;
import com.zrlog.util.I18NUtil;
import com.zrlog.web.controller.BaseController;
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

    public String index() {
        String webPath = PathKit.getWebRootPath();
        File[] templatesFile = new File(webPath + Constants.TEMPLATE_BASE_PATH).listFiles();
        List<TemplateVO> templates = new ArrayList<>();
        HttpServletRequest request = getRequest();
        if (templatesFile != null) {
            for (File file : templatesFile) {
                if (file.isDirectory() && !file.isHidden()) {
                    String templatePath = file.toString().substring(webPath.length()).replace("\\", "/");
                    TemplateVO templateVO = new TemplateVO();
                    File templateInfo = new File(file.toString() + "/template.properties");
                    if (templateInfo.exists()) {
                        Properties properties = new Properties();
                        try (InputStream in = new FileInputStream(templateInfo)) {
                            properties.load(in);
                            templateVO.setAuthor(properties.getProperty("author"));
                            templateVO.setName(properties.getProperty("name"));
                            templateVO.setDigest(properties.getProperty("digest"));
                            templateVO.setVersion(properties.getProperty("version"));
                            templateVO.setUrl(properties.getProperty("url"));
                            if (properties.get("previewImages") != null) {
                                String[] images = properties.get("previewImages").toString().split(",");
                                for (int i = 0; i < images.length; i++) {
                                    String image = images[i];
                                    if (!image.startsWith("https://") && !image.startsWith("http://")) {
                                        images[i] = request.getContextPath() + templatePath + "/" + image;
                                    }
                                }
                                templateVO.setPreviewImages(Arrays.asList(images));
                            }
                        } catch (IOException e) {
                            LOGGER.error("", e);
                        }
                    } else {
                        templateVO.setAuthor("");
                        templateVO.setName(templatePath.substring(Constants.TEMPLATE_BASE_PATH.length()));
                        templateVO.setUrl("");
                        templateVO.setVersion("");
                    }
                    if (templateVO.getPreviewImages() == null || templateVO.getPreviewImages().isEmpty()) {
                        templateVO.setPreviewImages(Collections.singletonList("assets/images/template-default-preview.jpg"));
                    }
                    if (StringUtils.isEmpty(templateVO.getDigest())) {
                        templateVO.setDigest("无简介");
                    }
                    templateVO.setTemplate(templatePath);
                    templates.add(templateVO);
                }
            }
        }

        List<TemplateVO> sortTemplates = new ArrayList<>();
        for (TemplateVO templateVO : templates) {
            if (templateVO.getTemplate().equals(Constants.DEFAULT_TEMPLATE_PATH)) {
                templateVO.setDeleteAble(false);
                sortTemplates.add(templateVO);
            } else {
                templateVO.setDeleteAble(true);
            }
        }
        for (TemplateVO templateVO : templates) {
            if (!templateVO.getTemplate().equals(Constants.DEFAULT_TEMPLATE_PATH)) {
                sortTemplates.add(templateVO);
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
            String jsonStr = new WebSite().getStringValueByName(templateName + templateConfigSuffix);
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
            String templatePath = fileName.substring(0, fileName.indexOf('.'));
            File path = new File(PathKit.getWebRootPath() + Constants.TEMPLATE_BASE_PATH + templatePath + "/");

            if (!path.exists()) {
                HttpFileHandle fileHandle = (HttpFileHandle) HttpUtil.getInstance().sendGetRequest(getPara("host") + "/template/download?id=" + getParaToInt("id"),
                        new HttpFileHandle(PathKit.getWebRootPath() + Constants.TEMPLATE_BASE_PATH), new HashMap<String, String>());
                String target = fileHandle.getT().getParent() + "/" + fileName;
                FileUtils.moveOrCopyFile(fileHandle.getT().toString(), target, true);
                ZipUtil.unZip(target, path.toString() + "/");
                setAttr("message", I18NUtil.getStringFromRes("templateDownloadSuccess", getRequest()));
            } else {
                setAttr("message", I18NUtil.getStringFromRes("templateExists", getRequest()));
            }
        } catch (Exception e) {
            LOGGER.error("download error ", e);
            setAttr("message", I18NUtil.getStringFromRes("someError", getRequest()));
        }
    }
}
