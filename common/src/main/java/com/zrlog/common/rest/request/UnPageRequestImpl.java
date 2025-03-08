package com.zrlog.common.rest.request;

import java.util.ArrayList;
import java.util.List;

public class UnPageRequestImpl implements PageRequest {

    private final Long page;
    private final Long size;
    private List<OrderBy> sorts = new ArrayList<>();

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
    public List<OrderBy> getSorts() {
        return sorts;
    }

    public void setOrder(List<OrderBy> orders) {
        this.sorts = orders;
    }
}
