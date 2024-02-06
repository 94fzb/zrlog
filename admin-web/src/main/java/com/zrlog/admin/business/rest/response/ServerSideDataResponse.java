package com.zrlog.admin.business.rest.response;

import java.util.Map;
public record ServerSideDataResponse(UserBasicInfoResponse user, Map<String,Object> resourceInfo, Object pageData,String key) {
}