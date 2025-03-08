package com.zrlog.common.rest.request;

import java.util.ArrayList;
import java.util.List;

public class PageRequestImpl implements PageRequest {

    private Long size;

    private Long page;

    private List<OrderBy> orders = new ArrayList<>();

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
    public List<OrderBy> getOrders() {
        return orders;
    }

    public void setOrders(List<OrderBy> orders) {
        this.orders = orders;
    }
}
