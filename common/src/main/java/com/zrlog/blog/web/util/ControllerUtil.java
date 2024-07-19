package com.zrlog.blog.web.util;

import com.hibegin.common.util.StringUtils;
import com.hibegin.http.server.web.Controller;
import com.zrlog.common.rest.request.PageRequest;
import com.zrlog.common.rest.request.PageRequestImpl;
import com.zrlog.common.rest.request.UnPageRequestImpl;

public class ControllerUtil {

    /**
     * 分页参数
     */
    public static PageRequest getPageRequest(Controller controller) {
        return toPageRequest(controller, 10);
    }

    private static PageRequest toPageRequest(Controller controller, int defaultPageSize) {
        PageRequestImpl pageRequest = new PageRequestImpl();
        pageRequest.setSize((long) controller.getRequest().getParaToInt("size"));
        if (pageRequest.getSize() <= 0) {
            pageRequest.setSize((long) defaultPageSize);
        }
        pageRequest.setPage((long) controller.getRequest().getParaToInt("page"));
        if (pageRequest.getPage() <= 0) {
            pageRequest.setPage(1L);
        }
        pageRequest.setSort(controller.getRequest().getParaToStr("sidx"));
        if (StringUtils.isEmpty(pageRequest.getSort())) {
            pageRequest.setSort("id");
        }
        return pageRequest;
    }

    public static PageRequest unPageRequest() {
        return new UnPageRequestImpl(1L);
    }
}
