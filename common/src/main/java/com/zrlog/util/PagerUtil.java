package com.zrlog.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PagerUtil {

    public static Map<String, Object> generatorPager(String currentUri, int currentPage, Integer total) {
        Map<String, Object> pager = new HashMap<>();
        List<Map<String, Object>> pageList = new ArrayList<>();
        if (currentPage != 1) {
            pageList.add(pageEntity(currentUri, currentPage, I18nUtil.getStringFromRes("prevPage"), currentPage - 1));
        }
        if (total > 10) {
            if (currentPage < 3 || total - 4 < currentPage) {
                for (int i = 1; i <= 4; i++) {
                    pageList.add(pageEntity(currentUri, currentPage, i));
                }
            } else {
                if (currentPage + 1 == total - 3) {
                    pageList.add(pageEntity(currentUri, currentPage, currentPage - 3));
                }
                for (int i = currentPage - 2; i <= currentPage; i++) {
                    pageList.add(pageEntity(currentUri, currentPage, i));
                }
                if (currentPage + 1 != total - 3) {
                    pageList.add(pageEntity(currentUri, currentPage, currentPage + 1));
                }
            }
            for (int i = total - 3; i <= total; i++) {
                pageList.add(pageEntity(currentUri, currentPage, i));
            }
        } else {
            for (int i = 1; i <= total; i++) {
                pageList.add(pageEntity(currentUri, currentPage, i));
            }
        }
        if (currentPage != total) {
            pageList.add(pageEntity(currentUri, currentPage, I18nUtil.getStringFromRes("nextPage"), currentPage + 1));
        }
        pager.put("pageList", pageList);
        pager.put("pageStartUrl", currentUri + 1);
        pager.put("pageEndUrl", currentUri + total);
        pager.put("startPage", currentPage == 1);
        pager.put("endPage", currentPage == total);
        return pager;
    }

    private static Map<String, Object> pageEntity(String url, int currentPage, String desc, int page) {
        Map<String, Object> map = new HashMap<>();
        map.put("url", url + page);
        map.put("desc", desc);
        map.put("current", currentPage == page);
        return map;
    }

    private static Map<String, Object> pageEntity(String url, int currentPage, int page) {
        return pageEntity(url, currentPage, page + "", page);
    }

}
