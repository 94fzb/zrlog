package com.zrlog.common.rest.request;

public class UnPageRequestImpl implements PageRequest {

    private final Long page;
    private final Long size;
    private String sort;
    private String order;


    public UnPageRequestImpl(Long page) {
        this.page = page;
        this.size = Long.MAX_VALUE;
    }

    @Override
    public Long getSize() {
        return size;
    }

    @Override
    public Long getPage() {
        return page;
    }

    @Override
    public String getSort() {
        return sort;
    }

    @Override
    public String getOrder() {
        return order;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public void setOrder(String order) {
        this.order = order;
    }
}
