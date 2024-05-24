package com.zrlog.model;

import com.hibegin.dao.DAO;
import com.zrlog.common.rest.request.PageRequest;
import com.zrlog.common.rest.request.PageRequestImpl;
import com.zrlog.common.rest.request.UnPageRequestImpl;
import com.zrlog.data.dto.PageData;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class BasePageableDAO extends DAO {

    PageData<Map<String, Object>> queryPageData(String sql, PageRequest pageRequest, Object[] obj) {
        PageData<Map<String, Object>> data = new PageData<>();
        try (ExecutorService executors = Executors.newVirtualThreadPerTaskExecutor()) {
            List<CompletableFuture<Void>> tasks = new ArrayList<>();
            tasks.add(CompletableFuture.runAsync(() -> {
                //无需查询具体数据
                if (pageRequest.getSize() <= 0) {
                    data.setRows(new ArrayList<>());
                    return;
                }
                try {
                    List<Object> params = new ArrayList<>(Arrays.stream(obj).toList());
                    //需要分页对象
                    if (pageRequest instanceof PageRequestImpl) {
                        params.add(pageRequest.getOffset());
                        params.add(pageRequest.getSize());
                        data.setRows(this.queryListWithParams(sql + " limit  ?,?", params.toArray()));
                    } else {
                        data.setRows(this.queryListWithParams(sql, params.toArray()));
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }, executors));
            tasks.add(CompletableFuture.runAsync(() -> {
                try {
                    data.setTotalElements((long) this.queryFirstObj("select count(1) cnt from (" + sql + ") as subquery", obj));
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }, executors));
            try {
                CompletableFuture.allOf(tasks.toArray(new CompletableFuture[0])).get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
        data.setPage(pageRequest.getPage());
        if (pageRequest instanceof UnPageRequestImpl) {
            data.setSize(Math.max(data.getRows().size(), 1000000L));
        } else {
            data.setSize(pageRequest.getSize());
        }
        return data;
    }
}
