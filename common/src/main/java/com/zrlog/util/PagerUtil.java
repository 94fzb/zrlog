package com.zrlog.util;

import java.util.ArrayList;
import java.util.List;

public class PagerUtil {

    public static PagerVO generatorPager(String currentUri, int currentPage, Integer total) {
        PagerVO pager = new PagerVO();
        List<PagerVO.PageEntry> pageList = new ArrayList<>();
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
        pager.setPageList(pageList);
        pager.setPageStartUrl(currentUri + 1);
        pager.setPageEndUrl(currentUri + total);
        pager.setStartPage(currentPage == 1);
        pager.setEndPage(currentPage == total);
        return pager;
    }

    private static PagerVO.PageEntry pageEntity(String url, int currentPage, String desc, int page) {
        PagerVO.PageEntry map = new PagerVO.PageEntry();
        map.setUrl(url + page);
        map.setDesc(desc);
        map.setCurrent(currentPage == page);
        return map;
    }

    private static PagerVO.PageEntry pageEntity(String url, int currentPage, int page) {
        return pageEntity(url, currentPage, page + "", page);
    }

}
