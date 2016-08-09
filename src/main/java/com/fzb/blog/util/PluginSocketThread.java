package com.fzb.blog.util;

import com.fzb.common.util.IOUtil;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.Socket;

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
