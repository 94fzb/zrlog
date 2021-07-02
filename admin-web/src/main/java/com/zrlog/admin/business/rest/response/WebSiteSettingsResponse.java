package com.zrlog.admin.business.rest.response;

import com.zrlog.admin.business.rest.base.BasicWebSiteRequest;
import com.zrlog.admin.business.rest.base.BlogWebSiteRequest;
import com.zrlog.admin.business.rest.base.OtherWebSiteRequest;
import com.zrlog.admin.business.rest.base.UpgradeWebSiteRequest;

public class WebSiteSettingsResponse {

    private BasicWebSiteRequest basic;
    private BlogWebSiteRequest blog;
    private OtherWebSiteRequest other;
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

    public UpgradeWebSiteRequest getUpgrade() {
        return upgrade;
    }

    public void setUpgrade(UpgradeWebSiteRequest upgrade) {
        this.upgrade = upgrade;
    }

}
