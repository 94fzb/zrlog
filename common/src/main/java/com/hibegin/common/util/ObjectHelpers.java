package com.hibegin.common.util;

import java.util.Objects;
import java.util.function.Supplier;

public class ObjectHelpers {

    public static <T> T requireNonNullElse(T obj, T defaultObj) {
        return (T)(obj != null ? obj : Objects.requireNonNull(defaultObj, "defaultObj"));
    }

    public static <T> T requireNonNullElseGet(T obj, Supplier<? extends T> supplier) {
        return (T)(obj != null ? obj : Objects.requireNonNull(((Supplier)Objects.requireNonNull(supplier, "supplier")).get(), "supplier.get()"));
    }
}
