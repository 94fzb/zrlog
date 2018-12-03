package com.zrlog.common.vo;

import java.util.List;

public class TemplateVO {

    private String template;
    private String url;
    private String version;
    private String name;
    private String digest;
    private boolean deleteAble;
    private List<String> previewImages;
    private String author;
    private boolean configAble;
    private boolean preview;

    public String getViewType() {
        return viewType;
    }

    public void setViewType(String viewType) {
        this.viewType = viewType;
    }

    private String viewType;

    public boolean isPreview() {
        return preview;
    }

    public void setPreview(boolean preview) {
        this.preview = preview;
    }

    public boolean isUse() {
        return use;
    }

    public void setUse(boolean use) {
        this.use = use;
    }

    private boolean use;


    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public boolean isDeleteAble() {
        return deleteAble;
    }

    public void setDeleteAble(boolean deleteAble) {
        this.deleteAble = deleteAble;
    }

    public List<String> getPreviewImages() {
        return previewImages;
    }

    public void setPreviewImages(List<String> previewImages) {
        this.previewImages = previewImages;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDigest() {
        return digest;
    }

    public void setDigest(String digest) {
        this.digest = digest;
    }

    public boolean isConfigAble() {
        return configAble;
    }

    public void setConfigAble(boolean configAble) {
        this.configAble = configAble;
    }
}
