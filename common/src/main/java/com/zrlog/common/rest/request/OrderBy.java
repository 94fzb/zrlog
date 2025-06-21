package com.zrlog.common.rest.request;

public class OrderBy {

    private final String sortKey;
    private final Direction direction;

    public String toParamString() {
        return sortKey + "," + direction;
    }

    public OrderBy(String sortKey, Direction direction) {
        this.direction = direction;
        this.sortKey = sortKey;
    }

    public String getSortKey() {
        return sortKey;
    }

    public Direction getDirection() {
        return direction;
    }
}
