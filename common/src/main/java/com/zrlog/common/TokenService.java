package com.zrlog.common;

import com.hibegin.http.server.api.HttpRequest;
import com.hibegin.http.server.api.HttpResponse;
import com.zrlog.common.vo.AdminTokenVO;

import java.util.Map;

public interface TokenService {

    AdminTokenVO getAdminTokenVO(HttpRequest request);

    void removeAdminToken(HttpRequest request, HttpResponse response);

    void setAdminToken(Map<String, Object> user, String sessionId, String protocol, HttpRequest request, HttpResponse response);
}
