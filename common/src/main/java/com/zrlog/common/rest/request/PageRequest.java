package com.zrlog.common.rest.request;

import java.util.List;

public interface PageRequest {

    Long getSize();

    Long getPage();

    List<OrderBy> getOrders();

    default Long getOffset() {
        return (getPage() - 1) * getSize();
    }
}
