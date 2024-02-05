package com.zrlog.admin.business.util;

import com.hibegin.common.util.StringUtils;
import com.hibegin.http.server.web.Controller;
import com.zrlog.common.rest.request.PageRequest;

public class ControllerUtil {

    /**
     * 分页参数
     */
    public static PageRequest getPageRequest(Controller controller) {
        PageRequest pageRequest = new PageRequest();
        pageRequest.setSize(controller.getRequest().getParaToInt("size"));
        if(pageRequest.getSize() <= 0){
            pageRequest.setSize(10);
        }
        pageRequest.setPage(controller.getRequest().getParaToInt("page"));
        if(pageRequest.getPage() <= 0){
            pageRequest.setPage(1);
        }
        pageRequest.setSort(controller.getRequest().getParaToStr("sidx"));
        if(StringUtils.isEmpty(pageRequest.getSort())){
            pageRequest.setSort("id");
        }
        return pageRequest;
    }
}
