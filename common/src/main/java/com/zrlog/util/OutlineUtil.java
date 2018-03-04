package com.zrlog.util;

import com.zrlog.common.vo.Outline;
import com.zrlog.common.vo.OutlineVO;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

public class OutlineUtil {

    private OutlineUtil() {
    }

    public static OutlineVO extractOutline(String htmlStr) {
        Document document = Jsoup.parse(htmlStr);
        Elements elements = document.body().select("h1,h2,h3,h4,h5,h6");
        OutlineVO outlines = new OutlineVO();
        for (Element element : elements) {
            Outline outline = new Outline();
            outline.setText(element.text().trim());
            outline.setLevel(-Integer.valueOf(element.tagName().replace("h", "")));
            outlines.add(outline);
        }
        int currentLevel = 0;
        List<Outline> needRemoveOutline = new ArrayList<>();
        for (Outline outline : outlines) {
            int currentIdx = outlines.indexOf(outline);
            if (currentLevel != 0) {
                if (currentLevel > outline.getLevel()) {
                    outlines.get(currentIdx - 1).getChildren().add(outline);
                    needRemoveOutline.add(outline);
                } else if (currentLevel <= outline.getLevel()) {
                    for (int i = currentIdx - 1; i >= 0; i--) {
                        if (outlines.get(i).getLevel() > outline.getLevel()) {
                            outlines.get(i).getChildren().add(outline);
                            needRemoveOutline.add(outline);
                            break;
                        }
                    }
                }
            }
            currentLevel = outline.getLevel();

        }
        outlines.removeAll(needRemoveOutline);
        return outlines;
    }

    public static String buildTocHtml(OutlineVO outlines, String baseStr) {
        StringBuilder baseStrBuilder = new StringBuilder(baseStr);
        for (Outline outline : outlines) {
            if (!outline.getChildren().isEmpty()) {
                baseStrBuilder.append("<li>").append(buildLiLink(outline.getText())).append(buildTocHtml(outline.getChildren(), "")).append("</li>");
            } else {
                baseStrBuilder.append("<li>").append(buildLiLink(outline.getText())).append("</li>");
            }
        }
        baseStr = baseStrBuilder.toString();
        return "<ul>" + baseStr + "</ul>";
    }

    private static String buildLiLink(String text) {
        return "<a href='#" + text + "'>" + text + "</a>";
    }
}
