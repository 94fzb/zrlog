package com.zrlog.business.rest.response;

import com.zrlog.business.rest.base.*;

public class WebSiteSettingsResponse {

    private BasicWebSiteRequest basic;
    private BlogWebSiteRequest blog;
    private OtherWebSiteRequest other;
    private TemplateWebSiteRequest template;
    private UpgradeWebSiteRequest upgrade;

    public BasicWebSiteRequest getBasic() {
        return basic;
    }

    public void setBasic(BasicWebSiteRequest basic) {
        this.basic = basic;
    }

    public BlogWebSiteRequest getBlog() {
        return blog;
    }

    public void setBlog(BlogWebSiteRequest blog) {
        this.blog = blog;
    }

    public OtherWebSiteRequest getOther() {
        return other;
    }

    public void setOther(OtherWebSiteRequest other) {
        this.other = other;
    }

    public TemplateWebSiteRequest getTemplate() {
        return template;
    }

    public void setTemplate(TemplateWebSiteRequest template) {
        this.template = template;
    }

    public UpgradeWebSiteRequest getUpgrade() {
        return upgrade;
    }

    public void setUpgrade(UpgradeWebSiteRequest upgrade) {
        this.upgrade = upgrade;
    }
}
