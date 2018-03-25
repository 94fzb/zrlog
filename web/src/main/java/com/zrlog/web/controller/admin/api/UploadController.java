package com.zrlog.web.controller.admin.api;

import com.hibegin.common.util.FileUtils;
import com.hibegin.common.util.IOUtil;
import com.jfinal.kit.PathKit;
import com.zrlog.common.Constants;
import com.zrlog.common.response.UploadFileResponse;
import com.zrlog.service.UploadService;
import com.zrlog.util.ThumbnailUtil;
import com.zrlog.web.controller.BaseController;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class UploadController extends BaseController {

    public UploadFileResponse index() {
        String uploadFieldName = "imgFile";
        String uri = generatorUri(uploadFieldName, "");
        String finalFilePath = PathKit.getWebRootPath() + uri;
        FileUtils.moveOrCopyFile(PathKit.getWebRootPath() + Constants.ATTACHED_FOLDER + getFile(uploadFieldName).getFileName(), finalFilePath, true);
        return new UploadService().getCloudUrl(getRequest().getContextPath(), uri, finalFilePath, getRequest());
    }

    private String generatorUri(String uploadFieldName, String alias) {
        String fileExt = getFile().getFileName().substring(getFile(uploadFieldName).getFileName().lastIndexOf(".") + 1).toLowerCase();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        return Constants.ATTACHED_FOLDER + getPara("dir") + "/" + sdf.format(new Date()) + "/" + df.format(new Date()) + "_" + new Random().nextInt(1000) + alias + "." + fileExt;
    }

    public UploadFileResponse thumbnail() throws IOException {
        String uploadFieldName = "imgFile";
        String uri = generatorUri(uploadFieldName, "_thumbnail");
        File imgFile = getFile(uploadFieldName).getFile();
        String finalFilePath = PathKit.getWebRootPath() + uri;
        File thumbnailFile = new File(finalFilePath);
        int height = -1;
        int width = -1;
        byte[] bytes = IOUtil.getByteByInputStream(new FileInputStream(imgFile));
        if (!thumbnailFile.getParentFile().exists()) {
            thumbnailFile.getParentFile().mkdirs();
        }
        if (!".gif".endsWith(uri)) {
            IOUtil.writeBytesToFile(ThumbnailUtil.jpeg(bytes, 1f), thumbnailFile);
            BufferedImage bimg = ImageIO.read(thumbnailFile);
            height = bimg.getHeight();
            width = bimg.getWidth();
        } else {
            IOUtil.writeBytesToFile(bytes, thumbnailFile);
        }
        FileUtils.moveOrCopyFile(thumbnailFile.toString(), finalFilePath, true);
        UploadFileResponse uploadFileResponse = new UploadService().getCloudUrl(getRequest().getContextPath(), uri, finalFilePath, getRequest());
        uploadFileResponse.setUrl(uploadFileResponse.getUrl() + "?h=" + height + "&w=" + width);
        return uploadFileResponse;
    }

}
