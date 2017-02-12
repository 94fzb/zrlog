package com.fzb.blog.web.incp;

import com.fzb.blog.common.Constants;
import com.fzb.blog.model.User;
import com.fzb.blog.model.WebSite;
import com.fzb.blog.web.util.AESCryptoUtil;
import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AdminTokenService {

    private static final Logger LOGGER = Logger.getLogger(AdminTokenService.class);

    public int getUserId(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            String decTokenString = null;
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(Constants.ADMIN_TOKEN)) {
                    decTokenString = cookie.getValue();
                }
            }
            try {
                if (StringUtils.isNotBlank(decTokenString)) {
                    int userId = Integer.valueOf(decTokenString.substring(0, decTokenString.indexOf(",")));
                    User user = User.dao.findById(userId);
                    if (user != null) {
                        byte[] adminTokenEncryptAfter = Base64.decodeBase64(decTokenString.substring(decTokenString.indexOf(","), decTokenString.length()).getBytes());
                        AdminToken adminToken = new JSONDeserializer<AdminToken>().deserialize(new String(AESCryptoUtil.decrypt(user.getStr("secretKey"), adminTokenEncryptAfter)), AdminToken.class);
                        if (adminToken.getCreatedDate() + getSessionTimeout() > System.currentTimeMillis()) {
                            return userId;
                        }
                    } else {
                        return -1;
                    }
                }
            } catch (Exception e) {
                LOGGER.info("error", e);
            }
        }
        return -1;
    }

    public void setAdminToken(int userId, HttpServletRequest request, HttpServletResponse response) {
        User user = User.dao.findById(userId);
        int sessionTimeout = getSessionTimeout();
        AdminToken adminToken = new AdminToken();
        adminToken.setUserId(user.getInt("userId"));
        long loginTime = System.currentTimeMillis();
        adminToken.setCreatedDate(loginTime);
        AdminTokenThreadLocal.setAdminToken(adminToken);
        String encryptBeforeString = new JSONSerializer().deepSerialize(adminToken);
        try {
            String encryptAfterString = Base64.encodeBase64String(AESCryptoUtil.encrypt(user.get("secretKey").toString(), encryptBeforeString.getBytes()));
            String finalTokenString = adminToken.getUserId() + "," + encryptAfterString;
            Cookie cookie = new Cookie(Constants.ADMIN_TOKEN, finalTokenString);
            cookie.setMaxAge(sessionTimeout / 1000);
            cookie.setDomain(getDomain(request));
            cookie.setPath("/");
            response.addCookie(cookie);
        } catch (Exception e) {
            LOGGER.error("", e);
        }
    }

    public int getSessionTimeout() {
        String sessionTimeoutString = WebSite.dao.getValueByName(Constants.SESSION_TIMEOUT_KEY);
        int sessionTimeout;
        if (!StringUtils.isEmpty(sessionTimeoutString)) {
            sessionTimeout = Integer.valueOf(sessionTimeoutString);
        } else {
            sessionTimeout = Constants.DEFAULT_SESSION_TIMEOUT;
        }
        return sessionTimeout;
    }

    public String getDomain(HttpServletRequest request) {
        String host = request.getHeader("Host");
        int idx = host.indexOf(":");
        if (idx != -1) {
            host = host.substring(0, idx);
        }
        return host;
    }
}
