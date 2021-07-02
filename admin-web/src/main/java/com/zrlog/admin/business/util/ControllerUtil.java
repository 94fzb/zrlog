package com.zrlog.admin.business.util;

import com.jfinal.core.Controller;
import com.zrlog.common.rest.request.PageRequest;

public class ControllerUtil {

    /**
     * 分页参数
     */
    public static PageRequest getPageRequest(Controller controller) {
        PageRequest pageRequest = new PageRequest();
        pageRequest.setSize(controller.getParaToInt("size", 10));
        pageRequest.setSort(controller.getPara("sidx"));
        pageRequest.setOrder(controller.getPara("sord"));
        pageRequest.setPage(controller.getParaToInt("page", 1));
        return pageRequest;
    }
}
