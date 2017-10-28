package com.zrlog.web.controller.admin.api;

import com.zrlog.common.Constants;
import com.zrlog.service.UploadService;
import com.zrlog.common.response.UploadFileResponse;
import com.zrlog.web.controller.BaseController;
import com.hibegin.common.util.IOUtil;
import com.jfinal.kit.PathKit;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class UploadController extends BaseController {

    public UploadFileResponse index() {
        String uploadFieldName = "imgFile";

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String fileExt = getFile().getFileName().substring(
                getFile(uploadFieldName).getFileName().lastIndexOf(".") + 1)
                .toLowerCase();
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        String uri = Constants.ATTACHED_FOLDER + getPara("dir") + "/"
                + sdf.format(new Date()) + "/" + df.format(new Date()) + "_"
                + new Random().nextInt(1000) + "." + fileExt;
        String finalFilePath = PathKit.getWebRootPath() + uri;

        IOUtil.moveOrCopyFile(PathKit.getWebRootPath() + Constants.ATTACHED_FOLDER + getFile(uploadFieldName).getFileName(), finalFilePath, true);
        UploadFileResponse uploadFileResponse = new UploadFileResponse();
        uploadFileResponse.setError(0);
        uploadFileResponse.setUrl(new UploadService().getCloudUrl(getRequest().getContextPath(), uri, finalFilePath, getRequest()));
        return uploadFileResponse;
    }

    public UploadFileResponse thumbnail() {
        String uploadFieldName = "imgFile";

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String fileExt = getFile().getFileName().substring(
                getFile(uploadFieldName).getFileName().lastIndexOf(".") + 1)
                .toLowerCase();
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        String uri = Constants.ATTACHED_FOLDER + getPara("dir") + "/"
                + sdf.format(new Date()) + "/" + df.format(new Date()) + "_"
                + new Random().nextInt(1000) + "." + fileExt;
        String finalFilePath = PathKit.getWebRootPath() + uri;

        IOUtil.moveOrCopyFile(PathKit.getWebRootPath() + Constants.ATTACHED_FOLDER + getFile(uploadFieldName).getFileName(), finalFilePath, true);
        UploadFileResponse uploadFileResponse = new UploadFileResponse();
        uploadFileResponse.setError(0);
        uploadFileResponse.setUrl(new UploadService().getCloudUrl(getRequest().getContextPath(), uri, finalFilePath, getRequest()));
        return uploadFileResponse;
    }


}
