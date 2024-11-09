package com.zrlog.admin.business.rest.response;

import com.zrlog.common.vo.ServerInfo;

import java.util.List;

public record IndexResponse(StatisticsInfoResponse statisticsInfo, List<ServerInfo> serverInfos,
                            String welcomeTip,
                            List<String> tips, Boolean dockerMode, Boolean nativeImageMode,
                            List<ArticleActivityData> activityData) {
}
