package com.zrlog.admin.web.plugin;

import com.hibegin.common.util.ZipUtil;
import com.hibegin.http.server.util.PathUtil;
import com.zrlog.common.Constants;
import com.zrlog.util.JarUpdater;

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

    public ZipUpdateVersionHandle(File file, Map<String, Object> backendRes) {
        this.file = file;
        this.backendRes = backendRes;
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
        sb.append(backendRes.get("upgradeUnzipping")).append(" ").append(file.getName()).append("<br/>");
        try {
            ZipUtil.unZip(file.toString(), PathUtil.getRootPath());
        } catch (Exception e) {
            sb.append(backendRes.get("unzipError")).append(" ").append(e.getMessage());
            message = sb.toString();
            return;
        }
        try {
            changeExecFile(sb);
            sb.append(backendRes.get("upgradeUnzipped")).append("<br/>");
            sb.append(backendRes.get("upgradeRestarting")).append("<br/>");
            message = sb.toString();
            if (finish) {
                return;
            }
            JarUpdater jarUpdater = Constants.zrLogConfig.getJarUpdater();
            jarUpdater.restartJarAsync();
            finish = true;
        } catch (Exception e) {
            sb.append(backendRes.get("upgradeError")).append(" ").append(e.getMessage());
            message = sb.toString();
        } finally {
            file.delete();
        }
    }

}
