package com.zrlog.util;

import com.zrlog.common.Constants;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
        StringBuilder sb = new StringBuilder();
        Document document = Jsoup.parseBodyFragment(str);
        List<Node> allTextNode = new ArrayList<>();
        getAllTextNode(document.childNodes(), allTextNode);
        int tLength = 0;
        for (Node node : allTextNode) {
            if (node instanceof TextNode) {
                sb.append(node.parent().outerHtml());
                tLength += ((TextNode) node).text().length();
                if (tLength > size) {
                    sb.append(" ...");
                    break;
                }
            }
        }
        String digest = sb.toString();
        Elements elements = Jsoup.parse(str).body().select("video");
        if (elements != null && !elements.isEmpty()) {
            digest = elements.get(0).toString() + "<br/>" + digest;
        }
        return digest.trim();
    }

    private static void getAllTextNode(List<Node> nodes, List<Node> nodeList) {
        for (Node node : nodes) {
            if (!node.childNodes().isEmpty()) {
                getAllTextNode(node.childNodes(), nodeList);
            } else {
                if (node instanceof TextNode) {
                    if (((TextNode) node).text().trim().length() > 0) {
                        nodeList.add(node);
                    }
                }
            }
        }
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
        return false;
    }

    /*private static boolean containsHanScript(String s) {
        for (int i = 0; i < s.length(); ) {
            int codepoint = s.codePointAt(i);
            i += Character.charCount(codepoint);
            if (Character.UnicodeScript.of(codepoint) == Character.UnicodeScript.HAN) {
                return true;
            }
        }
        return false;
    }*/

    /**
     * 输入一段纯文本，通过指定关键字提取相关的上下文
     *
     * @param content
     * @param keyword
     * @return
     */
    public static String wrapperKeyword(String content, String keyword) {
        String newContent = content;
        if (content.contains(keyword)) {
            newContent = content.replace(keyword, wrapperFontRed(keyword));
        } else {
            String lowerContent = content.toLowerCase();
            if (lowerContent.contains(keyword.toLowerCase())) {
                String[] strings = lowerContent.split(keyword.toLowerCase());
                StringBuilder sb = new StringBuilder();
                int count = 0;
                if (strings.length > 1) {
                    for (int i = 0; i < strings.length - 1; i++) {
                        count += strings[i].length();
                        String str = wrapperFontRed(content.substring(count, count + keyword.length()));
                        sb.append(content.substring(count - strings[i].length(), count));
                        sb.append(str);
                        count += keyword.length();
                    }
                    // 添加最后一段数据
                    sb.append(content.substring(count).replace(keyword.toLowerCase(), wrapperFontRed(keyword)));
                    newContent = sb.toString();
                } else if (strings.length > 0) {
                    sb.append(content.substring(0, strings[0].length()));
                    sb.append(lowerContent.substring(strings[0].length()).replace(keyword.toLowerCase(), wrapperFontRed(content.substring(strings[0].length()))));
                    newContent = sb.toString();
                } else {
                    newContent = wrapperFontRed(content);
                }
            }
        }
        if (newContent.length() != content.length()) {
            int first = newContent.indexOf("<font") - 5;
            if (first > -1 && newContent.length() > Constants.DEFAULT_ARTICLE_DIGEST_LENGTH) {
                String[] fontArr = (newContent.substring(first)).split("</font>");
                newContent = "";
                for (String str : fontArr) {
                    if (!"".equals(newContent) && newContent.length() + str.length() > Constants.DEFAULT_ARTICLE_DIGEST_LENGTH) {
                        if (newContent.length() < 100) {
                            newContent += str.substring(0, 100);
                        }
                        break;
                    }
                    newContent += str + "</font>";
                }
            }
        }
        return ParseUtil.optimizeContent(newContent, keyword);
    }


    private static String wrapperFontRed(String content) {
        return "<font color=\"#CC0000\">" + content + "</font>";
    }

    private static String optimizeContent(String content, String keyword) {
        Set<String> stopWords = new HashSet<>();
        stopWords.add("。");
        stopWords.add("，");
        stopWords.add(".");
        stopWords.add("；");
        stopWords.add(";");
        Set<String> stopWord2 = new HashSet<>();
        stopWord2.add("。");
        String newContent = content;
        for (String stopWord : stopWords) {
            if (newContent.contains(stopWord) && newContent.indexOf(stopWord) < newContent.indexOf(keyword)) {
                newContent = newContent.substring(content.indexOf(stopWord) + 1);
                break;
            }
        }
        for (String stopWord : stopWord2) {
            if (newContent.contains(stopWord) && newContent.lastIndexOf(stopWord) > newContent.lastIndexOf(keyword)) {
                newContent = newContent.substring(0, newContent.lastIndexOf(stopWord) + 1);
                break;
            }
        }
        return newContent;
    }

    public static void main(String[] args) {
        System.out.println("check = " + isGarbageComment("what is the best insurance company for auto"));
    }
}
