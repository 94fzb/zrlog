package com.zrlog.data.service;

import com.google.gson.Gson;
import com.hibegin.common.dao.ResultValueConvertUtils;
import com.hibegin.common.dao.dto.PageRequestImpl;
import com.hibegin.common.util.BeanUtil;
import com.hibegin.common.util.LoggerUtil;
import com.zrlog.data.cache.vo.BaseDataInitVO;
import com.zrlog.data.cache.vo.HotLogBasicInfoEntry;
import com.zrlog.data.cache.vo.HotTypeLogInfo;
import com.zrlog.model.*;

import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class BaseDataDbService {

    private static final Logger LOGGER = LoggerUtil.getLogger(BaseDataDbService.class);

    private HotLogBasicInfoEntry convertToBasicVO(Map<String, Object> log) {
        String format = "yyyy-MM-dd";
        log.put("releaseTime", ResultValueConvertUtils.formatDate(log.get("releaseTime"), format));
        log.put("lastUpdateDate", ResultValueConvertUtils.formatDate(log.get("last_update_date"), format));
        log.put("last_update_date", ResultValueConvertUtils.formatDate(log.get("last_update_date"), format));
        return BeanUtil.convert(log, HotLogBasicInfoEntry.class);
    }


    public BaseDataInitVO queryCacheInit(Executor executor) {
        BaseDataInitVO cacheInit = new BaseDataInitVO();
        //first set website info
        Map<String, Object> refreshWebSite = new WebSite().getPublicWebSite();
        cacheInit.setWebSite(refreshWebSite);
        cacheInit.setWebSiteVersion((long) new Gson().toJson(refreshWebSite).hashCode());
        List<CompletableFuture<Void>> futures = new ArrayList<>();
        BaseDataInitVO.Statistics statistics = new BaseDataInitVO.Statistics();
        futures.add(CompletableFuture.runAsync(() -> {
            statistics.setTotalArticleSize(new Log().getVisitorCount());
            cacheInit.setStatistics(statistics);
        }, executor));
        futures.add(CompletableFuture.runAsync(() -> {
            try {
                cacheInit.setLinks(new Link().findAll());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }, executor));
        futures.add(CompletableFuture.runAsync(() -> {
            try {
                cacheInit.setTypes(new Type().findAll());
                statistics.setTotalTypeSize((long) cacheInit.getTypes().size());
                List<Map<String, Object>> types = cacheInit.getTypes();
                List<HotTypeLogInfo> indexHotLog = new ArrayList<>();
                cacheInit.setTypeHotLogs(indexHotLog);
                //设置分类Hot
                for (Map<String, Object> type : types) {
                    futures.add(CompletableFuture.runAsync(() -> {
                        HotTypeLogInfo hotTypeLogInfo = new HotTypeLogInfo();
                        String alias = (String) type.get("alias");
                        hotTypeLogInfo.setTypeAlias(alias);
                        hotTypeLogInfo.setTypeName((String) type.get("typeName"));
                        hotTypeLogInfo.setTypeId(((Number) type.get("typeId")).longValue());
                        hotTypeLogInfo.setLogs(new Log().findByTypeAlias(1, 6, alias).getRows().stream().map(this::convertToBasicVO).collect(Collectors.toList()));
                        indexHotLog.add(hotTypeLogInfo);
                    }, executor));
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }, executor));
        futures.add(CompletableFuture.runAsync(() -> {
            //Last article
            cacheInit.setHotLogs(new Log().visitorFind(new PageRequestImpl(1L, 6L), "").getRows().stream().map(this::convertToBasicVO).collect(Collectors.toList()));
        }, executor));
        futures.add(CompletableFuture.runAsync(() -> {
            try {
                cacheInit.setLogNavs(new LogNav().findAll());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }, executor));
        futures.add(CompletableFuture.runAsync(() -> {
            try {
                cacheInit.setPlugins(new Plugin().findAll());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }, executor));
        futures.add(CompletableFuture.runAsync(() -> {
            try {
                cacheInit.setArchives(new Log().getArchives());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }, executor));
        futures.add(CompletableFuture.runAsync(() -> {
            try {
                cacheInit.setTags(new Tag().refreshTag());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            statistics.setTotalTagSize((long) cacheInit.getTags().size());
        }, executor));
        try {
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();
        } catch (Exception e) {
            LOGGER.warning("Load data error " + e.getMessage());
        }
        if (cacheInit.getTags() == null || cacheInit.getTags().isEmpty()) {
            cacheInit.getPlugins().stream().filter(e -> Objects.equals(e.get("pluginName"), "tags")).findFirst().ifPresent(e -> {
                cacheInit.getPlugins().remove(e);
            });
        }
        if (cacheInit.getArchives() == null || cacheInit.getArchives().isEmpty()) {
            cacheInit.getPlugins().stream().filter(e -> Objects.equals(e.get("pluginName"), "archives")).findFirst().ifPresent(e -> {
                cacheInit.getPlugins().remove(e);
            });
        }
        if (cacheInit.getTypes() == null || cacheInit.getTypes().isEmpty()) {
            cacheInit.getPlugins().stream().filter(e -> Objects.equals(e.get("pluginName"), "types")).findFirst().ifPresent(e -> {
                cacheInit.getPlugins().remove(e);
            });
        }
        if (cacheInit.getLinks() == null || cacheInit.getLinks().isEmpty()) {
            cacheInit.getPlugins().stream().filter(e -> Objects.equals(e.get("pluginName"), "links")).findFirst().ifPresent(e -> {
                cacheInit.getPlugins().remove(e);
            });
        }
        cacheInit.getTypeHotLogs().sort(Comparator.comparing(x -> Math.toIntExact(x.getTypeId())));
        //默认开启文章封面
        cacheInit.getWebSite().putIfAbsent("article_thumbnail_status", "1");
        return cacheInit;
    }
}
