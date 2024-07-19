package com.zrlog.common;

import com.zrlog.common.type.RunMode;
import com.zrlog.util.I18nUtil;

import java.util.Objects;

public class ValidatorUtils {

    public static void doValid(Validator entry) {
        if (Constants.runMode == RunMode.NATIVE_AGENT) {
            return;
        }
        if (Objects.isNull(entry)) {
            throw new NullPointerException(I18nUtil.getBackendStringFromRes("entryNull"));
        }
        entry.doValid();
    }
}
