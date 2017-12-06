package com.zrlog.web.token;

import com.hibegin.common.util.SecurityUtils;
import com.hibegin.common.util.StringUtils;
import com.zrlog.common.Constants;
import com.zrlog.model.WebSite;
import com.zrlog.model.User;
import com.google.gson.Gson;
import com.hibegin.common.util.ByteUtils;
import com.jfinal.core.JFinal;
import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;

public class AdminTokenService {

    private static final Logger LOGGER = Logger.getLogger(AdminTokenService.class);

    private static final String TOKEN_SPLIT_CHAR = "#";

    private static IvParameterSpec iv;
    private static SecretKeySpec secretKeySpec;

    static {
        try {
            iv = new IvParameterSpec(Constants.AES_PUBLIC_KEY.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private static byte[] encrypt(String secretKey, byte[] value) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        //必须要16位
        secretKey = SecurityUtils.md5(secretKey).substring(8, 24);
        secretKeySpec = new SecretKeySpec(secretKey.getBytes("UTF-8"), "AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, iv);
        return cipher.doFinal(value);
    }

    private static byte[] decrypt(String secretKey, byte[] encrypted) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        //必须要16位
        secretKey = SecurityUtils.md5(secretKey).substring(8, 24);
        secretKeySpec = new SecretKeySpec(secretKey.getBytes("UTF-8"), "AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, iv);
        return cipher.doFinal(encrypted);
    }

    public AdminToken getAdminToken(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            String decTokenString = null;
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(Constants.ADMIN_TOKEN)) {
                    decTokenString = cookie.getValue();
                }
            }
            try {
                if (StringUtils.isNotEmpty(decTokenString)) {
                    int userId = Integer.valueOf(decTokenString.substring(0, decTokenString.indexOf(TOKEN_SPLIT_CHAR)));
                    User user = User.dao.findById(userId);
                    if (user != null) {
                        byte[] adminTokenEncryptAfter = ByteUtils.hexString2Bytes(decTokenString.substring(decTokenString.indexOf(TOKEN_SPLIT_CHAR) + 1, decTokenString.length()));
                        String base64Encode = new String(decrypt(user.getStr("secretKey"), Base64.decodeBase64(adminTokenEncryptAfter)));
                        AdminToken adminToken = new Gson().fromJson(base64Encode, AdminToken.class);
                        if (adminToken.getCreatedDate() + getSessionTimeout() > System.currentTimeMillis()) {
                            return adminToken;
                        }
                    } else {
                        return null;
                    }
                }
            } catch (Exception e) {
                LOGGER.info("error", e);
            }
        }
        return null;
    }

    public void setAdminToken(int userId, int sessionId, HttpServletRequest request, HttpServletResponse response) {
        User user = User.dao.findById(userId);
        long sessionTimeout = getSessionTimeout();
        AdminToken adminToken = new AdminToken();
        adminToken.setUserId(user.getInt("userId"));
        adminToken.setSessionId(sessionId);
        long loginTime = System.currentTimeMillis();
        adminToken.setCreatedDate(loginTime);
        AdminTokenThreadLocal.setAdminToken(adminToken);
        String encryptBeforeString = new Gson().toJson(adminToken);
        try {
            byte[] base64Bytes = Base64.encodeBase64(encrypt(user.get("secretKey").toString(), encryptBeforeString.getBytes()));
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
