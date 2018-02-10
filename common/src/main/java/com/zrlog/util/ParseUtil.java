package com.zrlog.util;

import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

/**
 * 一些常见的转化的工具方法。
 */
public class ParseUtil {
    public static int getFirstRecord(int page, int pageSize) {
        return (page - 1) * pageSize;
    }

    public static int getTotalPate(long count, int pageSize) {
        return (int) Math.ceil(count / (pageSize * 1.0D));
    }

    public static String autoDigest(String str, int size) {
        String digest = str.replaceAll("<[^>]*>", " ").replaceAll("\t|\r|", "").replace("\n", " ");
        if (digest.length() > size) {
            digest = digest.substring(0, size) + "  ...";
        }
        Elements elements = Jsoup.parse(str).body().select("video");
        if (elements != null && !elements.isEmpty()) {
            digest = elements.get(0).toString() + "<br/>" + digest;
        }
        return digest.trim();
    }

    public static String removeHtmlElement(String str) {
        return Jsoup.parse(str).body().text();
    }

    /**
     * @param str
     * @param defaultValue
     * @return
     */
    public static int strToInt(String str, int defaultValue) {
        if (str == null) {
            return defaultValue;
        }
        if (str.matches("^-?[1-9]\\d*$")) {
            return Integer.parseInt(str);
        }
        return defaultValue;
    }

    public static boolean isGarbageComment(String str) {
        // TODO　如何过滤垃圾信息
        return !containsHanScript(str);
    }

    private static boolean containsHanScript(String s) {
        for (int i = 0; i < s.length(); ) {
            int codepoint = s.codePointAt(i);
            i += Character.charCount(codepoint);
            if (Character.UnicodeScript.of(codepoint) == Character.UnicodeScript.HAN) {
                return true;
            }
        }
        return false;
    }

    public static void main(String[] args) {
        System.out.println("check = " + isGarbageComment("what is the best insurance company for auto"));
    }
}
