package com.zrlog.web.plugin;

import com.hibegin.common.util.SecurityUtils;
import com.hibegin.common.util.http.handle.HttpHandle;
import com.zrlog.common.vo.Version;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpRequestBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class DownloadProcessHandle extends HttpHandle<Integer> implements Serializable {

    private static final Logger LOGGER = LoggerFactory.getLogger(DownloadProcessHandle.class);

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
        if (length <= 0 && response.getHeaders("Content-Length") != null) {
            length = Integer.valueOf(response.getFirstHeader("Content-Length").getValue());
        }
        new Thread() {
            @Override
            public void run() {
                try (FileOutputStream fin = new FileOutputStream(file)) {
                    if (response.getEntity() != null) {
                        InputStream in = response.getEntity().getContent();
                        byte[] bytes = new byte[1024 * 4];
                        int tLength;
                        int count = 0;
                        while ((tLength = in.read(bytes)) != -1) {
                            fin.write(bytes, 0, tLength);
                            count += tLength;
                            process = (int) Math.ceil(count / (length * 1.0) * 100);
                            LOGGER.info("process = " + process);
                        }
                    }
                    if (!SecurityUtils.md5(new FileInputStream(file)).equals(version.getMd5sum())) {
                        file.delete();
                    }
                } catch (IOException e) {
                    LOGGER.error("", e);
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
