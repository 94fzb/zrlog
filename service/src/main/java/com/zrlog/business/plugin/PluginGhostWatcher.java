package com.zrlog.business.plugin;

import com.hibegin.common.util.IOUtil;
import com.hibegin.common.util.LoggerUtil;

import java.net.Socket;
import java.util.logging.Logger;

/**
 * 处理由于未知原因插件异常停止后，还可以通过命令重新加载，保证插件的高可用。
 */
class PluginGhostWatcher {

    private static final Logger LOGGER = LoggerUtil.getLogger(PluginGhostWatcher.class);
    private final int port;
    private final String host;

    public PluginGhostWatcher(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void doWatch() {
        //使用Socket的方式进行监听，如果插件服务停止后，那么SocketServer也会被关闭，标记插件服务停止。
        try {
            //待插件启动
            Thread.sleep(3000);
            Socket socket = new Socket(host, port);
            IOUtil.getStringInputStream(socket.getInputStream());
        } catch (Exception e) {
            LOGGER.warning("Plugin exception stop " + e.getMessage());
        }
    }
}
