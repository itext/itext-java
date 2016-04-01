package com.itextpdf.io.util;

import java.io.File;

public final class FileUtil {

    private FileUtil() {
    }

    public static String getFontsDir() {
        String winDir = System.getenv("windir");
        String fileSeparator = System.getProperty("file.separator");
        return winDir + fileSeparator + "fonts";
    }

    public static String getFileName(String file) {
        return new File(file).getName();
    }

    public static boolean fileExists(String path) {
        if (path != null) {
            File f = new File(path);
            return f.exists() && f.isFile();
        }
        return false;
    }

    public static boolean directoryExists(String path) {
        if (path != null) {
            File f = new File(path);
            return f.exists() && f.isDirectory();
        }
        return false;
    }

    public static boolean isDirectory(String path) {
        return new File(path).isDirectory();
    }

    public static String[] getDirectoryList(String path) {
        if (path != null) {
            File f = new File(path);
            if (f.exists() && f.isDirectory()) {
                File[] files = f.listFiles();
                if (files == null || files.length == 0) {
                    return null;
                }
                String[] list = new String[files.length];
                for (int i = 0; i < files.length; i++) {
                    list[i] = files[i].getAbsolutePath();
                }
                return list;
            }
        }
        return null;
    }
}
