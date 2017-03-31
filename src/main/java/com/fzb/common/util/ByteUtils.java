package com.fzb.common.util;

public class ByteUtils {
    public static String bytesToHexString(byte[] b) {
        StringBuilder sb = new StringBuilder();
        for (byte aB : b) {
            String hex = Integer.toHexString(aB & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            sb.append(hex);
        }
        return sb.toString().toUpperCase();
    }

    public static byte[] hexString2Bytes(String hexString) {
        byte[] b = new byte[hexString.length() / 2];
        int j = 0;
        for (int i = 0; i < hexString.length() / 2; i++) {
            char c0 = hexString.charAt(j++);
            char c1 = hexString.charAt(j++);
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

    public static void main(String args[]) {
        String str = "xxx";
        String bytes = bytesToHexString(str.getBytes());
        System.out.println(new String(hexString2Bytes(bytes)));

    }
}