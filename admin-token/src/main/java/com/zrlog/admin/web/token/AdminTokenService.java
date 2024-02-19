package com.zrlog.admin.web.token;

import com.google.gson.Gson;
import com.hibegin.common.util.ByteUtils;
import com.hibegin.common.util.LoggerUtil;
import com.hibegin.common.util.SecurityUtils;
import com.hibegin.common.util.StringUtils;
import com.hibegin.http.server.api.HttpRequest;
import com.hibegin.http.server.api.HttpResponse;
import com.hibegin.http.server.web.cookie.Cookie;
import com.zrlog.business.util.InstallUtils;
import com.zrlog.common.Constants;
import com.zrlog.common.vo.AdminTokenVO;
import com.zrlog.model.User;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AdminTokenService {

    public static final String ADMIN_TOKEN = "admin-token";
    /**
     * 字符长度必须要大于16个字符
     */
    public static final String AES_PUBLIC_KEY = "_BLOG_BLOG_BLOG_";
    private static final Logger LOGGER = LoggerUtil.getLogger(AdminTokenService.class);
    private static final String TOKEN_SPLIT_CHAR = "#";
    private static final IvParameterSpec iv;
    private static SecretKeySpec secretKeySpec;

    static {
        iv = new IvParameterSpec(AES_PUBLIC_KEY.getBytes(StandardCharsets.UTF_8));
    }

    private static byte[] encrypt(String secretKey, byte[] value) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        //必须要16位
        String newSecretKey = SecurityUtils.md5(secretKey).substring(8, 24);
        secretKeySpec = new SecretKeySpec(newSecretKey.getBytes(StandardCharsets.UTF_8), "AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, iv);
        return cipher.doFinal(value);
    }

    private static byte[] decrypt(String secretKey, byte[] encrypted) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        //必须要16位
        String newSecretKey = SecurityUtils.md5(secretKey).substring(8, 24);
        secretKeySpec = new SecretKeySpec(newSecretKey.getBytes(StandardCharsets.UTF_8), "AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, iv);
        return cipher.doFinal(encrypted);
    }

    public AdminTokenVO getAdminTokenVO(HttpRequest request) {
        if (!InstallUtils.isInstalled()) {
            return null;
        }
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        String decTokenString = null;
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(ADMIN_TOKEN)) {
                decTokenString = cookie.getValue();
            }
        }
        if (StringUtils.isEmpty(decTokenString)) {
            return null;
        }
        try {
            int userId = Integer.parseInt(decTokenString.substring(0, decTokenString.indexOf(TOKEN_SPLIT_CHAR)));
            Map<String, Object> user = new User().loadById(userId);
            if (user == null) {
                return null;
            }
            byte[] adminTokenEncryptAfter = ByteUtils.hexString2Bytes(decTokenString.substring(decTokenString.indexOf(TOKEN_SPLIT_CHAR) + 1));
            String base64Encode = new String(decrypt((String) user.get("secretKey"), Base64.getDecoder().decode(adminTokenEncryptAfter)));
            AdminTokenVO adminTokenVO = new Gson().fromJson(base64Encode, AdminTokenVO.class);
            if (adminTokenVO.getCreatedDate() + Constants.getSessionTimeout() > System.currentTimeMillis()) {
                return adminTokenVO;
            }
        } catch (BadPaddingException e) {
            //ignore 发生数据库 secretKey 无法解析 token 的情况下
        } catch (Exception e) {
            LOGGER.warning("Parse token error " + e.getMessage());
        }
        return null;
    }

    public void setAdminToken(Map<String, Object> user, String sessionId, String protocol, HttpRequest request, HttpResponse response) {
        AdminTokenVO adminTokenVO = new AdminTokenVO();
        adminTokenVO.setUserId((Integer) user.get("userId"));
        adminTokenVO.setSessionId(sessionId);
        adminTokenVO.setProtocol(protocol);
        long loginTime = System.currentTimeMillis();
        adminTokenVO.setCreatedDate(loginTime);
        AdminTokenThreadLocal.setAdminToken(adminTokenVO);
        String encryptBeforeString = new Gson().toJson(adminTokenVO);
        try {
            byte[] base64Bytes = Base64.getEncoder().encode(encrypt(user.get("secretKey").toString(), encryptBeforeString.getBytes()));
            String encryptAfterString = ByteUtils.bytesToHexString(base64Bytes);
            String finalTokenString = adminTokenVO.getUserId() + TOKEN_SPLIT_CHAR + encryptAfterString;
            Cookie cookie = new Cookie();
            cookie.setName(ADMIN_TOKEN);
            cookie.setValue(finalTokenString);
            cookie.setExpireDate(new Date(System.currentTimeMillis() + Constants.getSessionTimeout()));
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            response.addCookie(cookie);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "", e);
        }
    }

}
