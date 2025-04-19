package com.zrlog.admin.util;

import com.hibegin.common.util.SecurityUtils;
import com.hibegin.http.server.api.HttpRequest;
import com.zrlog.common.Constants;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.StringJoiner;

public class UploadFileUtils {

    public static String generatorUri(String uploadFieldName, HttpRequest request) {
        File file = request.getFile(uploadFieldName);
        if (Objects.isNull(file)) {
            return "";
        }
        StringJoiner joiner = new StringJoiner(".");
        joiner.add(SecurityUtils.md5ByFile(file));
        String fileExt = request.getFile(uploadFieldName).getName().substring(file.getName().lastIndexOf(".") + 1).toLowerCase();
        if (!fileExt.trim().isEmpty()) {
            joiner.add(fileExt);
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        return Constants.ATTACHED_FOLDER + request.getParaToStr("dir") + "/" + sdf.format(new Date()) + "/" + joiner;
    }

}
