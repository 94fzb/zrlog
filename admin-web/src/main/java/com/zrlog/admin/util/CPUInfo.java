package com.zrlog.admin.util;

import com.hibegin.common.util.EnvKit;
import com.hibegin.common.util.LoggerUtil;
import com.hibegin.common.util.ObjectUtil;
import com.sun.management.OperatingSystemMXBean;
import com.zrlog.common.Constants;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CPUInfo {

    private static final Logger LOGGER = LoggerUtil.getLogger(CPUInfo.class);
    private static final CPUInfo instance = new CPUInfo();
    private String cpuModel;

    private CPUInfo() {
        try {
            this.cpuModel = readCPUModel();
        } catch (Exception e) {
            if (Constants.debugLoggerPrintAble()) {
                LOGGER.log(Level.SEVERE, e.getMessage(), e);
            }
            this.cpuModel = "unknown";
        }
    }

    public static CPUInfo getInstance() {
        return instance;
    }

    public static void main(String[] args) {
        System.out.println("CPU Model: " + instance.getCpuModel());
    }

    public String getCpuModel() {
        return cpuModel;
    }

    private String readCPUModel() throws Exception {
        if (EnvKit.isLambda()) {
            return ObjectUtil.requireNonNullElse(System.getenv("AWS_EXECUTION_ENV"), "AWS_Lambda_OS");
        }
        String os = System.getProperty("os.name").toLowerCase();
        // Windows 系统
        if (os.contains("win")) {
            String command = "wmic cpu get caption";
            Process process = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("Caption")) {
                    continue; // 忽略标题行
                }
                return line.trim(); // 返回 CPU 型号
            }
        }
        // Linux 系统
        else if (os.contains("nix") || os.contains("nux")) {
            String command = "lscpu";
            Process process = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains("Model name")) {
                    return line.split(":")[1].trim(); // 获取并返回 Model name
                }
            }
        }
        // macOS 系统
        else if (os.contains("mac")) {
            String command = "sysctl -n machdep.cpu.brand_string";
            Process process = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                return line.trim(); // 返回 CPU 型号
            }
        }

        return "Unable to retrieve CPU model"; // 如果无法获取 CPU 型号
    }

    public String getCpuLoad() {
        OperatingSystemMXBean osMXBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        return BigDecimal.valueOf(osMXBean.getSystemCpuLoad()).setScale(2, RoundingMode.HALF_UP).toString();
    }
}