package com.zrlog.admin.business.service;

import com.zrlog.admin.business.rest.response.StatisticsInfoResponse;
import com.zrlog.model.Comment;
import com.zrlog.model.Log;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class AdminStatisticsService {
    public CompletableFuture<StatisticsInfoResponse> statisticsInfo(Executor executor) {
        return CompletableFuture.supplyAsync(() -> {
            StatisticsInfoResponse info = new StatisticsInfoResponse();
            List<CompletableFuture<Void>> futures = new ArrayList<>();
            futures.add(CompletableFuture.runAsync(() -> {
                try {
                    info.setCommCount(new Comment().count());
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }, executor));
            futures.add(CompletableFuture.runAsync(() -> {
                try {
                    info.setToDayCommCount(new Comment().countToDayComment());
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }, executor));
            futures.add(CompletableFuture.runAsync(() -> {
                try {
                    info.setClickCount(new Log().sumClick().longValue());
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }, executor));
            futures.add(CompletableFuture.runAsync(() -> {
                info.setArticleCount(new Log().getAdminCount());
            }, executor));
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
            return info;
        }, executor);
    }
}
