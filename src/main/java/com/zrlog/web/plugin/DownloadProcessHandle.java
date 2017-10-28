package com.zrlog.web.plugin;

import com.hibegin.common.util.http.handle.HttpHandle;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpRequestBase;

import java.io.*;

public class DownloadProcessHandle extends HttpHandle<Integer> implements Serializable {

    private int process;
    private File file;
    private long length;
    private Version version;

    public DownloadProcessHandle(Version version, File file) {
        this.file = file;
        this.version = version;
    }

    @Override
    public boolean handle(HttpRequestBase request, final HttpResponse response) {
        if (length <= 0) {
            if (response.getHeaders("Content-Length") != null) {
                length = Integer.valueOf(response.getFirstHeader("Content-Length").getValue());
            }
        }
        new Thread() {
            @Override
            public void run() {
                FileOutputStream fin;
                try {
                    fin = new FileOutputStream(file);
                    if (response.getEntity() != null) {
                        InputStream in = response.getEntity().getContent();
                        byte[] bytes = new byte[1024 * 4];
                        int tLength;
                        int count = 0;
                        while ((tLength = in.read(bytes)) != -1) {
                            fin.write(bytes, 0, tLength);
                            count += tLength;
                            process = (int) Math.ceil(count / (length * 1.0) * 100);
                            System.out.println("process = " + process);
                        }
                        fin.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
        return false;
    }

    public int getProcess() {
        return process;
    }

    public File getFile() {
        return file;
    }

    public Version getVersion() {
        return version;
    }
}
