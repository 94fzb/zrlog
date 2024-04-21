package com.hibegin.common.util.http.handle;


import com.hibegin.common.util.LoggerUtil;
import com.hibegin.common.util.StringUtils;

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
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;
import java.util.Random;
import java.util.logging.Logger;

public class HttpFileHandle extends HttpHandle<File> {

    private static final Logger LOGGER = LoggerUtil.getLogger(HttpFileHandle.class);

    private final String filePath;
    private final String fileName;

    public HttpFileHandle(String filePath) {
        this.filePath = filePath;
        this.fileName = "";
    }

    public HttpFileHandle(String filePath, String fileName) {
        this.filePath = filePath;
        this.fileName = fileName;
    }

    private String getFileName(String uriPath) {
        if (StringUtils.isEmpty(fileName)) {
            return Arrays.asList(uriPath.split("/")).getLast();
        }
        return fileName;
    }

    @Override
    public boolean handle(HttpRequest request, HttpResponse<InputStream> response) {
        String contentDis = response.headers().firstValue("Content-Disposition").orElse(null);
        if (Objects.nonNull(contentDis)) {
            String[] strings = contentDis.split(",");
            for (String str : strings) {
                String[] tArr = str.split(";");
                if ("attachment".equals(tArr[0])) {
                    setT(new File(filePath + "/" + new String(tArr[1].split("=")[1].getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8)));
                }
            }
        }
        if (getT() == null) {
            setT(new File(filePath + "/" + getFileName(request.uri().getPath())));
        }
        try {
            Files.copy(response.body(), Paths.get(getT().toString()), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            LOGGER.warning("FileHandle error " + e.getMessage());
        }
        return false;
    }
}
