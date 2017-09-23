package com.fzb.blog.util;

import com.fzb.blog.common.response.PageableResponse;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 与实体相关的工具类
 */
public class BeanUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(BeanUtil.class);

    /**
     * 将输入的分页过后的对象，转化PageableResponse的对象
     *
     * @param object
     * @param toClazz
     * @param <T>
     * @return
     */
    public static <T> PageableResponse<T> convertPageable(Object object, Class<T> toClazz) {
        String jsonStr = new Gson().toJson(object);
        PageableResponse pageableResponse = new Gson().fromJson(jsonStr, PageableResponse.class);
        List<T> dataList = new ArrayList<>();
        List oldDataList = pageableResponse.getRows();
        for (Object obj : oldDataList) {
            dataList.add(convert(obj, toClazz));
        }
        PageableResponse<T> response = new PageableResponse<T>();
        response.setPage(pageableResponse.getPage());
        response.setTotal(pageableResponse.getTotal());
        response.setRecords(pageableResponse.getRecords());
        response.setRows(dataList);
        return response;
    }

    private static <T> T convert(Object obj, Class<T> tClass) {
        String jsonStr = new Gson().toJson(obj);
        return new Gson().fromJson(jsonStr, tClass);
    }

    public static <T> T cloneObject(Object obj) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = null;
        ObjectInputStream objectInputStream = null;
        try {
            objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(obj);
            objectOutputStream.close();
            objectInputStream = new ObjectInputStream(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));
            T t = (T) objectInputStream.readObject();
            objectInputStream.close();
            return t;
        } catch (IOException | ClassNotFoundException e) {
            LOGGER.error("", e);
        } finally {
            if (objectInputStream != null) {
                try {
                    objectInputStream.close();
                } catch (IOException e) {
                    LOGGER.error("", e);
                }
            }
            if (objectOutputStream != null) {
                try {
                    objectOutputStream.close();
                } catch (IOException e) {
                    LOGGER.error("", e);
                }
            }
        }
        return null;
    }
}
