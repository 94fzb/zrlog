package com.zrlog.data.dto;

import java.util.ArrayList;
import java.util.List;

public class PageData<T> {
    private long totalElements;
    private List<T> rows = new ArrayList<>();

    public List<T> getRows() {
        return rows;
    }

    public void setRows(List<T> rows) {
        this.rows = rows;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }
}