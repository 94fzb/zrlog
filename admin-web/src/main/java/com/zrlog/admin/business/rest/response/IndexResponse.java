package com.zrlog.admin.business.rest.response;

import com.zrlog.common.vo.ServerInfo;

import java.util.List;

public record IndexResponse(StatisticsInfoResponse statisticsInfo, List<ServerInfo> serverInfos, List<String> tips,Boolean dockerMode,Boolean nativeImageMode) {
}
