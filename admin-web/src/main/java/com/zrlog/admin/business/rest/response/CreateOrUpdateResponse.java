package com.zrlog.admin.business.rest.response;

public class CreateOrUpdateResponse {

    private final Long id;

    public CreateOrUpdateResponse(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
