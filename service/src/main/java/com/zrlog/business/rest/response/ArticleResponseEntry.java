package com.zrlog.business.rest.response;

public class ArticleResponseEntry {

    private String userName;
    private long id;
    private String title;
    private int click;
    private String keywords;
    private String releaseTime;

    private String lastUpdateDate;
    private String typeName;
    private boolean rubbish;
    private Boolean canComment;
    private String commentSize;
    private boolean privacy;
    private String url;
    private String alias;
    private String arrange_plugin;
    private String typeAlias;

    public int getClick() {
        return click;
    }

    public void setClick(int click) {
        this.click = click;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getReleaseTime() {
        return releaseTime;
    }

    public void setReleaseTime(String releaseTime) {
        this.releaseTime = releaseTime;
    }

    public String getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(String lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public boolean isRubbish() {
        return rubbish;
    }

    public void setRubbish(boolean rubbish) {
        this.rubbish = rubbish;
    }

    public boolean isPrivacy() {
        return privacy;
    }

    public void setPrivacy(boolean privacy) {
        this.privacy = privacy;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public Boolean getCanComment() {
        return canComment;
    }

    public void setCanComment(Boolean canComment) {
        this.canComment = canComment;
    }

    public String getCommentSize() {
        return commentSize;
    }

    public void setCommentSize(String commentSize) {
        this.commentSize = commentSize;
    }

    public String getArrange_plugin() {
        return arrange_plugin;
    }

    public void setArrange_plugin(String arrange_plugin) {
        this.arrange_plugin = arrange_plugin;
    }

    public String getTypeAlias() {
        return typeAlias;
    }

    public void setTypeAlias(String typeAlias) {
        this.typeAlias = typeAlias;
    }
}
