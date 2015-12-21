package com.fzb.blog.model;

import com.jfinal.plugin.activerecord.Model;
import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

import java.util.List;
import java.util.Map;

public class Plugin extends Model<Plugin> {
    public static final Plugin dao = new Plugin();

    public List<Plugin> queryAll() {
        return find("select * from plugin where level>?", 0);
    }

    public void updatePluginStatus(String pluginName, Integer status) {
        Plugin plugin = findFirst("select * from plugin where pluginName=?", pluginName);
        if (plugin != null) {
            Map<String, Object> map = new JSONDeserializer<Map<String, Object>>().deserialize(plugin.getStr("content"));
            map.put("status", status);
            plugin.set("content", new JSONSerializer().serialize(map));
            plugin.update();
        }
    }
}
