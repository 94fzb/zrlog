package com.zrlog.common;

import com.hibegin.http.server.api.HttpRequest;
import com.hibegin.http.server.api.HttpResponse;
import com.zrlog.common.vo.AdminFullTokenVO;

public interface TokenService {

    AdminFullTokenVO getAdminTokenVO(HttpRequest request);

    void removeAdminToken(HttpRequest request, HttpResponse response);

    void setAdminToken(Integer userId, String secretKey, String sessionId, String protocol, HttpRequest request, HttpResponse response) throws Exception;
}
