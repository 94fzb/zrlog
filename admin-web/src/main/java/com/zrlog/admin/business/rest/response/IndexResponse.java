package com.zrlog.admin.business.rest.response;

import java.util.List;

public record IndexResponse(StatisticsInfoResponse statisticsInfo,
                            String welcomeTip,
                            List<String> tips,
                            List<ArticleActivityData> activityData,
                            String versionInfo) {
}
