package com.fzb.common.util;


import org.apache.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

public class JarPackageUtil {

    private static final Logger LOGGER = Logger.getLogger(JarPackageUtil.class);

    public static void inJar(List<File> files, String basePath, String target) throws IOException {
        List<File> tempFiles = new ArrayList<File>();
        for (File file : files) {
            tempFiles.add(file);
        }
        for (File file : tempFiles) {
            if (file.isDirectory()) {
                IOUtil.getAllFiles(file.toString(), files);
            }
        }
        List<File> packageFileList = new ArrayList<File>();
        FileInputStream fileInputStream = null;
        for (File file : files) {
            if (file.toString().contains("META-INF" + File.separator + "MANIFEST.MF")) {
                fileInputStream = new FileInputStream(file);
            } else {
                packageFileList.add(file);
            }
        }
        JarOutputStream jarOutputStream = new JarOutputStream(new FileOutputStream(target), new Manifest(fileInputStream));
        for (File file : packageFileList) {
            JarEntry jarEntry;
            byte[] b = new byte[1024];
            int len;
            if (file.isFile()) {
                String entryName = file.toString().substring(basePath.length()).replace("\\", "/");
                jarEntry = new JarEntry(entryName);
                LOGGER.debug("Adding " + entryName);
                jarOutputStream.putNextEntry(jarEntry);
                InputStream is = new BufferedInputStream(new FileInputStream(file));
                while ((len = is.read(b, 0, b.length)) != -1) {
                    jarOutputStream.write(b, 0, len);
                }
                is.close();
            }
        }
        jarOutputStream.close();
    }

    public static void main(String[] args) {
        try {
            //unZip("E:/putty.zip", "E:/test/");
            List<File> files = new LinkedList<File>();
            files.add(new File("C:\\Users\\xiaochun\\git\\zrlog\\target\\ROOT"));
            inJar(files, "C:\\Users\\xiaochun\\git\\zrlog\\target\\ROOT\\", "C:/7/1.war");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
