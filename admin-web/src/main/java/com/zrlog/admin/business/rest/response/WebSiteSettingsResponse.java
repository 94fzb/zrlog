package com.zrlog.admin.business.rest.response;

import com.zrlog.admin.business.rest.base.BasicWebSiteRequest;
import com.zrlog.admin.business.rest.base.BlogWebSiteRequest;
import com.zrlog.admin.business.rest.base.OtherWebSiteRequest;
import com.zrlog.admin.business.rest.base.UpgradeWebSiteRequest;
import com.zrlog.common.vo.TemplateVO;

import java.util.ArrayList;
import java.util.List;

public class WebSiteSettingsResponse {

    private BasicWebSiteRequest basic;
    private BlogWebSiteRequest blog;
    private OtherWebSiteRequest other;
    private UpgradeWebSiteRequest upgrade;
    private List<TemplateVO> templates = new ArrayList<>();

    public List<TemplateVO> getTemplates() {

        return templates;
    }

    public void setTemplates(List<TemplateVO> templates) {
        this.templates = templates;
    }

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
