package com.hibegin.common.util;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;


public class ZipUtil {

    public static void unZip(String src, String target) throws IOException {
        ZipEntry in;
        try (ZipInputStream zipIn = new ZipInputStream(new FileInputStream(src)); ZipFile zip = new ZipFile(src)) {
            //FIXME zip 文件不能有中文
            while ((in = zipIn.getNextEntry()) != null) {
                File file = new File(target + in.getName());
                if (in.getName().endsWith("/")) {
                    file.mkdirs();
                } else {
                    byte[] b = IOUtil.getByteByInputStream(zip.getInputStream(in));
                    if (!new File(file.getParent()).exists()) {
                        new File(file.getParent()).mkdirs();
                    }
                    FileOutputStream fout = new FileOutputStream(file);
                    fout.write(b);
                    fout.close();
                }
            }
        }
    }

    public static void inZip(List<File> files, String basePath, String target) throws IOException {
        List<File> cfiles = new ArrayList<>(files);
        for (File file : cfiles) {
            if (file.isDirectory()) {
                FileUtils.getAllFiles(file.toString(), files);
            }
        }
        ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(target));
        for (File file : files) {
            ZipEntry entry;
            byte[] b = new byte[1024];
            int len;
            if (file.isFile()) {
                entry = new ZipEntry(file.toString().substring(basePath.length()));
                zos.putNextEntry(entry);
                InputStream is = new BufferedInputStream(new FileInputStream(file));
                while ((len = is.read(b, 0, b.length)) != -1) {
                    zos.write(b, 0, len);
                }
                is.close();
            }
        }
        zos.close();
    }
}
