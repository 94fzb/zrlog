package com.zrlog.common.rest.request;

public interface PageRequest {

    Long getSize();

    Long getPage();

    String getSort();

    String getOrder();

    default Long getOffset() {
        return (getPage() - 1) * getSize();
    }
}
