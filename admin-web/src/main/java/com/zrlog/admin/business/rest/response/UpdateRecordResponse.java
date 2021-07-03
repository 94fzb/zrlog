package com.zrlog.admin.business.rest.response;

import com.zrlog.common.rest.response.StandardResponse;

public class UpdateRecordResponse extends StandardResponse {

    public UpdateRecordResponse() {
    }

    public UpdateRecordResponse(Boolean success) {
        setError(success ? 0 : 1);
    }
}
