package com.fzb.blog.common.response;

import java.util.ArrayList;
import java.util.List;

public class PageableResponse<T> {

    private long total;
    private long records;
    private long page;
    private List<T> rows = new ArrayList<T>();

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public List<T> getRows() {
        return rows;
    }

    public void setRows(List<T> rows) {
        this.rows = rows;
    }

    public long getRecords() {
        return records;
    }

    public void setRecords(long records) {
        this.records = records;
    }

    public long getPage() {
        return page;
    }

    public void setPage(long page) {
        this.page = page;
    }
}
