package com.zrlog.updater;

final class ApiException extends RuntimeException {
    final int status;
    final String code;

    ApiException(int status, String code, String message) {
        super(message);
        this.status = status;
        this.code = code;
    }

    static ApiException invalid(String message) {
        return new ApiException(400, "invalid_request", message);
    }
}
