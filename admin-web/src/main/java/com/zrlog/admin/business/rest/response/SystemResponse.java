package com.zrlog.admin.business.rest.response;

import com.zrlog.common.vo.ServerInfo;

import java.util.List;

public record SystemResponse(SystemIOInfoVO systemIOInfo, List<ServerInfo> serverInfos, Boolean dockerMode,
                             Boolean nativeImageMode) {
}
