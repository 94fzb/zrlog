package com.fzb.blog.common;

import com.fzb.blog.model.*;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class BaseDataInitVO implements Serializable {

    private List<Tag> tags;
    private List<Type> types;
    private List<Link> links;
    private List<Plugin> plugins;
    private Map<String, Long> archives;
    private List<Archive> archiveList;
    private Map<String, Object> webSite;
    private List<Log> hotLogs;
    private List<LogNav> logNavs;
    private Map<Map<String, Object>, List<Log>> indexHotLogs;

    public List<Tag> getTags() {
        return tags;
    }

    public void setTags(List<Tag> tags) {
        this.tags = tags;
    }

    public List<Type> getTypes() {
        return types;
    }

    public void setTypes(List<Type> types) {
        this.types = types;
    }

    public List<Link> getLinks() {
        return links;
    }

    public void setLinks(List<Link> links) {
        this.links = links;
    }

    public List<Plugin> getPlugins() {
        return plugins;
    }

    public void setPlugins(List<Plugin> plugins) {
        this.plugins = plugins;
    }

    public Map<String, Long> getArchives() {
        return archives;
    }

    public void setArchives(Map<String, Long> archives) {
        this.archives = archives;
    }

    public Map<String, Object> getWebSite() {
        return webSite;
    }

    public void setWebSite(Map<String, Object> webSite) {
        this.webSite = webSite;
    }

    public List<Log> getHotLogs() {
        return hotLogs;
    }

    public void setHotLogs(List<Log> hotLogs) {
        this.hotLogs = hotLogs;
    }

    public List<LogNav> getLogNavs() {
        return logNavs;
    }

    public void setLogNavs(List<LogNav> logNavs) {
        this.logNavs = logNavs;
    }

    public Map<Map<String, Object>, List<Log>> getIndexHotLogs() {
        return indexHotLogs;
    }

    public void setIndexHotLogs(Map<Map<String, Object>, List<Log>> indexHotLogs) {
        this.indexHotLogs = indexHotLogs;
    }

    public List<Archive> getArchiveList() {
        return archiveList;
    }

    public void setArchiveList(List<Archive> archiveList) {
        this.archiveList = archiveList;
    }
}
