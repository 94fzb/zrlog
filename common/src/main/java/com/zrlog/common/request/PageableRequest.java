package com.zrlog.common.request;

import com.zrlog.util.ParseUtil;

public class PageableRequest {

    private int rows;
    private int page;
    private String sort;
    private String order;

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public int getOffset() {
        return ParseUtil.getFirstRecord(page, rows);
    }
}
