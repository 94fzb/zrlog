package com.zrlog.web.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 当Controller的方法变更到了 {@link com.zrlog.model.BaseDataInitVO} 的数据时，需要清除缓存数据
 */
@Target(value = ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RefreshCache {
}
