package com.zrlog.admin.business.rest.request;

public class UpdateNavRequestRequest extends CreateNavRequest {

    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
