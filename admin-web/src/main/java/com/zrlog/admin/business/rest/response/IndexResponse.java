package com.zrlog.admin.business.rest.response;

import java.util.List;

public class IndexResponse {

    private final StatisticsInfoResponse statisticsInfo;
    private final String welcomeTip;
    private final List<String> tips;
    private final List<ArticleActivityData> activityData;
    private final String versionInfo;

    public IndexResponse(StatisticsInfoResponse statisticsInfo, String welcomeTip, List<String> tips, List<ArticleActivityData> activityData, String versionInfo) {
        this.statisticsInfo = statisticsInfo;
        this.welcomeTip = welcomeTip;
        this.tips = tips;
        this.activityData = activityData;
        this.versionInfo = versionInfo;
    }

    public StatisticsInfoResponse getStatisticsInfo() {
        return statisticsInfo;
    }

    public String getWelcomeTip() {
        return welcomeTip;
    }

    public List<String> getTips() {
        return tips;
    }

    public List<ArticleActivityData> getActivityData() {
        return activityData;
    }

    public String getVersionInfo() {
        return versionInfo;
    }
}
