package com.fzb.common.util.http.handle;

import com.fzb.common.util.IOUtil;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpRequestBase;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Random;

public class HttpFileHandle extends HttpHandle<File> {

    private String filePath;

    public HttpFileHandle(String filePath) {
        this.filePath = filePath;
    }

    private static String randomFile() {
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        return df.format(new Date()) + "_" + (new Random()).nextInt(1000);
    }

    @Override
    public boolean handle(HttpRequestBase request, HttpResponse response) {
        String path = request.getURI().getPath();
        if (response.getHeaders("Content-Disposition") != null) {
            String[] strings = Arrays.toString(response.getHeaders("Content-Disposition")).split(",");
            for (String str : strings) {
                String[] tArr = str.split(";");
                if ("attachment".equals(tArr[0])) {
                    try {
                        setT(new File(filePath + new String(tArr[1].split("=")[1].getBytes("ISO-8859-1"), "UTF-8")));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        if (getT() == null) {
            if (path.contains(".")) {
                setT(new File(filePath + path.substring(path.lastIndexOf('/'))));
            } else {
                setT(new File(filePath + path.substring(path.lastIndexOf('/')) + "." + randomFile()));
            }
        }
        FileOutputStream fin;
        try {
            fin = new FileOutputStream(getT());
            if (response.getEntity() != null) {
                fin.write(IOUtil.getByteByInputStream(response.getEntity().getContent()));
                fin.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
