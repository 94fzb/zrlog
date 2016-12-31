package com.fzb.blog.web.plugin;

import com.fzb.common.util.IOUtil;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.Socket;

/**
 * 处理由于未知原因插件异常停止后，还可以通过命令重新加载，保证插件的高可用。
 */
public class PluginSocketThread extends Thread {

    private static final Logger LOGGER = Logger.getLogger(PluginSocketThread.class);
    private int port;
    private String host;

    private boolean stop;

    public PluginSocketThread(String host, int port) {
        this.host = host;
        this.port = port;
    }

    @Override
    public void run() {
        //使用Socket的方式进行监听，如果插件服务停止后，那么SocketServer也会被关闭，方法堵塞结束，标记插件服务停止。
        Socket socket;
        while (true) {
            try {
                Thread.sleep(1000);
                socket = new Socket(host, port);
                break;
            } catch (Exception e) {
                //LOGGER.warn("plugin exception stop ", e);
            }
        }
        try {
            IOUtil.getByteByInputStream(socket.getInputStream());
            stop = true;
        } catch (IOException e) {
            LOGGER.warn("plugin exception stop ", e);
        }
    }

    public boolean isStop() {
        return stop;
    }
}
