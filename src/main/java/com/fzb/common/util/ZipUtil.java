package com.fzb.common.util;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;


public class ZipUtil {

    public static void unZip(String src, String target) throws IOException {
        ZipInputStream zipIn = null;
        ZipFile zip = null;
        try {
            //FIXME zip 文件不能有中文
            zipIn = new ZipInputStream(new FileInputStream(src));
            ZipEntry in;
            zip = new ZipFile(src);
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
            zip.close();
        } finally {
            if (zipIn != null) {
                zipIn.close();
            }
            if (zip != null) {
                zip.close();
            }
        }
    }

    public static void inZip(List<File> files, String basePath, String target) throws IOException {
        List<File> cfiles = new ArrayList<File>();
        for (File file : files) {
            cfiles.add(file);
        }
        for (File file : cfiles) {
            if (file.isDirectory()) {
                IOUtil.getAllFiles(file.toString(), files);
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
            } else {

            }
        }
        zos.close();

    }

    public static void main(String[] args) {
        try {
            //unZip("E:/putty.zip", "E:/test/");
            List<File> files = new LinkedList<File>();
            files.add(new File("E:/keygen.exe"));
            files.add(new File("E:/xmlpull_1_0_5"));
            inZip(files, "E:/", "E:/test/1.zip");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
