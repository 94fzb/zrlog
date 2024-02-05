package com.hibegin.common.util;

import com.google.gson.Gson;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 与实体相关的工具类
 */
public class BeanUtil {

    private static final Logger LOGGER = LoggerUtil.getLogger(BeanUtil.class);

    public static <T> T convert(Object obj, Class<T> tClass) {
        String jsonStr = new Gson().toJson(obj);
        return new Gson().fromJson(jsonStr, tClass);
    }

    public static <T> T convert(InputStream inputStream, Class<T> tClass) {
        return new Gson().fromJson(IOUtil.getStringInputStream(inputStream), tClass);
    }

    public static <T> T cloneObject(Object obj) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)) {
            objectOutputStream.writeObject(obj);
            try (ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()))) {
                return (T) objectInputStream.readObject();
            }
        } catch (IOException | ClassNotFoundException e) {
            LOGGER.log(Level.SEVERE, "", e);
            throw new RuntimeException(e);
        }
    }
}
