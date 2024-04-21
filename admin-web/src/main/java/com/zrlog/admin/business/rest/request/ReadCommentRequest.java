package com.zrlog.admin.business.rest.request;

import com.zrlog.common.Validator;

public class ReadCommentRequest implements Validator {

    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public void doValid() {

    }
}
