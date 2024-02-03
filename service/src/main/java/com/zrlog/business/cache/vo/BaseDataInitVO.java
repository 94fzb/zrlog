package com.zrlog.business.cache.vo;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 程序初始化的数据（及全局的数据），存放了标签，分类，网站设置等信息。
 */
public class BaseDataInitVO implements Serializable {

    private List<Map<String, Object>> tags;
    private List<Map<String, Object>> types;
    private List<Map<String, Object>> links;
    private List<Map<String, Object>> plugins;
    private Map<String, Long> archives;
    private List<Archive> archiveList;
    private Map<String, Object> webSite;
    private List<Map<String, Object>> hotLogs;
    private List<Map<String, Object>> logNavs;
    private Map<Map<String, Object>, List<Map<String, Object>>> indexHotLogs;
    private Statistics statistics;

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

    public List<Map<String, Object>> getHotLogs() {
        return hotLogs;
    }

    public void setHotLogs(List<Map<String, Object>> hotLogs) {
        this.hotLogs = hotLogs;
    }

    public List<Map<String, Object>> getLogNavs() {
        return logNavs;
    }

    public void setLogNavs(List<Map<String, Object>> logNavs) {
        this.logNavs = logNavs;
    }

    public Map<Map<String, Object>, List<Map<String, Object>>> getIndexHotLogs() {
        return indexHotLogs;
    }

    public void setIndexHotLogs(Map<Map<String, Object>, List<Map<String, Object>>> indexHotLogs) {
        this.indexHotLogs = indexHotLogs;
    }

    public Statistics getStatistics() {
        return statistics;
    }

    public void setStatistics(Statistics statistics) {
        this.statistics = statistics;
    }

    public static class Statistics implements Serializable {

        private long totalArticleSize;
        private long totalTagSize;
        private long totalTypeSize;

        public long getTotalArticleSize() {
            return totalArticleSize;
        }

        public void setTotalArticleSize(long totalArticleSize) {
            this.totalArticleSize = totalArticleSize;
        }

        public long getTotalTagSize() {
            return totalTagSize;
        }

        public void setTotalTagSize(long totalTagSize) {
            this.totalTagSize = totalTagSize;
        }

        public long getTotalTypeSize() {
            return totalTypeSize;
        }

        public void setTotalTypeSize(long totalTypeSize) {
            this.totalTypeSize = totalTypeSize;
        }
    }
}
