package com.zrlog.common.rest.request;

public record OrderBy(String sortKey, Direction direction) {

    public String toParamString() {
        return sortKey + "," + direction;
    }
}
