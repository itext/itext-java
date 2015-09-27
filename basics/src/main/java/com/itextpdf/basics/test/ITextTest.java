package com.itextpdf.basics.test;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public abstract class ITextTest {

    protected final Logger LOGGER = LoggerFactory.getLogger(getClass());

    public static void createDestinationFolder(String path) {

        File fpath = new File(path);
        fpath.mkdirs();
    }

    public static void createOrClearDestinationFolder(String path) {
        File fpath = new File(path);
        fpath.mkdirs();
        for (File file : fpath.listFiles())
            file.delete();
    }

    public static void deleteDirectory(String path) {

        File fpath = new File(path);
        if (fpath.exists() && fpath.listFiles() != null) {
            for (File f : fpath.listFiles()) {
                if (f.isDirectory()) {
                    deleteDirectory(f.getPath());
                    f.delete();
                } else {
                    f.delete();
                }
            }
            fpath.delete();
        }
    }

    protected byte[] readFile(String filename) throws IOException {

        FileInputStream input = new FileInputStream(filename);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        byte[] buffer = new byte[8192];
        int read;
        while ((read = input.read(buffer)) != -1) {
            output.write(buffer, 0, read);
        }
        input.close();
        return output.toByteArray();
    }

    protected String createStringByEscaped(byte[] bytes) {

        String[] chars = (new String(bytes)).substring(1).split("#");
        StringBuilder buf = new StringBuilder(chars.length);
        for (String ch : chars) {
            if (ch.length() == 0) continue;
            Integer b = Integer.parseInt(ch, 16);
            buf.append((char) b.intValue());
        }
        return buf.toString();
    }

}
