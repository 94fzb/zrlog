package com.zrlog.admin.business.util;

import com.hibegin.http.server.web.Controller;
import com.zrlog.common.rest.request.PageRequest;

public class ControllerUtil {

    /**
     * 分页参数
     */
    public static PageRequest getPageRequest(Controller controller) {
        PageRequest pageRequest = new PageRequest();
        pageRequest.setSize(controller.getRequest().getParaToInt("size"));
        pageRequest.setSort(controller.getRequest().getParaToStr("sidx"));
        pageRequest.setPage(controller.getRequest().getParaToInt("page"));
        return pageRequest;
    }
}
