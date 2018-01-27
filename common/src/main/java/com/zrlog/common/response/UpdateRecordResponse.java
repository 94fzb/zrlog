package com.zrlog.common.response;

public class UpdateRecordResponse extends StandardResponse {

    public UpdateRecordResponse() {
    }

    public UpdateRecordResponse(Boolean success) {
        setError(success ? 0 : 1);
    }
}
