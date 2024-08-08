package com.zrlog.admin.business.rest.request;

import com.hibegin.common.util.StringUtils;
import com.zrlog.admin.business.exception.ArgsException;
import com.zrlog.common.Validator;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

import java.util.Objects;

public class CreateArticleRequest implements Validator {

    private String content;
    private String thumbnail;
    private String title;
    private Long typeId;
    private String alias;
    private String markdown;
    private boolean canComment;
    private boolean privacy;
    private boolean recommended;
    private boolean rubbish;
    private String keywords;
    private String digest;
    private String editorType;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Long getTypeId() {
        return typeId;
    }

    public void setTypeId(Long typeId) {
        this.typeId = typeId;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getMarkdown() {
        return markdown;
    }

    public void setMarkdown(String markdown) {
        this.markdown = markdown;
    }

    public boolean isCanComment() {
        return canComment;
    }

    public void setCanComment(boolean canComment) {
        this.canComment = canComment;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getDigest() {
        return digest;
    }

    public void setDigest(String digest) {
        this.digest = digest;
    }

    public boolean isRecommended() {
        return recommended;
    }

    public void setRecommended(boolean recommended) {
        this.recommended = recommended;
    }

    public boolean isPrivacy() {
        return privacy;
    }

    public void setPrivacy(boolean privacy) {
        this.privacy = privacy;
    }

    public boolean isRubbish() {
        return rubbish;
    }

    public void setRubbish(boolean rubbish) {
        this.rubbish = rubbish;
    }

    public String getEditorType() {
        return editorType;
    }

    public void setEditorType(String editorType) {
        this.editorType = editorType;
    }

    @Override
    public void doValid() {
        if (Objects.isNull(typeId) || typeId <= 0) {
            throw new ArgsException("typeId");
        }
    }

    @Override
    public void doClean() {
        if (StringUtils.isNotEmpty(this.alias)) {
            setAlias(Jsoup.clean(this.alias, Safelist.none()));
        }
        if (StringUtils.isNotEmpty(this.title)) {
            setTitle(Jsoup.clean(this.title, Safelist.none()));
        }
        if (StringUtils.isNotEmpty(this.thumbnail)) {
            setThumbnail(Jsoup.clean(this.thumbnail, Safelist.none()));
        }
        if (StringUtils.isNotEmpty(this.keywords)) {
            setKeywords(Jsoup.clean(this.keywords, Safelist.none()));
        }
        if (StringUtils.isNotEmpty(this.digest)) {
            setDigest(Jsoup.clean(this.digest, Safelist.relaxed()));
        }
    }
}
