package com.hibegin.common.util;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FileUtils {

    private static final Logger LOGGER = LoggerUtil.getLogger(FileUtils.class);

    public static void getAllFiles(String path, List<File> files) {
        getAllFilesBySuffix(path, null, files);
    }

    public static void getAllFilesBySuffix(String path, String suffix, List<File> files) {
        File file = new File(path);
        if (file.isDirectory()) {
            File[] fs = file.listFiles();
            if (fs != null) {
                for (File file2 : fs) {
                    if (file2.isDirectory()) {
                        getAllFilesBySuffix(file2.getPath(), suffix, files);
                    } else {
                        if (suffix != null) {
                            if (file2.toString().endsWith(suffix)) {
                                files.add(file2);
                            }
                        } else {
                            files.add(file2);
                        }
                    }
                }
            }
        } else {
            if (suffix != null) {
                if (file.toString().endsWith(suffix)) {
                    files.add(file);
                }
            } else {
                files.add(file);
            }
        }
    }

    public static void moveOrCopyFolder(String dest, String targetFolder, boolean isMove) {
        File f = new File(dest);
        if (f.isDirectory()) {
            File fs[] = new File(dest).listFiles();
            targetFolder = targetFolder + File.separator + f.getName();
            new File(targetFolder).mkdirs();
            if (fs != null) {
                for (File fl : fs) {
                    if (fl.isDirectory()) {
                        moveOrCopyFolder(fl.toString(), targetFolder, isMove);
                    } else {
                        moveOrCopyFile(fl.toString(), targetFolder + File.separator + fl.getName(), isMove);
                    }
                }
            }
        } else {
            moveOrCopyFile(f.toString(), targetFolder + File.separator + f.getName(), isMove);
        }
    }


    public static void moveOrCopyFile(String destFile, String targetFile, boolean isMove) {
        if (isMove) {
            File dest = new File(targetFile);
            dest.getParentFile().mkdirs();
            File srcFile = new File(destFile);
            srcFile.renameTo(dest);
            if (srcFile.exists()) {
                srcFile.delete();
            }
        } else {
            try {
                File f = new File(destFile);
                FileInputStream in = new FileInputStream(f);
                new File(targetFile).getParentFile().mkdirs();
                FileOutputStream out = new FileOutputStream(targetFile);
                // 小于1M(大小根据自己的情况而定)的文件直接一次性写入。
                byte[] b = new byte[1024];
                int length = 0; // 出来cnt次后 文件 跳出循环
                while ((length = in.read(b)) != -1) {
                    out.write(b, 0, length);
                }
                // 一定要记得关闭流额。 不然其他程序那个文件无法进行操作
                in.close();
                out.close();
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "copy file " + destFile + " to " + targetFile + " error", e);
            }
        }

    }

    public static boolean deleteFile(String file) {
        File f = new File(file);
        if (f.isDirectory()) {
            deleteDir(file);
            return !f.exists();
        } else {
            return new File(file).delete();
        }
    }

    private static void deleteDir(String filer) {
        File f = new File(filer);
        if (f.isDirectory()) {
            File fs[] = new File(filer).listFiles();
            if (fs != null && fs.length > 0) {
                for (File fl : fs) {
                    if (fl.isDirectory()) {
                        deleteDir(fl.toString());
                    } else {
                        fl.delete();
                    }
                }
            }
        }
        f.delete();
    }


    public static void tryResizeDiskSpace(String path, long currentLength, long maxLength) {
        List<File> fileList = new ArrayList<>();
        FileUtils.getAllFiles(path, fileList);
        long totalSize = currentLength;
        for (File tFile : fileList) {
            totalSize += tFile.length();
        }
        if (totalSize >= maxLength) {
            Collections.sort(fileList, new Comparator<File>() {
                @Override
                public int compare(File o1, File o2) {
                    return Long.compare(o1.lastModified(), o2.lastModified());
                }
            });
            long needRemoveSize = totalSize - maxLength;
            for (File tFile : fileList) {
                needRemoveSize -= tFile.length();
                tFile.delete();
                if (needRemoveSize <= 0) {
                    break;
                }
            }
        }
    }

    public static void main(String[] args) {
        moveOrCopyFolder("/home/xiaochun/0.jpg", "/home/Public/", true);
    }
}
