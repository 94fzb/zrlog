package com.fzb.blog.controlle;

import com.fzb.blog.model.Plugin;
import com.fzb.blog.util.LoadJarUtil;
import com.fzb.blog.util.plugin.PluginsUtil;
import com.fzb.blog.util.plugin.api.IZrlogPlugin;
import com.fzb.common.util.HttpUtil;
import com.fzb.common.util.IOUtil;
import com.fzb.common.util.ResponseData;
import com.fzb.common.util.ZipUtil;
import com.jfinal.kit.PathKit;
import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class PluginController extends ManageController {

    private static final Logger log = LoggerFactory
            .getLogger(QueryLogController.class);

    public void delete() {
        Plugin.dao.deleteById(getPara(0));
    }

    public void index() {
        queryAll();
    }

    public void queryAll() {

        String webPath = PathKit.getWebRootPath();
        File[] plugins = new File(webPath + "/admin/plugins/").listFiles();
        List<Map<String, Object>> plugin = new ArrayList<Map<String, Object>>();
        if (plugins != null) {
            for (File pluginFile : plugins) {
                Map<String, Object> map = new HashMap<String, Object>();
                if (pluginFile.getName().contains(".")) {
                    map.put("plugin", pluginFile.getName()
                            .substring(0, pluginFile.getName().indexOf(".")));
                } else {
                    map.put("plugin", pluginFile.getName());
                }
                map.put("author", "xiaochun");
                map.put("name", "plugin");
                map.put("digest", "");
                map.put("version", "1.0");
                plugin.add(map);
            }
        }
        setAttr("plugins", plugin);
        render("/admin/plugin.jsp");
    }

    @Override
    public void add() {

    }

    @Override
    public void update() {

    }

    public void start() {
        if (isNotNullOrNotEmptyStr(getPara("name"))) {
            String pName = getPara("name");
            IZrlogPlugin zPlugin = PluginsUtil.getPlugin(pName);
            if (zPlugin != null) {
                zPlugin.stop();
            }
            try {
                zPlugin = (IZrlogPlugin) Class.forName(
                        (String) getKey(pName, "classLoader")).newInstance();
                PluginsUtil.addPlugin(pName, zPlugin);
                setAttr("message", "插件开始运行了");
                new Plugin().updatePluginStatus(pName, 2);
            } catch (Exception e) {
                log.error("start plugin execption ", e);
                setAttr("message", "运行插件遇到了一些问题");
            }
        }
    }

    public void stop() {
        if (isNotNullOrNotEmptyStr(getPara("name"))) {
            String pName = getPara("name");
            IZrlogPlugin zPlugin = PluginsUtil.getPlugin(pName);
            if (zPlugin != null) {
                // TODO 为撒打包为Jar后无法移除了呢。
                // PluginsUtil.romvePlugin(pName);
                setAttr("message", "停用插件");
                PluginsUtil.getPluginsMap().remove(pName);
                new Plugin().updatePluginStatus(pName, 3);
            } else {
                setAttr("message", "不存在插件,或者插件没有运行");
            }
        }
    }

    public void unstall() {
        if (isNotNullOrNotEmptyStr(getPara("name"))) {
            String pName = getPara("name");
            IZrlogPlugin zPlugin = PluginsUtil.getPlugin(pName);
            if (zPlugin != null) {
                PluginsUtil.getPluginsMap().remove(pName);
                setAttr("message", "卸载插件");
                zPlugin.unstall();
                // TODO 删除解压的文件和数据库记录
                // new Plugin().updatePluginStatus(pName, 2);
            } else {
                setAttr("message", "不存在插件,或者插件没有运行");
            }
        }
    }

    public void install() {
        if (isNotNullOrNotEmptyStr(getPara("name"))) {
            String pName = getPara("name");
            IZrlogPlugin zPlugin = PluginsUtil.getPlugin(pName);
            if (zPlugin == null) {
                // TODO
                Map<String, Object> paramMap = new HashMap<String, Object>();
                Map<String, String[]> tparamMap = getParaMap();
                for (Entry<String, String[]> param : tparamMap.entrySet()) {
                    paramMap.put(param.getKey(), param.getValue()[0]);
                }
                paramMap.remove("name");
                Object tPlugin;
                try {
                    Map<String, Object> map = getPluginMsgByZipFileName(pName);
                    if (isNotNullOrNotEmptyStr(getPara("step"))) {
                        setAttr("mTitle", "插件设置");
                        setAttr("sTitle", map.get("desc") + " 设置");
                        getRequest().getRequestDispatcher(
                                "/admin/page.jsp?include=plugins/" + pName
                                        + "/html/" + map.get("page")).forward(
                                getRequest(), getResponse());
                        return;
                    }
                    Thread.currentThread().getContextClassLoader()
                            .loadClass(map.get("classLoader").toString());
                    tPlugin = Class.forName(map.get("classLoader").toString())
                            .newInstance();
                    if (tPlugin instanceof IZrlogPlugin) {
                        ((IZrlogPlugin) tPlugin).install(paramMap);
                    }
                    setAttr("message", "安装成功,<a href='plugin/start?name="
                            + pName + "'>点击开始运行</a>");
                } catch (Exception e) {
                    log.error("plugin install exception ", e);
                }
            } else {
                setAttr("message", "插件已经在运行了");
            }
        }
    }

    public void download() {
        try {
            ResponseData<File> data = new ResponseData<File>() {
            };
            HttpUtil.getResponse(getPara("host") + "/plugin/download?id="
                    + getParaToInt("id"), data, PathKit.getWebRootPath()
                    + "/admin/plugins/");
            String folerName = data
                    .getT()
                    .getName()
                    .substring(0, data.getT().getName().indexOf("."));
            Map<String, Object> map = getPluginMsgByZipFileName(folerName);
            setAttr("mTitle", "插件设置");
            setAttr("sTitle", map.get("desc") + " 设置");
            getRequest().getRequestDispatcher(
                    "/admin/page.jsp?include=plugins/" + folerName + "/html/"
                            + map.get("page")).forward(getRequest(),
                    getResponse());
        } catch (Exception e) {
            log.error("download plugin exception ", e);
        }
    }

    private Object getKey(String pluginName, String key) {
        Plugin plugin = Plugin.dao.findFirst(
                "select * from plugin where pluginName=?", pluginName);
        return new JSONDeserializer<Map<String, Object>>().deserialize(
                plugin.getStr("content")).get(key);
    }

    private Map<String, Object> getPluginMsgByZipFileName(String pluginName)
            throws IOException {
        String pluginPath = PathKit.getWebRootPath() + "/admin/plugins/"
                + pluginName + "";
        String webLibPath = PathKit.getWebRootPath() + "/WEB-INF/";
        String classPath = PathKit.getWebRootPath() + "/WEB-INF/";
        ZipUtil.unZip(pluginPath + ".zip", pluginPath + "/temp/");
        String installStr;
        installStr = IOUtil.getStringInputStream(new FileInputStream(pluginPath
                + "/temp/installGuide.txt"));
        String installArgs[] = installStr.split("\r\n");
        Map<String, Object> tmap = new HashMap<String, Object>();
        for (String arg : installArgs) {
            tmap.put(arg.split(":")[0],
                    arg.substring(arg.split(":")[0].length() + 1));
        }
        IOUtil.moveOrCopy(pluginPath + "/temp/html/", pluginPath, false);
        IOUtil.moveOrCopy(pluginPath + "/temp/lib/", webLibPath, false);
        IOUtil.moveOrCopy(pluginPath + "/temp/classes/", classPath, false);
        File[] jarFiles = new File(pluginPath + "/temp/lib/").listFiles();
        try {
            LoadJarUtil.loadJar(jarFiles);
        } catch (URISyntaxException e) {
            log.error("loadJar error ", e);
        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("status", 0);
        map.put("classLoader", tmap.get("classLoader"));
        map.put("author", tmap.get("author"));
        map.put("desc", tmap.get("instruction"));
        map.put("page", tmap.get("html"));
        map.put("version", tmap.get("version"));
        Plugin plugin = new Plugin().set("pluginName", pluginName)
                .set("content", new JSONSerializer().serialize(map))
                .set("level", -1);
        Plugin id = Plugin.dao.findFirst(
                "select pluginId from plugin where pluginName=?", pluginName);
        if (id == null) {
            plugin.save();
        }
        return map;
    }
}
