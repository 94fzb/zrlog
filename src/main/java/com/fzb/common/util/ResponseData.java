package com.fzb.common.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class ResponseData<T> {
	private T t;
	
	public Class<T> getClazz(){
        Type sType = getClass().getGenericSuperclass();
        Type[] generics = ((ParameterizedType) sType).getActualTypeArguments();
        @SuppressWarnings("unchecked")
		Class<T> mTClass = (Class<T>) (generics[0]);
        return mTClass;
    }

	public T getT() {
		return t;
	}

	@SuppressWarnings("unchecked")
	public void setT(Object t) {
		this.t =(T) t;
	}
}
