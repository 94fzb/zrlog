package com.zrlog.admin.business.rest.response;

import com.zrlog.admin.business.rest.base.BasicWebSiteInfo;
import com.zrlog.admin.business.rest.base.BlogWebSiteInfo;
import com.zrlog.admin.business.rest.base.OtherWebSiteInfo;
import com.zrlog.admin.business.rest.base.UpgradeWebSiteInfo;
import com.zrlog.common.vo.TemplateVO;

import java.util.ArrayList;
import java.util.List;

public class WebSiteSettingsResponse {

    private BasicWebSiteInfo basic;
    private BlogWebSiteInfo blog;
    private OtherWebSiteInfo other;
    private UpgradeWebSiteInfo upgrade;
    private List<TemplateVO> templates = new ArrayList<>();

    public List<TemplateVO> getTemplates() {

        return templates;
    }

    public void setTemplates(List<TemplateVO> templates) {
        this.templates = templates;
    }

    public BasicWebSiteInfo getBasic() {
        return basic;
    }

    public void setBasic(BasicWebSiteInfo basic) {
        this.basic = basic;
    }

    public BlogWebSiteInfo getBlog() {
        return blog;
    }

    public void setBlog(BlogWebSiteInfo blog) {
        this.blog = blog;
    }

    public OtherWebSiteInfo getOther() {
        return other;
    }

    public void setOther(OtherWebSiteInfo other) {
        this.other = other;
    }

    public UpgradeWebSiteInfo getUpgrade() {
        return upgrade;
    }

    public void setUpgrade(UpgradeWebSiteInfo upgrade) {
        this.upgrade = upgrade;
    }

}
