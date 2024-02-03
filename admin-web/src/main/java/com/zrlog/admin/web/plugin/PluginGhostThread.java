package com.zrlog.admin.web.plugin;

import com.hibegin.common.util.IOUtil;
import com.hibegin.common.util.LoggerUtil;

import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 处理由于未知原因插件异常停止后，还可以通过命令重新加载，保证插件的高可用。
 */
class PluginGhostThread extends Thread {

    private static final Logger LOGGER = LoggerUtil.getLogger(PluginGhostThread.class);
    private final int port;
    private final String host;

    private boolean stop;

    public PluginGhostThread(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public void run() {
        //使用Socket的方式进行监听，如果插件服务停止后，那么SocketServer也会被关闭，方法堵塞结束，标记插件服务停止。
        Socket socket = null;
        while (!stop) {
            try {
                Thread.sleep(1000);
                socket = new Socket(host, port);
                break;
            } catch (Exception e) {
                //LOGGER.warn("plugin exception stop ", e);
            }
        }
        try {
            if (socket != null) {
                IOUtil.getByteByInputStream(socket.getInputStream());
            }
            stop = true;
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "plugin exception stop ", e);
        }
    }

    public boolean isStop() {
        return stop;
    }

    public void setStop(boolean stop) {
        this.stop = stop;
    }
}
