package com.fzb.blog.util;

import com.fzb.blog.common.response.PageableResponse;
import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

import java.util.ArrayList;
import java.util.List;

public class BeanUtil {

    public static <T> PageableResponse<T> convertPageable(Object object, Class<T> toClazz) {
        String jsonStr = new JSONSerializer().deepSerialize(object);
        PageableResponse pageableResponse = new JSONDeserializer<PageableResponse>().deserialize(jsonStr, PageableResponse.class);
        List<T> dataList = new ArrayList<T>();
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
        String jsonStr = new JSONSerializer().deepSerialize(obj);
        return new JSONDeserializer<T>().deserialize(jsonStr, tClass);
    }
}
