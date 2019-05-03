package com.zrlog.web.controller.admin.api;

import com.hibegin.common.util.FileUtils;
import com.hibegin.common.util.IOUtil;
import com.jfinal.kit.PathKit;
import com.zrlog.common.Constants;
import com.zrlog.common.response.UploadFileResponse;
import com.zrlog.service.UploadService;
import com.zrlog.util.ThumbnailUtil;
import com.zrlog.web.controller.BaseController;
import com.zrlog.web.token.AdminTokenThreadLocal;

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
        String uri = generatorUri(uploadFieldName);
        File imgFile = getFile(uploadFieldName).getFile();
        String finalFilePath = PathKit.getWebRootPath() + uri;
        FileUtils.moveOrCopyFile(imgFile.toString(), finalFilePath, true);
        return new UploadService().getCloudUrl(getRequest().getContextPath(), uri, finalFilePath, getRequest(), AdminTokenThreadLocal.getUser());
    }

    private String generatorUri(String uploadFieldName) {
        String fileExt = getFile().getFileName().substring(getFile(uploadFieldName).getFileName().lastIndexOf(".") + 1).toLowerCase();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        return Constants.ATTACHED_FOLDER + getPara("dir") + "/" + sdf.format(new Date()) + "/" + df.format(new Date()) + "_" + new Random().nextInt(1000) + "." + fileExt;
    }

    public UploadFileResponse thumbnail() throws IOException {
        String uploadFieldName = "imgFile";
        String uri = generatorUri(uploadFieldName);
        File imgFile = getFile(uploadFieldName).getFile();
        String finalFilePath = PathKit.getWebRootPath() + uri;
        byte[] bytes = IOUtil.getByteByInputStream(new FileInputStream(imgFile));
        File thumbnailFile = new File(finalFilePath);
        if (!thumbnailFile.getParentFile().exists()) {
            thumbnailFile.getParentFile().mkdirs();
        }
        int height = -1;
        int width = -1;
        if (!".gif".endsWith(uri)) {
            IOUtil.writeBytesToFile(ThumbnailUtil.jpeg(bytes, 1f), thumbnailFile);
            BufferedImage bimg = ImageIO.read(thumbnailFile);
            height = bimg.getHeight();
            width = bimg.getWidth();
        } else {
            IOUtil.writeBytesToFile(bytes, thumbnailFile);
        }
        UploadFileResponse uploadFileResponse = new UploadService().getCloudUrl(getRequest().getContextPath(), uri, finalFilePath, getRequest(), AdminTokenThreadLocal.getUser());
        uploadFileResponse.setUrl(uploadFileResponse.getUrl() + "?h=" + height + "&w=" + width);
        imgFile.delete();
        return uploadFileResponse;
    }

}
