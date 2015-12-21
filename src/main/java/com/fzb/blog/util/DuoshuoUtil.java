package com.fzb.blog.util;

import com.fzb.blog.model.Comment;
import com.fzb.blog.util.duoshuo.DuoShuoComment;
import com.fzb.blog.util.duoshuo.Meta;
import com.fzb.blog.util.duoshuo.ResponseEntry;
import com.fzb.common.util.HttpUtil;
import com.fzb.common.util.ParseTools;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.text.ParseException;
import java.util.*;

public class DuoshuoUtil {

    public static String hmacSHA1Encrypt(Map<String, Object> param, String encryptKey) throws Exception {
        return hmacSHA1Encrypt(ParseTools.mapToQueryStr(param), encryptKey);
    }

    public static String hmacSHA1Encrypt(String encryptText, String encryptKey) throws Exception {
        String HMAC_SHA1_ALGORITHM = "HmacSHA1";
        SecretKeySpec signingKey = new SecretKeySpec(encryptKey.getBytes(), HMAC_SHA1_ALGORITHM);
        // Get an hmac_sha1 Mac instance and initialise with the signing key
        Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
        mac.init(signingKey);
        // Compute the hmac
        byte[] rawHmac = mac.doFinal(encryptText.getBytes());
        byte[] hexBytes = new Hex().encode(rawHmac);
        byte hex[] = hexString2Bytes(new String(hexBytes, "ISO-8859-1"));
        return new String(Base64.encodeBase64(hex));
    }


    public static List<ResponseEntry> getComments(String shortName, String secret) {
        DuoShuoComment duoShuoComment = getComment(10000, shortName, secret);
        if (duoShuoComment.getCode() == 0) {
            return duoShuoComment.getResponse();
        }
        return new ArrayList<ResponseEntry>();
    }

    public static ResponseEntry getCommentLast(String shortName, String secret) {
        DuoShuoComment duoShuoComment = getComment(1, shortName, secret);
        if (duoShuoComment.getCode() == 0) {
            return duoShuoComment.getResponse().get(0);
        }
        return null;
    }

    private static DuoShuoComment getComment(int limit, String shortName, String secret) {
        String urlPath = "http://api.duoshuo.com/log/list.json";
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("short_name", shortName);
        params.put("secret", secret);
        params.put("limit", limit);
        params.put("order", "desc");
        try {
            return new Gson().fromJson(HttpUtil.getResponse(urlPath, params),
                    new TypeToken<DuoShuoComment>() {
                    }.getType());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new DuoShuoComment();
    }

    public static Comment convertToSelf(Meta meta) {
        Date td = null;
        try {
            td = ParseTools.getDataBySdf("yyyy-MM-dd HH:mm:ss", meta.getCreated_at());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return new Comment().set("userIp", meta.getIp())
                .set("userMail", meta.getAuthor_email())
                .set("hide", false)
                .set("commTime", new Date())
                .set("userComment", meta.getMessage())
                .set("userName", meta.getAuthor_name())
                .set("logId", meta.getThread_key())
                .set("userHome", meta.getAuthor_url())
                .set("td", td)
                .set("postId", meta.getPost_id());
    }

    private static byte[] hexString2Bytes(String hexStr) {
        byte[] b = new byte[hexStr.length() / 2];
        int j = 0;
        for (int i = 0; i < b.length; i++) {
            char c0 = hexStr.charAt(j++);
            char c1 = hexStr.charAt(j++);
            b[i] = (byte) ((parse(c0) << 4) | parse(c1));
        }
        return b;
    }

    private static int parse(char c) {
        if (c >= 'a')
            return (c - 'a' + 10) & 0x0F;
        if (c >= 'A')
            return (c - 'A' + 10) & 0x0F;
        return (c - '0') & 0x0F;
    }
}
