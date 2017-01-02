package com.fzb.blog.model;

import com.jfinal.plugin.activerecord.Model;

import java.util.List;

/**
 * 存放了与侧边栏相关的数据，主要用于保存侧边栏的key，对应数据库的plugin表（目前实现方式不友好，建议直接使用主题设置，替换该设置）
 */
public class Plugin extends Model<Plugin> {
    public static final Plugin dao = new Plugin();

    public List<Plugin> queryAll() {
        return find("select * from plugin where level>?", 0);
    }
}
