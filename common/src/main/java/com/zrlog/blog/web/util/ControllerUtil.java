package com.zrlog.blog.web.util;

import com.hibegin.http.server.web.Controller;
import com.zrlog.common.rest.request.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ControllerUtil {

    /**
     * 分页参数
     */
    public static PageRequest getPageRequest(Controller controller) {
        return toPageRequest(controller, 10);
    }

    public static PageRequest toPageRequest(Controller controller, int defaultPageSize) {
        PageRequestImpl pageRequest = new PageRequestImpl();
        pageRequest.setSize((long) controller.getRequest().getParaToInt("size"));
        if (pageRequest.getSize() <= 0) {
            pageRequest.setSize((long) defaultPageSize);
        }
        pageRequest.setPage((long) controller.getRequest().getParaToInt("page"));
        if (pageRequest.getPage() <= 0) {
            pageRequest.setPage(1L);
        }
        pageRequest.setOrders(getOrderByListByParamMap(controller.getRequest().getParamMap()));
        return pageRequest;
    }

    private static List<OrderBy> getOrderByListByParamMap(Map<String, String[]> paramMap) {
        String[] sorts = paramMap.get("sort");
        if (Objects.isNull(sorts) || sorts.length == 0) {
            //兼容旧的 key
            sorts = paramMap.get("sidx");
        }
        if (Objects.isNull(sorts) || sorts.length == 0) {
            return List.of(new OrderBy("id", Direction.DESC));
        }
        List<OrderBy> orders = new ArrayList<>(sorts.length);
        for (String sort : sorts) {
            String[] args = sort.split(",");
            String key = args[0].trim();
            if (args.length > 1) {
                orders.add(new OrderBy(key, Direction.valueOf(args[1].toUpperCase())));
            } else {
                orders.add(new OrderBy(key, Direction.DESC));
            }
        }
        return orders;
    }

    public static PageRequest unPageRequest() {
        return new UnPageRequestImpl(1L);
    }
}
