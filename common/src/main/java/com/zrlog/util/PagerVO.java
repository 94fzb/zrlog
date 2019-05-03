package com.zrlog.util;

import java.util.List;

public class PagerVO {

    private List<PageEntry> pageList;
    private String pageStartUrl;
    private String pageEndUrl;
    private Boolean startPage;
    private Boolean endPage;

    public List<PageEntry> getPageList() {
        return pageList;
    }

    public void setPageList(List<PageEntry> pageList) {
        this.pageList = pageList;
    }

    public String getPageStartUrl() {
        return pageStartUrl;
    }

    public void setPageStartUrl(String pageStartUrl) {
        this.pageStartUrl = pageStartUrl;
    }

    public String getPageEndUrl() {
        return pageEndUrl;
    }

    public void setPageEndUrl(String pageEndUrl) {
        this.pageEndUrl = pageEndUrl;
    }

    public Boolean getStartPage() {
        return startPage;
    }

    public void setStartPage(Boolean startPage) {
        this.startPage = startPage;
    }

    public Boolean getEndPage() {
        return endPage;
    }

    public void setEndPage(Boolean endPage) {
        this.endPage = endPage;
    }

    public static class PageEntry {
        private String url;
        private Boolean current;
        private String desc;

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public Boolean getCurrent() {
            return current;
        }

        public void setCurrent(Boolean current) {
            this.current = current;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }
    }

}
