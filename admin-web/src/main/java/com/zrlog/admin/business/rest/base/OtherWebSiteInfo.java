package com.zrlog.admin.business.rest.base;

import com.zrlog.common.Validator;

public class OtherWebSiteInfo implements Validator {

    private String icp;
    private String webCm;
    private String robotRuleContent;

    public String getIcp() {
        return icp;
    }

    public void setIcp(String icp) {
        this.icp = icp;
    }

    public String getWebCm() {
        return webCm;
    }

    public void setWebCm(String webCm) {
        this.webCm = webCm;
    }

    @Override
    public void doValid() {

    }

    public String getRobotRuleContent() {
        return robotRuleContent;
    }

    public void setRobotRuleContent(String robotRuleContent) {
        this.robotRuleContent = robotRuleContent;
    }
}
