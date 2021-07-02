package com.zrlog.common.rest.request;

public class PageRequest {

    private int size;

    private int page;

    private String sort;

    private String order;

    public PageRequest(int page, int size) {
        this.page = page;
        this.size = size;
    }

    public PageRequest() {
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
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
        return (page - 1) * size;
    }
}
