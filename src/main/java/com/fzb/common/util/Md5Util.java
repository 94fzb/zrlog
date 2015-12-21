package com.fzb.common.util;

import java.io.IOException;
import java.security.MessageDigest;

public class Md5Util {

    private static char[] md5String = {'0', '1', '2', '3', '4', '5', '6', '7', '8',
            '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    public static String MD5(String pwd) {
        if (pwd != null) {
            try {
                byte[] btInput = pwd.getBytes();

                MessageDigest mdInst = MessageDigest.getInstance("MD5");

                mdInst.update(btInput);

                byte[] md = mdInst.digest();

                int j = md.length;

                char[] str = new char[j * 2];
                int k = 0;
                for (byte byte0 : md) {
                    str[(k++)] = md5String[(byte0 >>> 4 & 0xF)];
                    str[(k++)] = md5String[(byte0 & 0xF)];
                }

                return new String(str).toLowerCase();
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    public static void main(String[] args) throws IOException {
        System.out.println(MD5("w123456"));
        /*System.out.println(System.currentTimeMillis());
        System.out.println(MD5(UUID.randomUUID().toString()));
		System.out.println(System.currentTimeMillis());*/
    }
}
