package com.zrlog.common.vo;

import java.util.LinkedHashMap;
import java.util.List;

public class TemplateVO {

    private String template;
    private String url;
    private String version;
    private String name;
    private String digest;
    private boolean deleteAble;
    private List<String> previewImages;
    private String previewImage;
    private String author;
    private boolean configAble;
    private boolean preview;
    private TemplateConfigMap config;
    private String viewType;
    private boolean use;

    public String getViewType() {
        return viewType;
    }

    public void setViewType(String viewType) {
        this.viewType = viewType;
    }

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

    public String getPreviewImage() {
        return previewImage;
    }

    public void setPreviewImage(String previewImage) {
        this.previewImage = previewImage;
    }

    public TemplateConfigMap getConfig() {
        return config;
    }

    public void setConfig(TemplateConfigMap config) {
        this.config = config;
    }

    public static class TemplateConfigMap extends LinkedHashMap<String, TemplateConfigVO> {

    }

    public static class TemplateConfigVO {

        private String label;
        private String htmlElementType;
        private String placeholder;
        private String type;
        private Object value;

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public String getHtmlElementType() {
            return htmlElementType;
        }

        public void setHtmlElementType(String htmlElementType) {
            this.htmlElementType = htmlElementType;
        }

        public String getPlaceholder() {
            return placeholder;
        }

        public void setPlaceholder(String placeholder) {
            this.placeholder = placeholder;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public Object getValue() {
            return value;
        }

        public void setValue(Object value) {
            this.value = value;
        }
    }
}
