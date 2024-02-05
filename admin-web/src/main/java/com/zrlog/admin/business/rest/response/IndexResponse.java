package com.zrlog.admin.business.rest.response;

import java.util.Map;

public record IndexResponse(StatisticsInfoResponse statisticsInfo, Map<String,Object> serverInfo) {
}
