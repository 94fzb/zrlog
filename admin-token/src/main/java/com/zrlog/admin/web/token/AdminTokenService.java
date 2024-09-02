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
import com.zrlog.common.TokenService;
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
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AdminTokenService implements TokenService {

    private static final String ADMIN_TOKEN_KEY_IN_COOKIE = "admin-token";
    private static final String ADMIN_TOKEN_KEY_IN_REQUEST_HEADER = "X-ZrLog-Admin-Token";
    private static final Logger LOGGER = LoggerUtil.getLogger(AdminTokenService.class);
    private static final String TOKEN_SPLIT_CHAR = "#";
    private static final IvParameterSpec iv;
    private static SecretKeySpec secretKeySpec;

    static {
        //字符长度必须要大于16个字符
        iv = new IvParameterSpec("_BLOG_BLOG_BLOG_".getBytes(StandardCharsets.UTF_8));
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

    @Override
    public AdminTokenVO getAdminTokenVO(HttpRequest request) {
        if (!InstallUtils.isInstalled()) {
            return null;
        }
        //header first, seconds parse cookie
        String tokenInHeader = request.getHeader(ADMIN_TOKEN_KEY_IN_REQUEST_HEADER);
        if (StringUtils.isNotEmpty(tokenInHeader)) {
            AdminTokenVO adminTokenVO = parseAdminTokenByStr(tokenInHeader);
            if (Objects.nonNull(adminTokenVO)) {
                return adminTokenVO;
            }
        }
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        String tokenString = null;
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals(ADMIN_TOKEN_KEY_IN_COOKIE)) {
                tokenString = cookie.getValue();
            }
        }
        if (StringUtils.isEmpty(tokenString)) {
            return null;
        }
        return parseAdminTokenByStr(tokenString);
    }

    private AdminTokenVO parseAdminTokenByStr(String tokenString) {
        try {
            int userId = Integer.parseInt(tokenString.substring(0, tokenString.indexOf(TOKEN_SPLIT_CHAR)));
            Map<String, Object> user = new User().loadById(userId);
            if (user == null) {
                return null;
            }
            byte[] adminTokenEncryptAfter = ByteUtils.hexString2Bytes(tokenString.substring(tokenString.indexOf(TOKEN_SPLIT_CHAR) + 1));
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

    @Override
    public void removeAdminToken(HttpRequest request, HttpResponse response) {
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if (ADMIN_TOKEN_KEY_IN_COOKIE.equals(cookie.getName())) {
                cookie.setValue("");
                cookie.setExpireDate(new Date(0));
                cookie.setPath("/");
                cookie.setHttpOnly(true);
                response.addCookie(cookie);
            }
        }
        response.redirect(Constants.ADMIN_LOGIN_URI_PATH);
    }

    @Override
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
            cookie.setName(ADMIN_TOKEN_KEY_IN_COOKIE);
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
