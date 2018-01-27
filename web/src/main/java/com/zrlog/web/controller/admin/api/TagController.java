package com.zrlog.web.controller.admin.api;

import com.zrlog.model.Tag;
import com.zrlog.web.controller.BaseController;

import java.util.Map;

public class TagController extends BaseController {

    public Map index() {
        Tag.dao.refreshTag();
        return Tag.dao.find(getPageable());
    }
}
