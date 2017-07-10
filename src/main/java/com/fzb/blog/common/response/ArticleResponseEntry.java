package com.fzb.blog.common.response;

import java.util.Date;

public class ArticleResponseEntry {

    private String userName;
    private long id;
    private String title;
    private int click;
    private String keywords;
    private Date releaseTime;
    private String typeName;
    private boolean rubbish;
    private boolean _private;

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

    public Date getReleaseTime() {
        return releaseTime;
    }

    public void setReleaseTime(Date releaseTime) {
        this.releaseTime = releaseTime;
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

    public boolean is_private() {
        return _private;
    }

    public void set_private(boolean _private) {
        this._private = _private;
    }
}
