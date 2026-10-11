package com.zrlog.updater;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.Strictness;

final class Json {
    static final Gson GSON = new GsonBuilder().setStrictness(Strictness.STRICT).disableJdkUnsafe().create();

    private Json() { }

    static <T> T read(String text, Class<T> type) {
        try {
            T result = GSON.fromJson(text, type);
            if (result == null) { throw new IllegalArgumentException("null JSON"); }
            return result;
        } catch (RuntimeException error) {
            throw ApiException.invalid("Invalid JSON document");
        }
    }

    static <T> T copy(T value, Class<T> type) { return read(GSON.toJson(value), type); }

    static JsonElement tree(String text) { return JsonParser.parseString(text); }
}
