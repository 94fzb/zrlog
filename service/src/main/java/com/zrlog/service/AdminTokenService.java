package com.zrlog.service;

import com.google.gson.Gson;
import com.hibegin.common.util.ByteUtils;
import com.hibegin.common.util.SecurityUtils;
import com.hibegin.common.util.StringUtils;
import com.jfinal.core.JFinal;
import com.zrlog.common.Constants;
import com.zrlog.common.vo.AdminTokenVO;
import com.zrlog.model.User;
import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.util.AbstractMap;
import java.util.Map;

public class AdminTokenService {

    private static final Logger LOGGER = Logger.getLogger(AdminTokenService.class);

    private static final String TOKEN_SPLIT_CHAR = "#";

    private static IvParameterSpec iv;
    private static SecretKeySpec secretKeySpec;

    static {
        try {
            iv = new IvParameterSpec(Constants.AES_PUBLIC_KEY.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            LOGGER.error("", e);
        }
    }

    private static byte[] encrypt(String secretKey, byte[] value) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        //必须要16位
        String newSecretKey = SecurityUtils.md5(secretKey).substring(8, 24);
        secretKeySpec = new SecretKeySpec(newSecretKey.getBytes("UTF-8"), "AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, iv);
        return cipher.doFinal(value);
    }

    private static byte[] decrypt(String secretKey, byte[] encrypted) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        //必须要16位
        String newSecretKey = SecurityUtils.md5(secretKey).substring(8, 24);
        secretKeySpec = new SecretKeySpec(newSecretKey.getBytes("UTF-8"), "AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, iv);
        return cipher.doFinal(encrypted);
    }

    public Map.Entry<AdminTokenVO, User> getAdminTokenVOUserEntry(HttpServletRequest request) {
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
                    int userId = Integer.parseInt(decTokenString.substring(0, decTokenString.indexOf(TOKEN_SPLIT_CHAR)));
                    User user = User.dao.findById(userId);
                    if (user != null) {
                        byte[] adminTokenEncryptAfter = ByteUtils.hexString2Bytes(decTokenString.substring(decTokenString.indexOf(TOKEN_SPLIT_CHAR) + 1, decTokenString.length()));
                        String base64Encode = new String(decrypt(user.getStr("secretKey"), Base64.decodeBase64(adminTokenEncryptAfter)));
                        AdminTokenVO adminTokenVO = new Gson().fromJson(base64Encode, AdminTokenVO.class);
                        if (adminTokenVO.getCreatedDate() + Constants.getSessionTimeout() > System.currentTimeMillis()) {
                            return new AbstractMap.SimpleEntry<>(adminTokenVO, user);
                        }
                    } else {
                        return null;
                    }
                }
            } catch (BadPaddingException e) {
                //ignore 发生数据库 secretKey 无法解析 token 的情况下
            } catch (Exception e) {
                LOGGER.info("error", e);
            }
        }
        return null;
    }

    public void setAdminToken(User user, int sessionId, String protocol, HttpServletRequest request, HttpServletResponse response) {
        AdminTokenVO adminTokenVO = new AdminTokenVO();
        adminTokenVO.setUserId(user.getInt("userId"));
        adminTokenVO.setSessionId(sessionId);
        adminTokenVO.setProtocol(protocol);
        long loginTime = System.currentTimeMillis();
        adminTokenVO.setCreatedDate(loginTime);
        AdminTokenThreadLocal.setAdminToken(adminTokenVO);
        String encryptBeforeString = new Gson().toJson(adminTokenVO);
        try {
            byte[] base64Bytes = Base64.encodeBase64(encrypt(user.get("secretKey").toString(), encryptBeforeString.getBytes()));
            String encryptAfterString = ByteUtils.bytesToHexString(base64Bytes);
            String finalTokenString = adminTokenVO.getUserId() + TOKEN_SPLIT_CHAR + encryptAfterString;
            Cookie cookie = new Cookie(Constants.ADMIN_TOKEN, finalTokenString);
            cookie.setMaxAge((int) (Constants.getSessionTimeout() / 1000));
            setCookieDomain(request, cookie);
            cookie.setPath("/");
            response.addCookie(cookie);
        } catch (Exception e) {
            LOGGER.error("", e);
        }
    }

    private String getDomain(HttpServletRequest request) {
        String host = request.getHeader("Host");
        int idx = host.indexOf(':');
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
