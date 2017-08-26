package com.fzb.blog.web.controller.admin.api;

import com.fzb.blog.common.response.UploadFileResponse;
import com.fzb.blog.service.UploadService;
import com.fzb.blog.web.controller.BaseController;
import com.fzb.common.util.IOUtil;
import com.jfinal.kit.PathKit;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import static com.fzb.blog.common.Constants.ATTACHED_FOLDER;

public class UploadController extends BaseController {

    public UploadFileResponse index() {
        String uploadFieldName = "imgFile";

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String fileExt = getFile().getFileName().substring(
                getFile(uploadFieldName).getFileName().lastIndexOf(".") + 1)
                .toLowerCase();
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        String uri = ATTACHED_FOLDER + getPara("dir") + "/"
                + sdf.format(new Date()) + "/" + df.format(new Date()) + "_"
                + new Random().nextInt(1000) + "." + fileExt;
        String finalFilePath = PathKit.getWebRootPath() + uri;

        IOUtil.moveOrCopyFile(PathKit.getWebRootPath() + ATTACHED_FOLDER + getFile(uploadFieldName).getFileName(), finalFilePath, true);
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
        String uri = ATTACHED_FOLDER + getPara("dir") + "/"
                + sdf.format(new Date()) + "/" + df.format(new Date()) + "_"
                + new Random().nextInt(1000) + "." + fileExt;
        String finalFilePath = PathKit.getWebRootPath() + uri;

        IOUtil.moveOrCopyFile(PathKit.getWebRootPath() + ATTACHED_FOLDER + getFile(uploadFieldName).getFileName(), finalFilePath, true);
        UploadFileResponse uploadFileResponse = new UploadFileResponse();
        uploadFileResponse.setError(0);
        uploadFileResponse.setUrl(new UploadService().getCloudUrl(getRequest().getContextPath(), uri, finalFilePath, getRequest()));
        return uploadFileResponse;
    }


}
