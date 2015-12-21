package com.fzb.io.yunstore;

import com.fzb.io.api.FileManageAPI;
import com.qiniu.api.auth.AuthException;
import com.qiniu.api.auth.digest.Mac;
import com.qiniu.api.fop.ImageInfo;
import com.qiniu.api.fop.ImageInfoRet;
import com.qiniu.api.io.IoApi;
import com.qiniu.api.io.PutExtra;
import com.qiniu.api.io.PutRet;
import com.qiniu.api.net.CallRet;
import com.qiniu.api.rs.PutPolicy;
import com.qiniu.api.rs.RSClient;
import org.json.JSONException;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class QiniuBucketManageImpl implements FileManageAPI {

    private Map<String, Object> responseData = new HashMap<String, Object>();

    private BucketVO bucket;

    public QiniuBucketManageImpl(BucketVO bucket) {
        this.bucket = bucket;
    }

    @Override
    public Map<String, Object> delFile(String file) {
        Mac mac = new Mac(bucket.getAccessKey(), bucket.getSecretKey());
        RSClient client = new RSClient(mac);
        CallRet cr = client.delete(bucket.getBucketName(), file);
        responseData.put("statusCode", cr.statusCode);
        responseData.put("resp", cr.getResponse());
        return responseData;
    }

    @Override
    @Deprecated
    public Map<String, Object> delFolder(String folder) {
        return null;
    }

    @Override
    public Map<String, Object> create(File file) {
        // 生成一个新的文件名称  。不是太方便
        //String key = ParseTools.getRandomFileNameByOld(file).getName();
        return create(file, null);
    }

    @Override
    public Map<String, Object> create(File file, String key) {
        PutExtra extra = new PutExtra();
        try {
            PutRet ret = IoApi.putFile(getToken(), key.substring(1), file, extra);
            responseData.put("statusCode", ret.getStatusCode());
            String url = "http://" + bucket.getHost() + key;
            ImageInfoRet infoRet = ImageInfo.call(url);
            if (infoRet.width > 600) {
                url += "?imageView2/2/w/600";
            }
            responseData.put("url", url);
            return responseData;
        } catch (AuthException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Map<String, Object> moveOrCopy(String filer, String tag,
                                          boolean isMove) {
        return null;
    }

    @Override
    public Map<String, Object> moveOrCopyFile(String src, String tag,
                                              boolean isMove) {
        return null;
    }

    @Override
    public Map<String, Object> CopyFileByInStream(InputStream in, String tag) {
        return null;
    }

    @Override
    public Map<String, Object> modifyFile(String root, String code,
                                          String content) {
        return null;
    }

    @Override
    public Map<String, Object> getFileList(String folder) {
        return null;
    }

    private String getToken() throws AuthException, JSONException {
        Mac mac = new Mac(bucket.getAccessKey(), bucket.getSecretKey());
        // 请确保该bucket已经存在
        PutPolicy putPolicy = new PutPolicy(bucket.getBucketName());
        return putPolicy.token(mac);
    }

}
