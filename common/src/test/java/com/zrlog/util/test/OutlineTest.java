package com.zrlog.util.test;

import com.zrlog.common.vo.Outline;
import com.zrlog.util.OutlineUtil;

import java.util.List;

public class OutlineTest {


    public static void main(String[] args) {
        //String htmlStr = "<h1>H1</h1><h2>H2</h2><h3>h3</h3><h2>H2-2</h2><h3>h3-2</h3><h3>h3-3</h3><h4>h4</h4><h2>h2</h2><h1>h1</h1>";
        String htmlStr = "<h3>h3</h3><h2>h2</h2><h2>h2</h2><h4>h4</h4>";
        List<Outline> outlineList = OutlineUtil.extractOutline(htmlStr);
        String baseHtml = OutlineUtil.buildTocHtml(outlineList, "");
        System.out.println("baseHtml = " + baseHtml);
    }
}
