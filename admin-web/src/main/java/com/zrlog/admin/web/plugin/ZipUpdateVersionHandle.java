package com.zrlog.admin.web.plugin;

import com.hibegin.common.util.ZipUtil;
import com.hibegin.http.server.util.PathUtil;
import com.zrlog.common.Constants;
import com.zrlog.common.Updater;
import com.zrlog.common.vo.Version;

import java.io.File;
import java.io.Serializable;
import java.util.*;

/**
 * 更新ZrLog，具体的流程看 run() 里面有详细流程
 */
public class ZipUpdateVersionHandle implements Serializable, UpdateVersionHandler {

    private String message = "";
    private final File file;
    private boolean finish;
    private final Map<String, Object> backendRes;
    private final Version upgradeVersion;

    public ZipUpdateVersionHandle(File file, Map<String, Object> backendRes, Version upgradeVersion) {
        this.file = file;
        this.backendRes = backendRes;
        this.upgradeVersion = upgradeVersion;
    }

    /**
     * 提示更新进度
     */
    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public boolean isFinish() {
        return finish;
    }

    /**
     * 修改文件的属性，让其可以快速执行
     *
     * @param sb
     */
    private void changeExecFile(StringBuilder sb) {
        //非 unix，无法运行 chmod
        if (!Objects.equals(File.separator, "/")) {
            return;
        }
        List<String> execFiles = new ArrayList<>();
        execFiles.add(PathUtil.getRootPath() + "/bin/start.sh");
        execFiles.add(PathUtil.getRootPath() + "/bin/run.sh");
        execFiles.add(PathUtil.getRootPath() + "/bin/start.bat");
        for (String execFile : execFiles) {
            try {
                Process process = Runtime.getRuntime().exec(Arrays.asList("chmod", "a+x", execFile).toArray(new String[0]));
                process.waitFor();
            } catch (Exception e) {
                sb.append("chmod error ").append(execFile).append("<br/>");
            }
        }
    }

    @Override
    public void doHandle() {
        StringBuilder sb = new StringBuilder();
        sb.append("- ").append(backendRes.get("upgradeUnzipping")).append(" ").append(file.getName()).append("\n");
        try {
            ZipUtil.unZip(file.toString(), Constants.zrLogConfig.getUpdater().getUnzipPath());
        } catch (Exception e) {
            sb.append("- ").append(backendRes.get("unzipError")).append(" ").append(e.getMessage());
            message = sb.toString();
            return;
        }
        try {
            changeExecFile(sb);
            sb.append("- ").append(backendRes.get("upgradeUnzipped")).append("\n");
            sb.append("- ").append(backendRes.get("upgradeRestarting")).append("\n");
            message = sb.toString();
            if (finish) {
                return;
            }
            Updater updater = Constants.zrLogConfig.getUpdater();
            updater.restartProcessAsync(upgradeVersion);
            finish = true;
        } catch (Exception e) {
            sb.append("- ").append(backendRes.get("upgradeError")).append(" ").append(e.getMessage());
            message = sb.toString();
        } finally {
            file.delete();
        }
    }

}
