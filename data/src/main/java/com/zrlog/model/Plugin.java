package com.zrlog.model;

import com.jfinal.plugin.activerecord.Model;

import java.util.List;

/**
 * 存放了与侧边栏相关的数据，主要用于保存侧边栏的key，对应数据库的plugin表（目前实现方式不友好，建议直接使用主题设置，替换该设置）
 */
public class Plugin extends Model<Plugin> {
    public static final String TABLE_NAME = "plugin";

    public List<Plugin> find() {
        return find("select * from " + TABLE_NAME + " where level>?", 0);
    }
}
