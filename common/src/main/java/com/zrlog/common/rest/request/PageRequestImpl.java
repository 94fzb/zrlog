package com.zrlog.common.rest.request;

public class PageRequestImpl implements PageRequest {

    private Long size;

    private Long page;

    private String sort;

    private String order;

    public PageRequestImpl() {
    }

    public PageRequestImpl(Long page, Long size) {
        this.page = page;
        this.size = size;
    }

    @Override
    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    @Override
    public Long getPage() {
        return page;
    }

    public void setPage(Long page) {
        this.page = page;
    }

    @Override
    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    @Override
    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }
}
