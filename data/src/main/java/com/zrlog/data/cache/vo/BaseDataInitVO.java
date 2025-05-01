package com.zrlog.data.cache.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 程序初始化的数据（及全局的数据），存放了标签，分类，网站设置等信息。
 */
public class BaseDataInitVO implements Serializable {

    public BaseDataInitVO() {
    }

    private Long webSiteVersion;
    private List<Map<String, Object>> tags = new ArrayList<>();
    private List<Map<String, Object>> types = new ArrayList<>();
    private List<Map<String, Object>> links = new ArrayList<>();
    private List<Map<String, Object>> plugins = new ArrayList<>();
    private Map<String, Long> archives = new HashMap<>();
    private List<Archive> archiveList = new ArrayList<>();
    private Map<String, Object> webSite = new HashMap<>();
    private List<HotLogBasicInfoEntry> hotLogs = new ArrayList<>();
    private List<Map<String, Object>> logNavs = new ArrayList<>();
    private List<HotTypeLogInfo> typeHotLogs;
    private Statistics statistics = new Statistics();
    private Long version;

    public List<Map<String, Object>> getTags() {
        return tags;
    }

    public void setTags(List<Map<String, Object>> tags) {
        this.tags = tags;
    }

    public List<Map<String, Object>> getTypes() {
        return types;
    }

    public void setTypes(List<Map<String, Object>> types) {
        this.types = types;
    }

    public List<Map<String, Object>> getLinks() {
        return links;
    }

    public void setLinks(List<Map<String, Object>> links) {
        this.links = links;
    }

    public List<Map<String, Object>> getPlugins() {
        return plugins;
    }

    public void setPlugins(List<Map<String, Object>> plugins) {
        this.plugins = plugins;
    }

    public Map<String, Long> getArchives() {
        return archives;
    }

    public void setArchives(Map<String, Long> archives) {
        this.archives = archives;
    }

    public List<Archive> getArchiveList() {
        return archiveList;
    }

    public void setArchiveList(List<Archive> archiveList) {
        this.archiveList = archiveList;
    }

    public Map<String, Object> getWebSite() {
        return webSite;
    }

    public void setWebSite(Map<String, Object> webSite) {
        this.webSite = webSite;
    }

    public List<HotLogBasicInfoEntry> getHotLogs() {
        return hotLogs;
    }

    public void setHotLogs(List<HotLogBasicInfoEntry> hotLogs) {
        this.hotLogs = hotLogs;
    }

    public List<Map<String, Object>> getLogNavs() {
        return logNavs;
    }

    public void setLogNavs(List<Map<String, Object>> logNavs) {
        this.logNavs = logNavs;
    }

    public List<HotTypeLogInfo> getTypeHotLogs() {
        return typeHotLogs;
    }

    public void setTypeHotLogs(List<HotTypeLogInfo> typeHotLogs) {
        this.typeHotLogs = typeHotLogs;
    }

    public Statistics getStatistics() {
        return statistics;
    }

    public void setStatistics(Statistics statistics) {
        this.statistics = statistics;
    }

    public static class Statistics implements Serializable {

        public Statistics() {
        }

        private Long totalArticleSize;
        private Long totalTagSize;
        private Long totalTypeSize;

        public Long getTotalArticleSize() {
            return totalArticleSize;
        }

        public void setTotalArticleSize(Long totalArticleSize) {
            this.totalArticleSize = totalArticleSize;
        }

        public Long getTotalTagSize() {
            return totalTagSize;
        }

        public void setTotalTagSize(Long totalTagSize) {
            this.totalTagSize = totalTagSize;
        }

        public Long getTotalTypeSize() {
            return totalTypeSize;
        }

        public void setTotalTypeSize(Long totalTypeSize) {
            this.totalTypeSize = totalTypeSize;
        }
    }

    public Long getWebSiteVersion() {
        return webSiteVersion;
    }

    public void setWebSiteVersion(Long webSiteVersion) {
        this.webSiteVersion = webSiteVersion;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
