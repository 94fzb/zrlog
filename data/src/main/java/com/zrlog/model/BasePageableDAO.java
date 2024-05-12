package com.zrlog.model;

import com.hibegin.dao.DAO;
import com.zrlog.common.rest.request.PageRequest;
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
                try {
                    List<Object> params = new ArrayList<>(Arrays.stream(obj).toList());
                    params.add(pageRequest.getOffset());
                    params.add(pageRequest.getSize());
                    data.setRows(this.queryListWithParams(sql + " limit  ?,?", params.toArray()));
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
        return data;
    }
}
