package com.fzb.blog.web.incp;

import com.fzb.blog.common.Constants;
import com.fzb.blog.model.User;
import com.fzb.blog.model.WebSite;
import com.fzb.blog.web.util.AESCryptoUtil;
import com.fzb.common.util.ByteUtils;
import com.google.gson.Gson;
import com.jfinal.core.JFinal;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AdminTokenService {

    private static final Logger LOGGER = Logger.getLogger(AdminTokenService.class);

    private static final String TOKEN_SPLIT_CHAR = "#";

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
                    int userId = Integer.valueOf(decTokenString.substring(0, decTokenString.indexOf(TOKEN_SPLIT_CHAR)));
                    User user = User.dao.findById(userId);
                    if (user != null) {
                        byte[] adminTokenEncryptAfter = ByteUtils.hexString2Bytes(decTokenString.substring(decTokenString.indexOf(TOKEN_SPLIT_CHAR) + 1, decTokenString.length()));
                        String base64Encode = new String(AESCryptoUtil.decrypt(user.getStr("secretKey"), Base64.decodeBase64(adminTokenEncryptAfter)));
                        AdminToken adminToken = new Gson().fromJson(base64Encode, AdminToken.class);
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
        long sessionTimeout = getSessionTimeout();
        AdminToken adminToken = new AdminToken();
        adminToken.setUserId(user.getInt("userId"));
        long loginTime = System.currentTimeMillis();
        adminToken.setCreatedDate(loginTime);
        AdminTokenThreadLocal.setAdminToken(adminToken);
        String encryptBeforeString = new Gson().toJson(adminToken);
        try {
            byte[] base64Bytes = Base64.encodeBase64(AESCryptoUtil.encrypt(user.get("secretKey").toString(), encryptBeforeString.getBytes()));
            String encryptAfterString = ByteUtils.bytesToHexString(base64Bytes);
            String finalTokenString = adminToken.getUserId() + TOKEN_SPLIT_CHAR + encryptAfterString;
            Cookie cookie = new Cookie(Constants.ADMIN_TOKEN, finalTokenString);
            cookie.setMaxAge((int) (sessionTimeout / 1000));
            setCookieDomain(request, cookie);
            cookie.setPath("/");
            response.addCookie(cookie);
        } catch (Exception e) {
            LOGGER.error("", e);
        }
    }

    public Long getSessionTimeout() {
        String sessionTimeoutString = WebSite.dao.getValueByName(Constants.SESSION_TIMEOUT_KEY);
        Long sessionTimeout;
        if (!StringUtils.isEmpty(sessionTimeoutString)) {
            //*60， Cookie过期时间单位为分钟
            sessionTimeout = Long.valueOf(sessionTimeoutString) * 60 * 1000;
            if (sessionTimeout <= 0) {
                sessionTimeout = Constants.DEFAULT_SESSION_TIMEOUT;
            }
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

    public void setCookieDomain(HttpServletRequest request, Cookie cookie) {
        //一些IE遇到localhost的情况下无法正常存储cookie信息
        if (!JFinal.me().getConstants().getDevMode()) {
            cookie.setDomain(getDomain(request));
        }
    }
}
