package com.zrlog.business.type;

public enum TestConnectDbResult {
    SUCCESS(0), DB_NOT_EXISTS(1), CREATE_CONNECT_ERROR(2), USERNAME_OR_PASSWORD_ERROR(3), SQL_EXCEPTION_UNKNOWN(4), MISSING_MYSQL_DRIVER(5), UNKNOWN(6);
    private final int error;

    TestConnectDbResult(int error) {
        this.error = error;
    }

    public int getError() {
        return error;
    }
}
