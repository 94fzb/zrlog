package com.zrlog.common;

import com.zrlog.util.I18nUtil;

import java.util.Objects;

public class ValidatorUtils {

    public static void doValid(Validator entry) {
        if (Objects.isNull(entry)) {
            throw new NullPointerException(I18nUtil.getBackendStringFromRes("entryNull"));
        }
        entry.doValid();
    }
}
