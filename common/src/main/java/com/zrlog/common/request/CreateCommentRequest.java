package com.zrlog.common.request;

import com.google.gson.annotations.SerializedName;

public class CreateCommentRequest {

    @SerializedName(value = "webHome", alternate = {"userHome", "blog"})
    private String userHome;
    @SerializedName(value = "mail", alternate = {"userMail"})
    private String mail;
    private String userIp;
    private String userName;
    private String logId;
    @SerializedName(value = "comment", alternate = {"userComment"})
    private String comment;
    private String ip;
    private String userAgent;
    private int replyId;

    public String getUserHome() {
        return userHome;
    }

    public void setUserHome(String userHome) {
        this.userHome = userHome;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getUserIp() {
        return userIp;
    }

    public void setUserIp(String userIp) {
        this.userIp = userIp;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getLogId() {
        return logId;
    }

    public void setLogId(String logId) {
        this.logId = logId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public int getReplyId() {
        return replyId;
    }

    public void setReplyId(int replyId) {
        this.replyId = replyId;
    }
}
