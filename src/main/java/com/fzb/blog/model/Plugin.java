package com.fzb.blog.model;

import com.jfinal.plugin.activerecord.Model;

import java.util.List;

public class Plugin extends Model<Plugin> {
    public static final Plugin dao = new Plugin();

    public List<Plugin> queryAll() {
        return find("select * from plugin where level>?", 0);
    }
}
