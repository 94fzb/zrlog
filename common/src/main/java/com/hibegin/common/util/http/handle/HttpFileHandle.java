package com.hibegin.common.util.http.handle;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.Random;

public class HttpFileHandle extends HttpHandle<File> {

    private final String filePath;

    public HttpFileHandle(String filePath) {
        this.filePath = filePath;
    }

    private static String randomFile() {
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        return df.format(new Date()) + "_" + (new Random()).nextInt(1000);
    }

    @Override
    public boolean handle(HttpRequest request, HttpResponse<InputStream> response) {
        String path = request.uri().getPath();
        String contentDis = response.headers().firstValue("Content-Disposition").orElse(null);
        if (Objects.nonNull(contentDis)) {
            String[] strings = contentDis.split(",");
            for (String str : strings) {
                String[] tArr = str.split(";");
                if ("attachment".equals(tArr[0])) {
                    setT(new File(filePath + new String(tArr[1].split("=")[1].getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8)));
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
        try {
            Files.copy(response.body(), Paths.get(getT().toString()), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
