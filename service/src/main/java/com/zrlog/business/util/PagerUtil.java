package com.zrlog.business.util;

import com.zrlog.util.I18nUtil;

import java.util.ArrayList;
import java.util.List;

public class PagerUtil {

    public static PagerVO generatorPager(String currentUri, int currentPage, long totalPage) {
        PagerVO pager = new PagerVO();
        List<PagerVO.PageEntry> pageList = new ArrayList<>();
        if (currentPage != 1) {
            pageList.add(pageEntity(currentUri, currentPage, I18nUtil.getBlogStringFromRes("prevPage"), currentPage - 1));
        }
        if (totalPage > 10) {
            if (currentPage < 3 || totalPage - 4 < currentPage) {
                for (int i = 1; i <= 4; i++) {
                    pageList.add(pageEntity(currentUri, currentPage, i));
                }
            } else {
                if (currentPage + 1 == totalPage - 3) {
                    pageList.add(pageEntity(currentUri, currentPage, currentPage - 3));
                }
                for (int i = currentPage - 2; i <= currentPage; i++) {
                    pageList.add(pageEntity(currentUri, currentPage, i));
                }
                if (currentPage + 1 != totalPage - 3) {
                    pageList.add(pageEntity(currentUri, currentPage, currentPage + 1));
                }
            }
            for (long i = totalPage - 3; i <= totalPage; i++) {
                pageList.add(pageEntity(currentUri, currentPage, i));
            }
        } else {
            for (int i = 1; i <= totalPage; i++) {
                pageList.add(pageEntity(currentUri, currentPage, i));
            }
        }
        if (currentPage != totalPage) {
            pageList.add(pageEntity(currentUri, currentPage, I18nUtil.getBlogStringFromRes("nextPage"), currentPage + 1));
        }
        pager.setPageList(pageList);
        pager.setPageStartUrl(currentUri + 1);
        pager.setPageEndUrl(currentUri + totalPage);
        pager.setStartPage(currentPage == 1);
        pager.setEndPage(currentPage == totalPage);
        return pager;
    }

    private static PagerVO.PageEntry pageEntity(String url, int currentPage, String desc, long page) {
        PagerVO.PageEntry map = new PagerVO.PageEntry();
        map.setUrl(url + page);
        map.setDesc(desc);
        map.setCurrent(currentPage == page);
        return map;
    }

    private static PagerVO.PageEntry pageEntity(String url, int currentPage, long page) {
        return pageEntity(url, currentPage, page + "", page);
    }

}
