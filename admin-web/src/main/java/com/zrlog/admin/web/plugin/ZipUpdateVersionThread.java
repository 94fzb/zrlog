package com.zrlog.admin.web.plugin;

import com.zrlog.util.ZrLogUtil;

import java.io.File;
import java.io.Serializable;

/**
 * 更新Zrlog，具体的流程看 run() 里面有详细流程
 */
public class ZipUpdateVersionThread extends Thread implements Serializable, UpdateVersionHandler {

    private final StringBuilder sb = new StringBuilder();

    public ZipUpdateVersionThread(File file) {
    }

    @Override
    public void run() {
        if (ZrLogUtil.isDockerMode()) {
            sb.append("Docker mode, Please use `sh upgrade.sh` to upgrade");
        } else {
            sb.append("NotImplement");
        }
    }

    /**
     * 提示更新进度
     *
     * @return
     */
    @Override
    public String getMessage() {
        return sb.toString();
    }

    @Override
    public boolean isFinish() {
        return false;
    }

}
