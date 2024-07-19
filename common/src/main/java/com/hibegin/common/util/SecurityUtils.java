package com.hibegin.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.logging.Logger;

public class SecurityUtils {

    private static final Logger LOGGER = LoggerUtil.getLogger(SecurityUtils.class);

    private static final char[] md5String = {'0', '1', '2', '3', '4', '5', '6', '7', '8',
            '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    public static String md5(String string) {
        if (string != null) {
            return md5(string.getBytes());
        }
        return null;
    }

    public static String md5(byte[] bytes) {
        try {
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            mdInst.update(bytes);
            return toMdStr(mdInst);
        } catch (Exception e) {
            LOGGER.warning("md5 error " + e.getMessage());
            return null;
        }
    }

    private static String toMdStr(MessageDigest mdInst) {
        byte[] md = mdInst.digest();

        int j = md.length;

        char[] str = new char[j * 2];
        int k = 0;
        for (byte byte0 : md) {
            str[(k++)] = md5String[(byte0 >>> 4 & 0xF)];
            str[(k++)] = md5String[(byte0 & 0xF)];
        }

        return new String(str).toLowerCase();
    }

    public static String md5ByFile(File file) {
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            return md5(fileInputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String md5(InputStream inputStream) {
        if (inputStream == null) {
            return null;
        }
        try {
            MessageDigest mdInst = MessageDigest.getInstance("MD5");
            byte[] buffer = new byte[8192]; // Use a buffer for efficient reading
            int bytesRead;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
                mdInst.update(buffer, 0, bytesRead); // Update digest with read bytes
            }
            return toMdStr(mdInst);
        } catch (Exception e) {
            LOGGER.warning("md5 error " + e.getMessage());
            return null;
        }
    }

    public static void main(String[] args) throws IOException {
        System.out.println(md5ByFile(new File("/home/xiaochun/wallpaper/dawagenza.jpg")));
    }
}
