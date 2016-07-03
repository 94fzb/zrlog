package com.fzb.common.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class CmdUtil {

    public static int findPidByPort(int port) throws IOException, InterruptedException {
        String cmdStr;
        if (File.separatorChar != '\\') {
            cmdStr = "netstat -anp";
        } else {
            cmdStr = "netstat -atu";
        }
        String content = sendCmd(cmdStr);
        if (content != null && content.length() > 0 && content.split("\n").length > 1) {
            String cons[] = content.split("\n");
            for (int i = 2; i < cons.length; i++) {
                content = cons[i];
                String nContext = "";
                //format
                for (int j = 0; j < content.length(); j++) {
                    if (j > 0) {
                        if (content.charAt(j) == ' ' && content.charAt(j) == content.charAt(j - 1)) {
                            continue;
                        }
                    }
                    nContext += content.charAt(j);
                }

                String strings[] = nContext.trim().split(" ");
                int flag = 1;
                if (RuntimeMessage.getSystemRm() == SystemType.LINUX) {
                    flag = 3;
                }
                if (strings.length <= flag) {
                    continue;
                }
                String[] ipPort = strings[flag].split(":");
                if (ipPort.length >= 2) {
                    Integer tempPort = Integer.parseInt(ipPort[ipPort.length - 1]);
                    if (port == tempPort) {
                        //
                        System.out.println(nContext);
                        String procMsg = strings[6];
                        if (RuntimeMessage.getSystemRm() == SystemType.LINUX && procMsg.contains("/")) {
                            return Integer.parseInt(procMsg.substring(0, strings[6].indexOf("/")));
                        }
                    }
                }
            }
        }
        return -1;
    }

    public static void killProcByPid(int pid) {
        sendCmd("kill -9 ", pid + "");
    }

    public static void killProcByPort(int port) {
        try {
            long start = System.currentTimeMillis();
            int pid = findPidByPort(port);
            if (pid != -1) {
                killProcByPid(pid);
            }
            System.out.println(System.currentTimeMillis() - start);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String sendCmd(String cmd, String... args) {
        InputStream[] in = getCmdInputStream(cmd, args);
        if (in != null) {
            return IOUtil.getStringInputStream(in[0]);
        }
        return "";
    }

    public static InputStream[] getCmdInputStream(String cmd, String... args) {
        Process pr = getProcess(cmd, args);
        BufferedInputStream[] bufferedInputStreams = new BufferedInputStream[2];
        if (pr != null) {
            bufferedInputStreams[0] = new BufferedInputStream(pr.getInputStream());
            bufferedInputStreams[1] = new BufferedInputStream(pr.getErrorStream());
            return bufferedInputStreams;
        }
        return bufferedInputStreams;
    }

    public static Process getProcess(String cmd, Object... args) {
        if (args != null) {
            cmd += " ";
            for (Object str : args) {
                cmd += str + " ";
            }
        }
        Runtime rt = Runtime.getRuntime();
        try {
            return rt.exec(cmd);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) {
        killProcByPort(80);
    }

}
