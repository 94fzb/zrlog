package com.zrlog.admin.web.controller.api;

import com.hibegin.common.util.FileUtils;
import com.hibegin.common.util.IOUtil;
import com.hibegin.http.annotation.ResponseBody;
import com.hibegin.http.server.util.PathUtil;
import com.hibegin.http.server.web.Controller;
import com.zrlog.admin.business.exception.ArgsException;
import com.zrlog.admin.business.rest.response.UploadFileResponse;
import com.zrlog.admin.business.service.UploadService;
import com.zrlog.admin.web.token.AdminTokenThreadLocal;
import com.zrlog.common.rest.response.ApiStandardResponse;
import com.zrlog.admin.util.UploadFileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class UploadController extends Controller {

    //private static final Logger LOGGER = LoggerUtil.getLogger(UploadController.class);

    @ResponseBody
    public ApiStandardResponse<UploadFileResponse> index() {
        String uploadFieldName = "imgFile";
        File imgFile = request.getFile(uploadFieldName);
        if (imgFile == null || !imgFile.exists()) {
            throw new ArgsException("imgFile");
        }
        String uri = UploadFileUtils.generatorUri(uploadFieldName, request);
        String finalFilePath = PathUtil.getStaticPath() + uri;
        FileUtils.moveOrCopyFile(imgFile.toString(), finalFilePath, true);
        return new ApiStandardResponse<>(new UploadService().getCloudUrl("", uri, finalFilePath, getRequest(), AdminTokenThreadLocal.getUser()));
    }


    @ResponseBody
    public ApiStandardResponse<UploadFileResponse> thumbnail() throws IOException {
        String uploadFieldName = "imgFile";
        File tempImgFile = request.getFile(uploadFieldName);
        if (tempImgFile == null || !tempImgFile.exists()) {
            throw new ArgsException("imgFile");
        }
        String uri = UploadFileUtils.generatorUri(uploadFieldName, request);
        try {
            String finalFilePath = PathUtil.getStaticPath() + uri;
            byte[] bytes = IOUtil.getByteByInputStream(new FileInputStream(tempImgFile));
            File thumbnailFile = new File(finalFilePath);
            if (!thumbnailFile.getParentFile().exists()) {
                thumbnailFile.getParentFile().mkdirs();
            }
            int height = -1;
            int width = -1;
            //copy file
            IOUtil.writeBytesToFile(bytes, thumbnailFile);
            UploadFileResponse uploadFileResponse = new UploadService().getCloudUrl("", uri, finalFilePath, getRequest(), AdminTokenThreadLocal.getUser());
            return new ApiStandardResponse<>(new UploadFileResponse(uploadFileResponse.url() + "?h=" + height + "&w=" + width));
        } finally {
            tempImgFile.delete();
        }
    }

}
