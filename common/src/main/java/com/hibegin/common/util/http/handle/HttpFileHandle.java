package com.hibegin.common.util.http.handle;


import com.hibegin.common.util.LoggerUtil;
import com.hibegin.common.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Objects;
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
            String[] split = uriPath.split("/");
            return split[split.length - 1];
        }
        return fileName;
    }

    private static String getFileNameByHeader(String header) {
        if (Objects.nonNull(header) && header.startsWith("attachment;")) {
            String s = header.substring("attachment;".length()).trim();
            if (s.startsWith("filename=\"") && s.endsWith("\"")) {
                return s.substring("filename=\"".length(), s.length() - 1); // 带引号
            } else if (s.startsWith("filename=")) {
                return s.substring("filename=".length()); // 不带引号
            }
        }
        return null;
    }

    public static void main(String[] args) {
        String fileName = getFileNameByHeader("attachment;filename=1.png");
        System.out.println("fileName = " + fileName);
    }

    @Override
    public boolean handle(HttpRequest request, HttpResponse<InputStream> response) {
        String contentDis = response.headers().firstValue("Content-Disposition").orElse(null);
        if (Objects.nonNull(contentDis)) {
            String fileName = getFileNameByHeader(contentDis);
            if (Objects.nonNull(fileName)) {
                setT(new File(fileName + "/" + fileName));
            }
        }
        if (getT() == null) {
            setT(new File(filePath + "/" + getFileName(request.uri().getPath())));
        }
        try {
            if (!getT().getParentFile().exists()) {
                getT().getParentFile().mkdirs();
            }
            if (response.statusCode() == 200) {
                Files.copy(response.body(), Paths.get(getT().toString()), StandardCopyOption.REPLACE_EXISTING);
            } else {
                LOGGER.warning("FileHandle " + request.uri() + " status error " + response.statusCode());
            }
        } catch (IOException e) {
            LOGGER.warning("FileHandle error " + e.getMessage());
        }
        return false;
    }
}
