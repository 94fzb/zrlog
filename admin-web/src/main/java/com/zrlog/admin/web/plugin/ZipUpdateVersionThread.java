package com.zrlog.admin.web.plugin;

import com.hibegin.common.util.ZipUtil;
import com.hibegin.http.server.util.PathUtil;
import com.zrlog.util.JarUpdater;

import java.io.File;
import java.io.Serializable;
import java.util.Map;
import java.util.Objects;

/**
 * 更新ZrLog，具体的流程看 run() 里面有详细流程
 */
public class ZipUpdateVersionThread extends Thread implements Serializable, UpdateVersionHandler {

    private String message = "";
    private final File file;
    private boolean finish;
    private final Map<String, Object> backendRes;
    public static JarUpdater jarUpdater;
    private boolean unzipped;

    public ZipUpdateVersionThread(File file, Map<String, Object> backendRes) {
        this.file = file;
        this.backendRes = backendRes;
    }

    @Override
    public void run() {
        StringBuilder sb = new StringBuilder();
        sb.append(backendRes.get("upgradeUnzipping")).append(" ").append(file.getName()).append("<br/>");
        try {
            if (!unzipped) {
                ZipUtil.unZip(file.toString(), PathUtil.getRootPath());
                unzipped = true;
            }
            sb.append(backendRes.get("upgradeUnzipped")).append("<br/>");
            sb.append(Objects.nonNull(jarUpdater) ? backendRes.get("upgradeRestarting") : "当前环境不支持自动重启，请手动重启程序，以完成升级");
            message = sb.toString();
            if (finish) {
                return;
            }
            if (Objects.nonNull(jarUpdater)) {
                jarUpdater.restartJarAsync();
            }
            finish = true;
        } catch (Exception e) {
            message = "更新异常 " + e.getMessage();
        }
    }

    /**
     * 提示更新进度
     *
     * @return
     */
    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public boolean isFinish() {
        return finish;
    }

}
