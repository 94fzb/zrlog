package com.zrlog.admin.business.rest.response;

public class UpdateRecordResponse extends AdminApiPageDataStandardResponse<Object> {

    public UpdateRecordResponse() {
    }

    public UpdateRecordResponse(Boolean success) {
        setError(success ? 0 : 1);
    }
}
