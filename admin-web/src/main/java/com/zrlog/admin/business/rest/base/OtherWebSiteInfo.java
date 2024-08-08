package com.zrlog.admin.business.rest.base;

import com.hibegin.common.util.StringUtils;
import com.zrlog.common.Validator;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

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

    @Override
    public void doClean() {
        if (StringUtils.isNotEmpty(robotRuleContent)) {
            this.robotRuleContent = Jsoup.clean(robotRuleContent, Safelist.none());
        }
        if (StringUtils.isNotEmpty(icp)) {
            this.icp = Jsoup.clean(robotRuleContent, Safelist.basicWithImages());
        }
    }

    public String getRobotRuleContent() {
        return robotRuleContent;
    }

    public void setRobotRuleContent(String robotRuleContent) {
        this.robotRuleContent = robotRuleContent;
    }
}
