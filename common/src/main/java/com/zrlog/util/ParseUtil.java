package com.zrlog.util;

import com.hibegin.common.util.StringUtils;
import com.zrlog.common.Constants;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 一些常用的转化的工具方法。
 */
public class ParseUtil {

    public static void main(String[] args) {
        String s = autoDigest("这是一个测试文本。它包含了一些文字和<span style='color:red;'>红色</span>内容。还有更多内容...", 20);
        System.out.println(s);
    }

    public static String getPlainSearchText(String content) {
        if (StringUtils.isEmpty(content)) {
            return "";
        }
        return Jsoup.parse(content).body().text();
    }


    public static String autoDigest(String str, int size) {
        if (Objects.isNull(str) || str.trim().isEmpty()) {
            return str;
        }
        Document doc = Jsoup.parseBodyFragment(str);
        StringBuilder sb = new StringBuilder();
        int currentLength = 0;
        Element body = doc.body();

        for (Node child : body.childNodes()) {
            currentLength += processNodeWithTags(child, size - currentLength, sb);
            if (currentLength >= size) {
                break; // 达到长度限制，退出循环
            }
        }

        return sb.toString();
    }

    /**
     * 摘要截断的位置可能仍然不是语义上的完整句子，这取决于文本内容和长度限制。 可以根据需要进行优化，例如寻找最近的标点符号进行截断。
     *
     * @param node
     * @param remainingLength
     * @param sb
     * @return
     */
    private static int processNodeWithTags(Node node, int remainingLength, StringBuilder sb) {
        if (node instanceof TextNode) {
            String text = ((TextNode) node).text().trim();
            if (!text.isEmpty()) {
                if (text.length() <= remainingLength) {
                    sb.append(text);
                    return text.length();
                } else {
                    sb.append(text, 0, remainingLength);
                    sb.append("...");
                    return remainingLength; // 标记已达到长度限制
                }
            }
        } else if (node instanceof Element) {
            Element element = (Element) node;
            int usedLength = 0;

            // 添加开始标签 (不计入长度)
            sb.append("<").append(element.tagName());
            for (org.jsoup.nodes.Attribute attribute : element.attributes()) {
                sb.append(" ").append(attribute.getKey()).append("=\"").append(attribute.getValue()).append("\"");
            }
            sb.append(">");

            for (Node child : element.childNodes()) {
                usedLength += processNodeWithTags(child, remainingLength - usedLength, sb);
                if (usedLength >= remainingLength) {
                    break; // 子节点已达到长度限制
                }
            }

            // 添加结束标签 (不计入长度)
            sb.append("</").append(element.tagName()).append(">");

            return usedLength; // 只返回文本内容的长度
        }
        return 0;
    }

    public static String removeHtmlElement(String str) {
        if (Objects.isNull(str) || str.trim().isEmpty()) {
            return str;
        }
        return Jsoup.parse(str).body().text();
    }

    /**
     *
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

    /**
     * 输入一段纯文本，通过指定关键字提取相关的上下文
     */
    public static String wrapperKeyword(String content, String keyword) {
        if (content == null) {
            return null;
        }
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
                        sb.append(content, count - strings[i].length(), count);
                        sb.append(str);
                        count += keyword.length();
                    }
                    // 添加最后一段数据
                    sb.append(content.substring(count).replace(keyword.toLowerCase(), wrapperFontRed(keyword)));
                    newContent = sb.toString();
                } else if (strings.length > 0) {
                    sb.append(content, 0, strings[0].length());
                    sb.append(lowerContent.substring(strings[0].length()).replace(keyword.toLowerCase(),
                            wrapperFontRed(content.substring(strings[0].length()))));
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

    public static boolean isNumeric(String str) {
        if (str == null || str.isEmpty()) {
            return false;
        }
        return str.matches("-?([1-9]\\d*|0)(\\.\\d+)?");
    }


    public static String toNamingDurationString(long milliseconds, boolean en) {
        long days = TimeUnit.MILLISECONDS.toDays(milliseconds);
        long hours = TimeUnit.MILLISECONDS.toHours(milliseconds) % 24;
        long minutes = TimeUnit.MILLISECONDS.toMinutes(milliseconds) % 60;
        long seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds) % 60;
        if (en) {
            return String.format("%dd %dh %dm %ds", days, hours, minutes, seconds);
        }
        return String.format("%d天 %d时 %d分 %d秒", days, hours, minutes, seconds);
    }
}
