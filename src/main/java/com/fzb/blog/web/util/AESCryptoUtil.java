package com.fzb.blog.web.util;

import com.fzb.blog.common.Constants;
import com.fzb.common.util.SecurityUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;

public class AESCryptoUtil {

    private static IvParameterSpec iv;
    private static SecretKeySpec secretKeySpec;

    static {
        try {
            iv = new IvParameterSpec(Constants.AES_PUBLIC_KEY.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public static byte[] encrypt(String secretKey, byte[] value) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        //必须要16位
        secretKey = SecurityUtils.md5(secretKey).substring(8, 24);
        secretKeySpec = new SecretKeySpec(secretKey.getBytes("UTF-8"), "AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, iv);
        return cipher.doFinal(value);
    }

    public static byte[] decrypt(String secretKey, byte[] encrypted) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
        //必须要16位
        secretKey = SecurityUtils.md5(secretKey).substring(8, 24);
        secretKeySpec = new SecretKeySpec(secretKey.getBytes("UTF-8"), "AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, iv);
        return cipher.doFinal(encrypted);
    }

    public static void main(String[] args) throws Exception {
        String secretKey = "Bar12345Bar54321";
        String sourceStr = "java";
        for (int i = 0; i < 10; i++) {
            sourceStr += i;
            long start = System.currentTimeMillis();
            System.out.println(System.currentTimeMillis() - start);
            byte[] encrypt = encrypt(secretKey, sourceStr.getBytes());
            System.out.println("encrypt data " + new String(encrypt));
            System.out.println(System.currentTimeMillis() - start);
            byte[] decrypt = decrypt(secretKey, encrypt);
            String decryptStr = new String(decrypt);
            System.out.println("decrypt data " + decryptStr);
            assert sourceStr.equals(decryptStr);
            System.out.println(System.currentTimeMillis() - start);
        }

    }
}