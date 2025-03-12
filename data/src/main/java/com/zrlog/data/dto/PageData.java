package com.zrlog.data.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PageData<T> implements Serializable {

    public PageData() {
        this(0L, new ArrayList<>());
    }

    public PageData(Long totalElements, List<T> rows) {
        this(totalElements, rows, 0L, 0L);
    }

    public PageData(Long totalElements, List<T> rows, Long page, Long size) {
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.rows = rows;
    }

    private Long page;
    private Long size;
    private Long totalElements;
    private String key;
    private List<T> rows;
    private List<String> sort;
    private Long defaultPageSize;

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

    public Long getPage() {
        return page;
    }

    public void setPage(Long page) {
        this.page = page;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public void setTotalElements(Long totalElements) {
        this.totalElements = totalElements;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Long getDefaultPageSize() {
        return defaultPageSize;
    }

    public void setDefaultPageSize(Long defaultPageSize) {
        this.defaultPageSize = defaultPageSize;
    }

    public List<String> getSort() {
        return sort;
    }

    public void setSort(List<String> sort) {
        this.sort = sort;
    }
}